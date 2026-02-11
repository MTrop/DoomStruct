/*******************************************************************************
 * Copyright (c) 2015-2025 Matt Tropiano
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.text;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.mtrop.doom.object.TextObject;
import net.mtrop.doom.struct.Lexer;
import net.mtrop.doom.struct.Lexer.Kernel;
import net.mtrop.doom.struct.Lexer.Parser;
import net.mtrop.doom.text.data.MapInfoData;
import net.mtrop.doom.text.data.MapInfoData.Value;

/**
 * An abstraction of the Eternity Map Info entry (EMAPINFO).
 * Entry headers are stored case-insensitively, for convenience.
 * @author Matthew Tropiano
 * @since 2.19.0
 */
public class EternityMapInfo implements TextObject, Iterable<Map.Entry<String, MapInfoData[]>>
{
	/** The map of header to info data entries. */
	private Map<String, MapInfoData[]> entryMap;

	public EternityMapInfo()
	{
		this.entryMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	}
	
	/**
	 * Adds a new entry to this EMAPINFO.
	 * @param header the header name (without brackets).
	 * @param data the contained data entries.
	 */
	public void addEntry(String header, MapInfoData ... data)
	{
		MapInfoData[] entries = new MapInfoData[data.length];
		System.arraycopy(data, 0, entries, 0, data.length);
		entryMap.put(header, entries);
	}
	
	/**
	 * Checks if this contains an entry by name.
	 * @param header the header name.
	 * @return true if so, false if not.
	 */
	public boolean containsEntry(String header)
	{
		return entryMap.containsKey(header);
	}
	
	/**
	 * Fetches an entry from this map info by map header.
	 * @param header the header name.
	 * @return the corresponding data, or null if no data.
	 * @since 2.19.2
	 */
	public MapInfoData[] getEntry(String header)
	{
		return entryMap.get(header);
	}
	
	/**
	 * Removes an entry from this map info and returns the associated data, if it existed.
	 * @param header the entry name to remove.
	 * @return the associated data from the removed entry, or null if no corresponding entry.
	 */
	public MapInfoData[] removeEntry(String header)
	{
		return entryMap.remove(header);
	}
	
	@Override
	public Iterator<Entry<String, MapInfoData[]>> iterator() 
	{
		return entryMap.entrySet().iterator();
	}
	
	@Override
	public void readText(Reader reader) throws IOException 
	{
		entryMap.clear();
		(new InfoParser(reader)).parseInto(this);
	}

	@Override
	public void writeText(Writer writer) throws IOException 
	{
		for (Map.Entry<String, MapInfoData[]> entry : this)
		{
			writer.append('[').append(entry.getKey().toUpperCase()).append(']').append('\n');
			
			for (MapInfoData infoData : entry.getValue())
			{
				writer.append('\t');
				writer.append(infoData.getName());
				writer.append(" = ");
				boolean first = true;
				for (Value value : infoData.getValues())
				{
					if (!first)
						writer.append(" ");
					writer.append(value.getToken());
					first = false;
				}
				writer.append('\n');
			}
			
			writer.append('\n');
		}
	}

	private static class InfoKernel extends Kernel
	{
		private static final int TYPE_LBRACK = 1;
		private static final int TYPE_RBRACK = 2;
		private static final int TYPE_EQUAL = 3;

		private InfoKernel()
		{
			setDecimalSeparator('.');
			
			addCommentLineDelimiter("//");
			addCommentLineDelimiter("#");
			addCommentLineDelimiter(";");
			
			addDelimiter("[", TYPE_LBRACK);
			addDelimiter("]", TYPE_RBRACK);
			addDelimiter("=", TYPE_EQUAL);
			
			setEmitNewlines(true);
		}
	}
	
	private static class InfoParser extends Parser
	{
		private InfoParser(Reader in)
		{
			super(new Lexer(new InfoKernel(), in));
		}
		
		private void parseInto(EternityMapInfo mapInfo) throws ParseException
		{
			nextToken();
			while (currentToken() != null)
			{
				parseValueSet(mapInfo);
			}
		}

		private void eatNewLines()
		{
			while(matchType(InfoKernel.TYPE_NEWLINE)) ;
		}
		
		private void parseValueSet(EternityMapInfo mapInfo) throws ParseException
		{
			eatNewLines();
			
			if (currentToken() == null)
			{
				// EOS. Done.
			}
			else if (matchType(InfoKernel.TYPE_LBRACK))
			{
				String header = currentLexeme();
				if (header == null)
					throw new ParseException(getTokenInfoLine("Expected header name. End-of-stream reached."));
				nextToken();
				
				if (!matchType(InfoKernel.TYPE_RBRACK))
				{
					throw new ParseException(getTokenInfoLine("Expected ']' to end header name."));
				}

				if (!matchType(InfoKernel.TYPE_NEWLINE))
				{
					throw new ParseException(getTokenInfoLine("Expected end-of-line."));
				}
				
				mapInfo.addEntry(header, parseValueBody());
			}
			else
			{
				throw new ParseException(getTokenInfoLine("Expected '[' to start header name."));
			}
		}
		
		private MapInfoData[] parseValueBody() throws ParseException
		{
			List<MapInfoData> dataList = new LinkedList<>();
			
			while (currentType(InfoKernel.TYPE_IDENTIFIER))
			{
				String name = currentLexeme();
				nextToken();
				
				if (matchType(InfoKernel.TYPE_EQUAL))
				{
					// Eat equals - optional.
				}
				
				List<Value> valueList = new LinkedList<>(); 
				
				while (!matchType(InfoKernel.TYPE_NEWLINE))
				{
					if (!currentType(InfoKernel.TYPE_IDENTIFIER, InfoKernel.TYPE_NUMBER, InfoKernel.TYPE_STRING))
						throw new ParseException(getTokenInfoLine("Expected value."));

					if (currentType(InfoKernel.TYPE_STRING))
						valueList.add(Value.create(currentLexeme()));
					else if (currentType(InfoKernel.TYPE_IDENTIFIER))
						valueList.add(Value.create(currentLexeme(), true));
					else if (currentType(InfoKernel.TYPE_NUMBER))
						valueList.add(Value.create(parseNumber()));
					
					nextToken();
				}
				
				eatNewLines();
				
				dataList.add(new MapInfoData(name, valueList.toArray(new Value[valueList.size()])));
			}
			
			return dataList.toArray(new MapInfoData[dataList.size()]);
		}
		
		private Number parseNumber()
		{
			Number currentValue;
			String lexeme = currentLexeme();

			if (lexeme.startsWith("0X") || lexeme.startsWith("0x"))
			{
				currentValue = Integer.parseInt(lexeme.substring(2), 16);
				return currentValue;
			}
			else if (lexeme.contains("."))
			{
				currentValue = Double.parseDouble(lexeme);
				return currentValue;
			}
			else
			{
				currentValue = Integer.parseInt(lexeme);
				return currentValue;
			}
		}
		
	}
	
}
