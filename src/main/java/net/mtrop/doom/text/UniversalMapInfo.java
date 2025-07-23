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
import java.util.TreeMap;
import java.util.Map.Entry;

import net.mtrop.doom.object.TextObject;
import net.mtrop.doom.struct.Lexer;
import net.mtrop.doom.struct.Lexer.Kernel;
import net.mtrop.doom.struct.Lexer.Parser;
import net.mtrop.doom.text.data.MapInfoData;
import net.mtrop.doom.text.data.MapInfoData.Value;

/**
 * An abstraction of the Universal Map Info entry (UMAPINFO).
 * Entry headers are stored case-insensitively, for convenience.
 * @author Matthew Tropiano
 * @since [NOW]
 */
public class UniversalMapInfo implements TextObject, Iterable<Map.Entry<String, MapInfoData[]>> 
{
	/** The map of header to info data entries. */
	private Map<String, MapInfoData[]> entryMap;

	public UniversalMapInfo() 
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
			writer.append("map ").append(entry.getKey().toUpperCase()).append('\n');
			
			writer.append('{').append('\n');
			for (MapInfoData infoData : entry.getValue())
			{
				writer.append('\t');
				writer.append(infoData.getName());
				writer.append(" = ");
				boolean first = true;
				for (Value value : infoData.getValues())
				{
					if (!first)
						writer.append(", ");
					writer.append(value.toString());
					first = false;
				}
				writer.append('\n');
			}
			
			writer.append("}\n\n");
		}
	}

	private static class InfoKernel extends Kernel
	{
		private static final int TYPE_LBRACE = 1;
		private static final int TYPE_RBRACE = 2;
		private static final int TYPE_EQUAL = 3;
		private static final int TYPE_COMMA = 4;

		private static final int TYPE_MAP = 10;
		
		private InfoKernel()
		{
			addCaseInsensitiveKeyword("map", TYPE_MAP);
			
			addStringDelimiter('"', '"');
			
			addDelimiter("{", TYPE_LBRACE);
			addDelimiter("}", TYPE_RBRACE);
			addDelimiter("=", TYPE_EQUAL);
			addDelimiter(",", TYPE_COMMA);
		}
	}
	
	private static class InfoParser extends Parser
	{
		private InfoParser(Reader in)
		{
			super(new Lexer(new InfoKernel(), in));
		}
		
		private void parseInto(UniversalMapInfo mapInfo) throws ParseException
		{
			nextToken();
			while (currentToken() != null)
			{
				parseValueSet(mapInfo);
			}
		}
		
		private void parseValueSet(UniversalMapInfo mapInfo) throws ParseException
		{
			while (matchType(InfoKernel.TYPE_MAP))
			{
				if (!currentType(InfoKernel.TYPE_IDENTIFIER))
					throw new ParseException(getTokenInfoLine("Expected map entry header after 'map'."));
				
				String header = currentLexeme();
				nextToken();
				
				if (!matchType(InfoKernel.TYPE_LBRACE))
					throw new ParseException(getTokenInfoLine("Expected '{' after map entry header."));
				
				MapInfoData[] data = parseMapData(); 
				
				if (!matchType(InfoKernel.TYPE_RBRACE))
					throw new ParseException(getTokenInfoLine("Expected '}' after map data."));
				
				mapInfo.addEntry(header, data);
			}
			
			if (currentToken() != null)
				throw new ParseException(getTokenInfoLine("Unknown token."));
		}

		private MapInfoData[] parseMapData() throws ParseException
		{
			List<MapInfoData> dataList = new LinkedList<>();
			
			while (currentType(InfoKernel.TYPE_IDENTIFIER))
			{
				String name = currentLexeme();
				nextToken();
				
				if (!matchType(InfoKernel.TYPE_EQUAL))
					throw new ParseException(getTokenInfoLine("Expected '=' after value name."));
				
				List<Value> valueList = new LinkedList<>();

				do {
					if (!currentType(InfoKernel.TYPE_IDENTIFIER, InfoKernel.TYPE_NUMBER, InfoKernel.TYPE_STRING))
						throw new ParseException(getTokenInfoLine("Expected value."));

					if (currentType(InfoKernel.TYPE_STRING))
						valueList.add(Value.create(currentLexeme()));
					else if (currentType(InfoKernel.TYPE_IDENTIFIER))
						valueList.add(Value.create(currentLexeme(), true));
					else if (currentType(InfoKernel.TYPE_NUMBER))
						valueList.add(Value.create(parseNumber()));

					nextToken();
					
				} while (matchType(InfoKernel.TYPE_COMMA));
				
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
