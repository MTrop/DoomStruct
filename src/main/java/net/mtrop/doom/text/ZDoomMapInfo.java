/*******************************************************************************
 * Copyright (c) 2015-2023 Matt Tropiano
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

import net.mtrop.doom.object.TextObject;
import net.mtrop.doom.struct.Lexer;
import net.mtrop.doom.struct.Lexer.Kernel;
import net.mtrop.doom.struct.Lexer.Parser;
import net.mtrop.doom.text.data.MapInfoData;

/**
 * An abstraction of the ZDoom Map Info entry (ZMAPINFO/MAPINFO).
 * All of the typed value sets in this info are stored in order, as the ordering matters when read.
 * @author Matthew Tropiano
 * @since [NOW]
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
	private List<MapInfoData> typedValueSetList;
	
	/**
	 * Creates a new, empty value set at the end of the list of sets.
	 * @param type the set type.
	 * @param typeValues the set type's values (if any).
	 * @return the created empty set.
	 * @throws IndexOutOfBoundsException if the index is less than 0 or greater than or equal to the amount of sets in this MapInfo.
	 */
	public MapInfoData addValueSet(String type, Object ... typeValues)
	{
		MapInfoData out = new MapInfoData(type, typeValues);
		typedValueSetList.add(out);
		return out;
	}

	/**
	 * Creates a new, empty value set to add to at a specific index in the list of sets.
	 * @param index the index to add to.
	 * @param type the set type.
	 * @param typeValues the set type's values (if any).
	 * @return the created empty set.
	 * @throws IndexOutOfBoundsException if the index is less than 0 or greater than or equal to the amount of sets in this MapInfo.
	 */
	public MapInfoData addValueSetAt(int index, String type, Object ... typeValues)
	{
		MapInfoData out = new MapInfoData(type, typeValues);
		typedValueSetList.add(index, out);
		return out;
	}

	/**
	 * Gets a specific typed value set from this MapInfo by its index.
	 * @param index the index of the value set.
	 * @return the value set at the index.
	 * @throws IndexOutOfBoundsException if the index is less than 0 or greater than or equal to the amount of sets in this MapInfo.
	 */
	public MapInfoData getValueSetAt(int index)
	{
		return typedValueSetList.get(index);
	}

	/**
	 * Removes a value set at a specific index.
	 * @param index the index.
	 * @return the removed set.
	 * @throws IndexOutOfBoundsException if the index is less than 0 or greater than or equal to the amount of sets in this MapInfo.
	 */
	public MapInfoData removeValueSetAt(int index)
	{
		return typedValueSetList.remove(index);
	}

	/**
	 * @return the amount of value sets in this MapInfo.
	 */
	public int getValueSetCount()
	{
		return typedValueSetList.size();
	}

	@Override
	public Iterator<MapInfoData> iterator()
	{
		return typedValueSetList.iterator();
	}

	@Override
	public void readText(Reader reader) throws IOException
	{
		typedValueSetList.clear();
		(new ZDoomMapInfoParser(reader)).parseInto(this);
	}

	@Override
	public void writeText(Writer writer) throws IOException
	{
		// TODO: Finish.
	}

	private static class ZDoomMapInfoKernel extends Kernel
	{
		private static final int TYPE_COMMENT = 0;
		private static final int TYPE_LBRACE = 1;
		private static final int TYPE_RBRACE = 2;
		private static final int TYPE_EQUAL = 3;
		private static final int TYPE_COMMA = 4;

		private static final int TYPE_INCLUDE = 10;

		private ZDoomMapInfoKernel()
		{
			addCaseInsensitiveKeyword("include", TYPE_INCLUDE);
			
			addCommentLineDelimiter("//", TYPE_COMMENT);
			addCommentStartDelimiter("/*", TYPE_COMMENT);
			addCommentEndDelimiter("*/", TYPE_COMMENT);
			
			addStringDelimiter('"', '"');

			addDelimiter("{", TYPE_LBRACE);
			addDelimiter("}", TYPE_RBRACE);
			addDelimiter("=", TYPE_EQUAL);
			addDelimiter(",", TYPE_COMMA);
		}
		
	}
	
	private static class ZDoomMapInfoParser extends Parser
	{
		private ZDoomMapInfoParser(Reader in)
		{
			super(new Lexer(new ZDoomMapInfoKernel(), in));
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
			if (matchType(ZDoomMapInfoKernel.TYPE_INCLUDE))
			{
				String entry = currentToken().getLexeme();
				mapInfo.addValueSet("include", entry);
			}
			else if (!currentType(ZDoomMapInfoKernel.TYPE_IDENTIFIER))
				throw new ParseException(getTokenInfoLine("Expected definition name."));
			
			String type = currentToken().getLexeme();
			List<String> tokens = new LinkedList<>(); 
			nextToken();
			while (!currentType(ZDoomMapInfoKernel.TYPE_LBRACE))
			{
				tokens.add(currentToken().getLexeme());
				nextToken();
			}
			parseBody(mapInfo.addValueSet(type, tokens.toArray(new Object[tokens.size()])));
		}
		
		private void parseBody(MapInfoData data) throws ParseException
		{
			if (!matchType(ZDoomMapInfoKernel.TYPE_LBRACE))
				throw new ParseException(getTokenInfoLine("Expected '{'."));

			while (currentType(ZDoomMapInfoKernel.TYPE_IDENTIFIER, ZDoomMapInfoKernel.TYPE_NUMBER, ZDoomMapInfoKernel.TYPE_STRING))
			{
				String property = currentToken().getLexeme();
				nextToken();
				
				if (matchType(ZDoomMapInfoKernel.TYPE_EQUAL))
				{
					List<String> tokens = new LinkedList<>();
					if (!currentType(ZDoomMapInfoKernel.TYPE_IDENTIFIER, ZDoomMapInfoKernel.TYPE_NUMBER, ZDoomMapInfoKernel.TYPE_STRING))
						throw new ParseException(getTokenInfoLine("Expected value after '='."));
					
					tokens.add(currentToken().getLexeme());
					while (currentType(ZDoomMapInfoKernel.TYPE_COMMA))
					{
						nextToken();
						if (!currentType(ZDoomMapInfoKernel.TYPE_IDENTIFIER, ZDoomMapInfoKernel.TYPE_NUMBER, ZDoomMapInfoKernel.TYPE_STRING))
							throw new ParseException(getTokenInfoLine("Expected value after ','."));
						tokens.add(currentToken().getLexeme());
						nextToken();
					}
					
					data.addChild(property, tokens.toArray(new Object[tokens.size()]));
				}
				else if (matchType(ZDoomMapInfoKernel.TYPE_LBRACE))
				{
					// TODO: Finish.
				}
				
			}
			
			if (!matchType(ZDoomMapInfoKernel.TYPE_RBRACE))
				throw new ParseException(getTokenInfoLine("Expected '}'."));
		}
		
	}
	
}
