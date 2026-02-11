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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.mtrop.doom.object.TextObject;
import net.mtrop.doom.struct.Lexer;
import net.mtrop.doom.struct.Lexer.Kernel;
import net.mtrop.doom.struct.Lexer.Parser;
import net.mtrop.doom.text.data.MapInfoData;
import net.mtrop.doom.text.data.MapInfoData.Value;

/**
 * An abstraction of the ZDoom Map Info entry (ZMAPINFO/MAPINFO).
 * All of the data sets in this info are stored in order, as the ordering matters when read.
 * "Include" directives are not followed - they are instead stored as objects.
 * @author Matthew Tropiano
 * @since 2.19.0
 */
public class ZDoomMapInfo implements TextObject, Iterable<MapInfoData>
{
	public static final String SETTYPE_INCLUDE = "include";
	
	public static final String SETTYPE_CLEAREPISODES = "clearepisodes";
	public static final String SETTYPE_EPISODE = "episode";
	public static final String SETTYPE_MAP = "map";
	public static final String SETTYPE_DEFAULTMAP = "defaultmap";
	public static final String SETTYPE_ADDDEFAULTMAP = "adddefaultmap";
	public static final String SETTYPE_GAMEDEFAULTS = "gamedefaults";
	public static final String SETTYPE_CLUSTERDEF = "clusterdef";
	public static final String SETTYPE_CLEARSKILLS = "clearskills";
	public static final String SETTYPE_SKILL = "skill";
	public static final String SETTYPE_GAMEINFO = "gameinfo";
	public static final String SETTYPE_INTERMISSON = "intermission";
	public static final String SETTYPE_AUTOMAP = "automap";
	public static final String SETTYPE_DOOMEDNUMS = "doomednums";
	public static final String SETTYPE_SPAWNNUMS = "spawnnums";
	public static final String SETTYPE_CONVERSATIONID = "conversationid";
	public static final String SETTYPE_DAMAGETYPE = "damagetype";

	/** The list of typed structures in this MapInfo. */
	private List<MapInfoData> mapInfoDataList;
	
	/**
	 * Creates a new, blank ZDoom-style MapInfo.
	 */
	public ZDoomMapInfo()
	{
		this.mapInfoDataList = new ArrayList<>(16);
	}
	
	/**
	 * Creates a new, empty child set at the end of the list of sets.
	 * @param type the set type.
	 * @param typeValues the set type's values (if any).
	 * @return the created empty set.
	 * @throws IndexOutOfBoundsException if the index is less than 0 or greater than or equal to the amount of sets in this MapInfo.
	 */
	public MapInfoData addChild(String type, Value ... typeValues)
	{
		MapInfoData out = new MapInfoData(type, typeValues);
		mapInfoDataList.add(out);
		return out;
	}

	/**
	 * Creates a new, empty child set to add to at a specific index in the list of sets.
	 * @param index the index to add to.
	 * @param type the set type.
	 * @param typeValues the set type's values (if any).
	 * @return the created empty set.
	 * @throws IndexOutOfBoundsException if the index is less than 0 or greater than or equal to the amount of sets in this MapInfo.
	 */
	public MapInfoData addChildAt(int index, String type, Value ... typeValues)
	{
		MapInfoData out = new MapInfoData(type, typeValues);
		mapInfoDataList.add(index, out);
		return out;
	}

	/**
	 * Gets a specific typed child set from this MapInfo by its index.
	 * @param index the index of the child set.
	 * @return the child set at the index.
	 * @throws IndexOutOfBoundsException if the index is less than 0 or greater than or equal to the amount of sets in this MapInfo.
	 */
	public MapInfoData getChildAt(int index)
	{
		return mapInfoDataList.get(index);
	}

	/**
	 * Removes a child set at a specific index.
	 * @param index the index.
	 * @return the removed set.
	 * @throws IndexOutOfBoundsException if the index is less than 0 or greater than or equal to the amount of sets in this MapInfo.
	 */
	public MapInfoData removeChildAt(int index)
	{
		return mapInfoDataList.remove(index);
	}

	/**
	 * @return the amount of child sets in this MapInfo.
	 */
	public int getChildCount()
	{
		return mapInfoDataList.size();
	}

	@Override
	public Iterator<MapInfoData> iterator()
	{
		return mapInfoDataList.iterator();
	}

	@Override
	public void readText(Reader reader) throws IOException
	{
		mapInfoDataList.clear();
		(new InfoParser(reader)).parseInto(this);
	}

	@Override
	public void writeText(Writer writer) throws IOException
	{
		for (MapInfoData data : this)
		{
			writer.append(data.getName());
			if (!data.isDirective())
			{
				if (!data.hasChildren())
				{
					writer.append(" = ");
					boolean first = true;
					for (Value value : data.getValues())
					{
						if (!first)
							writer.append(", ");
						writer.append(value.toString());
						first = false;
					}
				}
				else
				{
					for (Value value : data.getValues())
					{
						writer.append(" ").append(value.toString());
					}
					
					writer.append("\n{\n");
					for (MapInfoData child : data)
					{
						writeBody(writer, child, "\t");
					}
					writer.append("}\n");
				}
			}
			else
			{
				writer.append("\n");
			}
			writer.append("\n");
			writer.flush();
		}
	}
	
	private void writeBody(Writer writer, MapInfoData body, String tab) throws IOException
	{
		writer.append(tab).append(body.getName());
		boolean first = true;
		for (Value value : body.getValues())
		{
			if (!first)
				writer.append(", ");
			else
				writer.append(" = ");
			
			writer.append(value.toString());
			first = false;
		}
		writer.append("\n");
	}

	private static class InfoKernel extends Kernel
	{
		private static final int TYPE_LBRACE = 1;
		private static final int TYPE_RBRACE = 2;
		private static final int TYPE_EQUAL = 3;
		private static final int TYPE_COMMA = 4;

		// special handling
		private static final int TYPE_INCLUDE = 10;
		private static final int TYPE_CLEAREPISODES = 11;
		private static final int TYPE_CLEARSKILLS = 12;

		private InfoKernel()
		{
			setDecimalSeparator('.');
			
			addCaseInsensitiveKeyword("include", TYPE_INCLUDE);
			addCaseInsensitiveKeyword("clearepisodes", TYPE_CLEAREPISODES);
			addCaseInsensitiveKeyword("clearskills", TYPE_CLEARSKILLS);
			
			addCommentDelimiter("/*", "*/");
			addCommentLineDelimiter("//");
			
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
		
		private void parseInto(ZDoomMapInfo mapInfo) throws ParseException
		{
			nextToken();
			while (currentToken() != null)
			{
				parseValueSet(mapInfo);
			}
		}
		
		private void parseValueSet(ZDoomMapInfo mapInfo) throws ParseException
		{
			if (matchType(InfoKernel.TYPE_INCLUDE))
			{
				String entry = currentLexeme();
				mapInfo.addChild("include", Value.create(entry));
			}
			else if (matchType(InfoKernel.TYPE_CLEAREPISODES))
			{
				mapInfo.addChild("clearepisodes");
			}
			else if (matchType(InfoKernel.TYPE_CLEARSKILLS))
			{
				mapInfo.addChild("clearskills");
			}
			else if (!currentType(InfoKernel.TYPE_IDENTIFIER))
			{
				throw new ParseException(getTokenInfoLine("Expected definition name."));
			}
			
			String type = currentLexeme();
			List<Value> valueList = new LinkedList<>(); 
			nextToken();
			
			while (!currentType(InfoKernel.TYPE_LBRACE))
			{
				if (currentType(InfoKernel.TYPE_STRING))
					valueList.add(Value.create(currentLexeme()));
				else if (currentType(InfoKernel.TYPE_IDENTIFIER))
					valueList.add(Value.create(currentLexeme(), true));
				else if (currentType(InfoKernel.TYPE_NUMBER))
					valueList.add(Value.create(parseNumber()));
				nextToken();
			}
			parseBody(mapInfo.addChild(type, valueList.toArray(new Value[valueList.size()])));
		}
		
		private void parseBody(MapInfoData data) throws ParseException
		{
			if (!matchType(InfoKernel.TYPE_LBRACE))
				throw new ParseException(getTokenInfoLine("Expected '{'."));

			while (currentType(InfoKernel.TYPE_IDENTIFIER, InfoKernel.TYPE_NUMBER, InfoKernel.TYPE_STRING))
			{
				String property = currentLexeme();
				nextToken();
				
				if (matchType(InfoKernel.TYPE_EQUAL))
				{
					List<Value> values = new LinkedList<>();
					if (!currentType(InfoKernel.TYPE_IDENTIFIER, InfoKernel.TYPE_NUMBER, InfoKernel.TYPE_STRING))
						throw new ParseException(getTokenInfoLine("Expected value after '='."));

					if (currentType(InfoKernel.TYPE_STRING))
						values.add(Value.create(currentLexeme()));
					else if (currentType(InfoKernel.TYPE_IDENTIFIER))
						values.add(Value.create(currentLexeme(), true));
					else if (currentType(InfoKernel.TYPE_NUMBER))
						values.add(Value.create(parseNumber()));

					nextToken();
					
					if (currentType(InfoKernel.TYPE_LBRACE))
					{
						parseBody(data.addChild(property, values.toArray(new Value[values.size()])));
					}
					else
					{
						while (currentType(InfoKernel.TYPE_COMMA))
						{
							nextToken();
							if (!currentType(InfoKernel.TYPE_IDENTIFIER, InfoKernel.TYPE_NUMBER, InfoKernel.TYPE_STRING))
								throw new ParseException(getTokenInfoLine("Expected value after ','."));

							if (currentType(InfoKernel.TYPE_STRING))
								values.add(Value.create(currentLexeme()));
							else if (currentType(InfoKernel.TYPE_IDENTIFIER))
								values.add(Value.create(currentLexeme(), true));
							else if (currentType(InfoKernel.TYPE_NUMBER))
								values.add(Value.create(parseNumber()));
							
							nextToken();
						}
						
						data.addChild(property, values.toArray(new Value[values.size()]));
					}
				}
				else if (currentType(InfoKernel.TYPE_LBRACE))
				{
					parseBody(data.addChild(property));
				}
				else
				{
					data.addChild(property);
				}
			}
			
			if (!matchType(InfoKernel.TYPE_RBRACE))
				throw new ParseException(getTokenInfoLine("Expected '}'."));
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
