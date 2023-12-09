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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import net.mtrop.doom.object.TextObject;
import net.mtrop.doom.struct.Lexer;
import net.mtrop.doom.struct.Lexer.Kernel;
import net.mtrop.doom.struct.Lexer.Parser;
import net.mtrop.doom.text.data.MapInfoData;

/**
 * Abstraction of Hexen-style MAPINFO data (also called "old MAPINFO" in ZDoom).
 * All of the typed value sets in this info are stored in order, as the ordering matters when read.
 * @author Matthew Tropiano
 * @since [NOW]
 */
public class HexenMapInfo implements TextObject, Iterable<MapInfoData>
{
	// Author's note: this is the worst MAPINFO to parse - all keywords require knowledge about them to figure out how to parse them.
	
	public static final String SETTYPE_CLEAREPISODES = "clearepisodes";
	public static final String SETTYPE_EPISODE = "episode";
	public static final String SETTYPE_MAP = "map";
	public static final String SETTYPE_DEFAULTMAP = "defaultmap";
	public static final String SETTYPE_ADDDEFAULTMAP = "adddefaultmap";
	public static final String SETTYPE_GAMEDEFAULTS = "gamedefaults";
	public static final String SETTYPE_CLUSTERDEF = "clusterdef";
	public static final String SETTYPE_CLEARSKILLS = "clearskills";
	public static final String SETTYPE_SKILL = "skill";

	/** The list of typed structures in this MapInfo. */
	private List<MapInfoData> mapInfoDataList;

	/**
	 * Creates a new, blank Hexen-style MapInfo.
	 */
	public HexenMapInfo()
	{
		this.mapInfoDataList = new ArrayList<>(16);
	}
	
	/**
	 * Creates a new, empty value set at the end of the list of sets.
	 * @param type the set type.
	 * @param typeValues the set type's values (if any).
	 * @return the created empty set.
	 * @throws IndexOutOfBoundsException if the index is less than 0 or greater than or equal to the amount of sets in this MapInfo.
	 */
	public MapInfoData addChild(String type, Object ... typeValues)
	{
		MapInfoData out = new MapInfoData(type, typeValues);
		mapInfoDataList.add(out);
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
	public MapInfoData addChildAt(int index, String type, Object ... typeValues)
	{
		MapInfoData out = new MapInfoData(type, typeValues);
		mapInfoDataList.add(index, out);
		return out;
	}
	
	/**
	 * Gets a specific typed value set from this MapInfo by its index.
	 * @param index the index of the value set.
	 * @return the value set at the index.
	 * @throws IndexOutOfBoundsException if the index is less than 0 or greater than or equal to the amount of sets in this MapInfo.
	 */
	public MapInfoData getChildAt(int index)
	{
		return mapInfoDataList.get(index);
	}
	
	/**
	 * Removes a value set at a specific index.
	 * @param index the index.
	 * @return the removed set.
	 * @throws IndexOutOfBoundsException if the index is less than 0 or greater than or equal to the amount of sets in this MapInfo.
	 */
	public MapInfoData removeChildAt(int index)
	{
		return mapInfoDataList.remove(index);
	}
	
	/**
	 * @return the amount of value sets in this MapInfo.
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
		(new HexenMapInfoParser(reader)).parseInto(this);
	}

	@Override
	public void writeText(Writer writer) throws IOException 
	{
		for (MapInfoData set : this)
		{
			writer.append(set.getName());
			for (String value : set.getValues())
			{
				if (value.contains(" "))
					writer.append(" \"").append(value).append("\"");
				else
					writer.append(" ").append(value);
			}
			writer.append("\r\n");
			writer.flush();
			writeBody(writer, set);
		}
	}
	
	private void writeBody(Writer writer, MapInfoData set) throws IOException 
	{
		for (MapInfoData property : set)
		{
			writer.append(property.getName());
			for (String value : property.getValues())
			{
				if (value.contains(" "))
					writer.append(" \"").append(value).append("\"");
				else
					writer.append(" ").append(value);
			}

			if (property.hasChildren())
			{
				writer.append(" {\r\n");
				for (MapInfoData child : property)
				{
					writer.append(child.getName());
					for (String value : child.getValues())
					{
						if (value.contains(" "))
							writer.append(" \"").append(value).append("\"");
						else
							writer.append(" ").append(value);
					}
					writer.append("\r\n");
				}
				writer.append("}\r\n");
			}
			
			writer.append("\r\n");
			writer.flush();
		}
		writer.append("\r\n");
		writer.flush();
	}
	
	private static class HexenMapInfoKernel extends Kernel
	{
		private static final int TYPE_COMMENT = 0;
		
		private static final int TYPE_LBRACE = 1;
		private static final int TYPE_RBRACE = 2;
		private static final int TYPE_COMMA = 3;

		private static final int TYPE_ADDDEFAULTMAP = 4;
		private static final int TYPE_CLEAREPISODES = 5;
		private static final int TYPE_CLEARSKILLS = 6;
		private static final int TYPE_CLUSTERDEF = 7;
		private static final int TYPE_DEFAULTMAP = 8;
		private static final int TYPE_EPISODE = 9;
		private static final int TYPE_GAMEDEFAULTS = 10;
		private static final int TYPE_MAP = 11;
		private static final int TYPE_SKILL = 12;

		private HexenMapInfoKernel()
		{
			addCaseInsensitiveKeyword(SETTYPE_ADDDEFAULTMAP, TYPE_ADDDEFAULTMAP);
			addCaseInsensitiveKeyword(SETTYPE_CLEAREPISODES, TYPE_CLEAREPISODES);
			addCaseInsensitiveKeyword(SETTYPE_CLEARSKILLS, TYPE_CLEARSKILLS);
			addCaseInsensitiveKeyword(SETTYPE_CLUSTERDEF, TYPE_CLUSTERDEF);
			addCaseInsensitiveKeyword(SETTYPE_DEFAULTMAP, TYPE_DEFAULTMAP);
			addCaseInsensitiveKeyword(SETTYPE_EPISODE, TYPE_EPISODE);
			addCaseInsensitiveKeyword(SETTYPE_GAMEDEFAULTS, TYPE_GAMEDEFAULTS);
			addCaseInsensitiveKeyword(SETTYPE_MAP, TYPE_MAP);
			addCaseInsensitiveKeyword(SETTYPE_SKILL, TYPE_SKILL);
			
			addCommentLineDelimiter(";", TYPE_COMMENT);
			
			addRawStringDelimiter('"', '"'); // Yes, strings can be multiline.

			addDelimiter("{", TYPE_LBRACE);
			addDelimiter("}", TYPE_RBRACE);
			addDelimiter(",", TYPE_COMMA);
		}
	}
	
	private static class HexenMapInfoParser extends Parser
	{
		private static final Set<String> ZEROARG_SET = setOf(
			// Episode
			"remove",
			"noskillmenu",
			"optional",
			
			// Map
			"doublesky",
			"nointermission",
			"nosoundclipping",
			"allowmonstertelefrags",
			"map07special",
			"baronspecial",
			"cyberdemonspecial",
			"spidermastermindspecial",
			"specialaction_exitlevel",
			"specialaction_opendoor",
			"specialaction_lowerfloor",
			"specialaction_killmonsters",
			"lightning",
			"evenlighting",
			"smoothlighting",
			"clipmidtextures",
			"forcenoskystretch",
			"skystretch",
			"noautosequences",
			"autosquences",
			"strictmonsteractivation",
			"laxmonsteractivation",
			"missileshootersactivateimpactlines",
			"missilesactivateimpactlines",
			"fallingdamage",
			"monsterfallingdamage",
			"oldfallingdamage",
			"strifefallingdamage",
			"forcefallingdamage",
			"nofallingdamage",
			"filterstarts",
			"allowrespawn",
			"teamplayon",
			"teamplayoff",
			"noinventorybar",
			"keepfullinventory",
			"infiniteflightpowerup",
			"nojump",
			"allowjump",
			"nocrouch",
			"allowcrouch",
			"noinfighting",
			"normalinfighting",
			"totalinfighting",
			"checkswitchrange",
			"nocheckswitchrange",
			"unfreezesingleplayerconversations",
			
			// Cluster
			"hub",
			
			// Skill
			"easybossbrain",
			"fastmonsters",
			"disablecheats",
			"autousehealth"
		);
		
		private static final Set<String> SINGLEARG_SET = setOf(
			"name",
			"lookup",
			"picname",
			"key",
			
			"warptrans",
			"levelnum",
			"next",
			"secretnext",
			"cluster",
			"fade",
			"outsidefog",
			"titlepatch",
			"par",
			"music",
			"cdtrack",
			"cdid",
			"exitpic",
			"enterpic",
			"intermusic",
			"bordertexture",
			"fadetable",
			"vertwallshade",
			"horizwallshade",
			"teamdamage",
			"gravity",
			"aircontrol",
			"airsupply",
			"f1",
			
			"entertext",
			"exittext",
			"flat",
			"pic",
			
			"ammofactor",
			"dropammofactor",
			"doubleammofactor",
			"damagefactor",
			"respawntime",
			"respawnlimit",
			"aggressiveness",
			"spawnfilter",
			"acsreturn",
			"key",
			"mustconfirm",
			"picname",
			"textcolor"
		);
		
		private static final Set<String> DOUBLEARG_SET = setOf(
			"sky1",
			"sky2",
			"playerclassname"
		);

		private static final Set<String> SPECIALARG_SET = setOf(
			"specialaction"
		);

		private HexenMapInfoParser(Reader in)
		{
			super(new Lexer(new HexenMapInfoKernel(), in));
		}
		
		@SafeVarargs
		private static <T extends Comparable<T>> Set<T> setOf(T ... values)
		{
			Set<T> out = new TreeSet<T>();
			for (T t : values)
				out.add(t);
			return out;
		}
		
		private boolean isZeroArgumentProperty(String prop)
		{
			prop = prop.toLowerCase();
			return (prop.startsWith("compat_") || ZEROARG_SET.contains(prop));
		}
		
		private boolean isSingleArgumentProperty(String prop)
		{
			prop = prop.toLowerCase();
			return SINGLEARG_SET.contains(prop);
		}
		
		private boolean isDoubleArgumentProperty(String prop)
		{
			prop = prop.toLowerCase();
			return DOUBLEARG_SET.contains(prop);
		}
		
		private boolean isSpecialArgumentProperty(String prop)
		{
			prop = prop.toLowerCase();
			return SPECIALARG_SET.contains(prop);
		}
		
		private void parseInto(HexenMapInfo mapinfo) throws ParseException
		{
			final int STATE_START = 0;
			final int STATE_PROPERTIES = 1;
			final int STATE_VALUE1 = 2;
			final int STATE_VALUE2 = 3;
			final int STATE_VALUESPECIAL = 4;

			final int STATE_VALUE1NEXT = 5;
			final int STATE_VALUE1NEXTENDGAME = 6;

			nextToken();
			
			int state = STATE_START;
			Stack<MapInfoData> currentSet = new Stack<>();
			String property = null;
			while (currentToken() != null)
			{
				switch (state)
				{
					case STATE_START:
					{
						if (currentType(HexenMapInfoKernel.TYPE_ADDDEFAULTMAP))
						{
							mapinfo.addChild(SETTYPE_ADDDEFAULTMAP);
							nextToken();
						}
						else if (currentType(HexenMapInfoKernel.TYPE_CLEAREPISODES))
						{
							mapinfo.addChild(SETTYPE_CLEAREPISODES);
							nextToken();
						}
						else if (currentType(HexenMapInfoKernel.TYPE_CLEARSKILLS))
						{
							mapinfo.addChild(SETTYPE_CLEARSKILLS);
							nextToken();
						}
						else if (currentType(HexenMapInfoKernel.TYPE_CLUSTERDEF))
						{
							List<String> tokens = new LinkedList<>(); 
							nextToken();
							tokens.add(currentToken().getLexeme()); // cluster id
							nextToken();
							currentSet.push(mapinfo.addChild(SETTYPE_CLUSTERDEF, tokens.toArray(new Object[tokens.size()])));
							state = STATE_PROPERTIES;
						}
						else if (currentType(HexenMapInfoKernel.TYPE_DEFAULTMAP))
						{
							nextToken();
							currentSet.push(mapinfo.addChild(SETTYPE_DEFAULTMAP));
							state = STATE_PROPERTIES;
						}
						else if (currentType(HexenMapInfoKernel.TYPE_EPISODE))
						{
							List<String> tokens = new LinkedList<>(); 
							nextToken();
							tokens.add(currentToken().getLexeme()); // episode lump
							nextToken();
							currentSet.push(mapinfo.addChild(SETTYPE_EPISODE, tokens.toArray(new Object[tokens.size()])));
							state = STATE_PROPERTIES;
						}
						else if (currentType(HexenMapInfoKernel.TYPE_GAMEDEFAULTS))
						{
							nextToken();
							if (!matchType(HexenMapInfoKernel.TYPE_DELIM_NEWLINE))
								throw new ParseException(getTokenInfoLine("Expected newline."));
							currentSet.push(mapinfo.addChild(SETTYPE_GAMEDEFAULTS));
							state = STATE_PROPERTIES;
						}
						else if (currentType(HexenMapInfoKernel.TYPE_MAP))
						{
							List<String> tokens = new LinkedList<>(); 
							nextToken();
							tokens.add(currentToken().getLexeme()); // map lump
							nextToken();
							
							if (currentToken().getLexeme().equalsIgnoreCase("lookup"))
							{
								tokens.add(currentToken().getLexeme()); // "lookup"
								nextToken();
								tokens.add(currentToken().getLexeme()); // language lookup
								nextToken();
							}
							else
							{
								tokens.add(currentToken().getLexeme()); // name
								nextToken();
							}
							
							currentSet.push(mapinfo.addChild(SETTYPE_MAP, tokens.toArray(new Object[tokens.size()])));
							state = STATE_PROPERTIES;
						}
						else if (currentType(HexenMapInfoKernel.TYPE_SKILL))
						{
							List<String> tokens = new LinkedList<>(); 
							nextToken();
							tokens.add(currentToken().getLexeme()); // skill type
							nextToken();
							currentSet.push(mapinfo.addChild(SETTYPE_SKILL, tokens.toArray(new Object[tokens.size()])));
							state = STATE_PROPERTIES;
						}
						else if (currentToken().getLexeme().startsWith("cd_")) // Hexen CD Track data
						{
							String name = currentToken().getLexeme();
							List<String> tokens = new LinkedList<>(); 
							nextToken();
							tokens.add(currentToken().getLexeme()); // track id
							nextToken();
							currentSet.push(mapinfo.addChild(name, tokens.toArray(new Object[tokens.size()])));
						}
						else
						{
							throw new ParseException(getTokenInfoLine("Expected definition type."));
						}
					}
					break;
					
					case STATE_PROPERTIES:
					{
						if (currentType(
							HexenMapInfoKernel.TYPE_ADDDEFAULTMAP,
							HexenMapInfoKernel.TYPE_CLEAREPISODES,
							HexenMapInfoKernel.TYPE_CLEARSKILLS,
							HexenMapInfoKernel.TYPE_CLUSTERDEF,
							HexenMapInfoKernel.TYPE_DEFAULTMAP,
							HexenMapInfoKernel.TYPE_EPISODE,
							HexenMapInfoKernel.TYPE_GAMEDEFAULTS,
							HexenMapInfoKernel.TYPE_MAP,
							HexenMapInfoKernel.TYPE_SKILL
						) || currentToken().getLexeme().startsWith("cd_")){
							currentSet.pop();
							state = STATE_START;
						}
						else
						{
							property = currentToken().getLexeme();
							
							if (isZeroArgumentProperty(property))
							{
								currentSet.peek().addChild(property);
							}
							else if (isSingleArgumentProperty(property))
							{
								if (property.equalsIgnoreCase("next"))
									state = STATE_VALUE1NEXT;
								else if (property.equalsIgnoreCase("secretnext"))
									state = STATE_VALUE1NEXT;
								else
									state = STATE_VALUE1;
							}
							else if (isDoubleArgumentProperty(property))
								state = STATE_VALUE2;
							else if (isSpecialArgumentProperty(property))
								state = STATE_VALUESPECIAL;
							else
								throw new ParseException(getTokenInfoLine("Unknown property - can't parse."));
								
							nextToken();
						}
					}
					break;
					
					case STATE_VALUE1:
					{
						if (currentToken() == null)
							throw new ParseException("Expected argument after property \"" + property + "\"");
						
						String value1 = currentToken().getLexeme();
						nextToken();
						
						currentSet.peek().addChild(property, value1);
						state = STATE_PROPERTIES;
					}
					break;

					case STATE_VALUE1NEXT:
					{
						if (currentToken() == null)
							throw new ParseException("Expected argument after property \"" + property + "\"");
						
						String value1 = currentToken().getLexeme();
						nextToken();
						
						if (value1.equalsIgnoreCase("endgame")) // oh god help me
						{
							currentSet.push(currentSet.peek().addChild(property, value1));
							state = STATE_VALUE1NEXTENDGAME;
						}
						else
						{
							currentSet.peek().addChild(property, value1);
							state = STATE_PROPERTIES;
						}
					}
					break;

					case STATE_VALUE1NEXTENDGAME:
					{
						if (!matchType(HexenMapInfoKernel.TYPE_LBRACE))
							throw new ParseException(getTokenInfoLine("Expected '{'."));
						
						parseEndGameBody(currentSet);
												
						if (!matchType(HexenMapInfoKernel.TYPE_RBRACE))
							throw new ParseException(getTokenInfoLine("Expected '}'."));
						
						state = STATE_PROPERTIES;
					}
					break;

					case STATE_VALUE2:
					{
						if (currentToken() == null)
							throw new ParseException("Expected first argument after property \"" + property + "\"");

						String value1 = currentToken().getLexeme();
						nextToken();
						
						if (currentToken() == null)
							throw new ParseException("Expected second argument after property \"" + property + "\"");

						String value2 = currentToken().getLexeme();
						nextToken();

						currentSet.peek().addChild(property, value1, value2);
						state = STATE_PROPERTIES;
					}
					break;
					
					case STATE_VALUESPECIAL:
					{
						List<String> tokens = new LinkedList<>();
						
						if (currentToken() == null)
							throw new ParseException("Expected first argument after property \"" + property + "\"");

						tokens.add(currentToken().getLexeme());
						nextToken();
						
						while (currentType(HexenMapInfoKernel.TYPE_COMMA))
						{
							nextToken();
							if (currentToken() == null)
								throw new ParseException("Expected first argument after property \"" + property + "\"");
						}
						
						currentSet.peek().addChild(property, tokens.toArray(new Object[tokens.size()]));
						state = STATE_PROPERTIES;
					}
					break;
				}
				
			}

		}

		// I hate this.
		private void parseEndGameBody(Stack<MapInfoData> currentSet) throws ParseException
		{
			while (currentType(HexenMapInfoKernel.TYPE_IDENTIFIER))
			{
				if (currentToken().getLexeme().equalsIgnoreCase("cast"))
				{
					nextToken();
					currentSet.peek().addChild("cast");
				}
				else if (currentToken().getLexeme().equalsIgnoreCase("pic"))
				{
					String property = currentToken().getLexeme();
					nextToken();
					
					if (currentToken() == null)
						throw new ParseException("Expected first argument after property \"" + property + "\"");
					
					String value1 = currentToken().getLexeme();
					nextToken();

					currentSet.peek().addChild(property, value1);
				}
				else if (currentToken().getLexeme().equalsIgnoreCase("music"))
				{
					String property = currentToken().getLexeme();
					nextToken();
					
					if (currentToken() == null)
						throw new ParseException("Expected first argument after property \"" + property + "\"");
					
					String value1 = currentToken().getLexeme();
					nextToken();
					
					if (currentToken() == null)
						return;

					if (currentToken().getLexeme().equalsIgnoreCase("loop"))
					{
						String value2 = currentToken().getLexeme();
						nextToken();
						
						currentSet.peek().addChild(property, value1, value2);
					}
					else
					{
						currentSet.peek().addChild(property, value1);
					}
				}
				else if (currentToken().getLexeme().equalsIgnoreCase("hscroll"))
				{
					String property = currentToken().getLexeme();
					nextToken();
					
					if (currentToken() == null)
						throw new ParseException("Expected first argument after property \"" + property + "\"");
					
					String value1 = currentToken().getLexeme();
					nextToken();
					
					if (currentToken() == null)
						throw new ParseException("Expected second argument for property \"" + property + "\"");
					
					String value2 = currentToken().getLexeme();
					nextToken();
					
					currentSet.peek().addChild(property, value1, value2);
				}
				else if (currentToken().getLexeme().equalsIgnoreCase("vscroll"))
				{
					String property = currentToken().getLexeme();
					nextToken();
					
					if (currentToken() == null)
						throw new ParseException("Expected first argument after property \"" + property + "\"");
					
					String value1 = currentToken().getLexeme();
					nextToken();
					
					if (currentToken() == null)
						throw new ParseException("Expected second argument for property \"" + property + "\"");
					
					String value2 = currentToken().getLexeme();
					nextToken();
					
					currentSet.peek().addChild(property, value1, value2);
				}
				else
				{
					throw new ParseException("Expected EngGame property.");
				}
			}
		}
		
	}
	
}
