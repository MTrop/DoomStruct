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
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.mtrop.doom.object.TextObject;
import net.mtrop.doom.struct.Lexer;
import net.mtrop.doom.struct.Lexer.Kernel;
import net.mtrop.doom.struct.Lexer.Parser;
import net.mtrop.doom.struct.io.IOUtils;

/**
 * Parser for Hexen-style MAPINFO data (also called "old MAPINFO" in ZDoom).
 * This parser provides a means for scanning through a MAPINFO definition, line by line, tokenized, as its data
 * is procedural and not serializable. 
 * All data is buffered into memory on read through {@link #readText(Reader)}.
 * @author Matthew Tropiano
 * @since 2.19.0
 */
public class HexenMapInfo implements TextObject
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

	/** Internal text for MapInfo. */
	private StringBuilder builder;
	/** Internal parser for MapInfo. */
	private InfoParser parser;
	
	/**
	 * Creates a blank Hexen MAPINFO parser that reads from a blank definition.
	 * You should probably use {@link #readText(Reader)} or {@link #readFile(java.io.File)} to construct this.
	 */
	public HexenMapInfo() 
	{
		this("");
	}
	
	/**
	 * Creates a Hexen MAPINFO parser that reads from text.
	 * @param sequence the MAPINFO data to read.
	 */
	public HexenMapInfo(CharSequence sequence) 
	{
		this.builder = new StringBuilder(sequence);
		refreshParser(); 
	}
	
	/**
	 * Gets the underlying StringBuilder for this info parser.
	 * If the data is changed, call {@link #refreshParser()} to reset the parser. 
	 * @return a reference to the underlying builder.
	 */
	public StringBuilder getBuilder()
	{
		return builder;
	}
	
	/**
	 * Refreshes the internal parser if changes were made to the internal text.
	 */
	public void refreshParser()
	{
		parser = new InfoParser(new StringReader(builder.toString())); 
	}
	
	/**
	 * Parses the next significant line in the MAPINFO and returns it as a set of tokens.
	 * @return the set of tokens as a string array, or null if end of MAPINFO.
	 * @throws ParseException if a parse error occurs during read.
	 */
	public String[] nextTokens() throws ParseException
	{
		return parser.parse();
	}
	
	/**
	 * Scans the MAPINFO to the next property and returns its line, tokenized.
	 * @param property the property to scan to, case-insensitive check.
	 * @return the set of tokens as a string array, or null if end of MAPINFO.
	 * @throws ParseException if a parse error occurs during read.
	 */
	public String[] scanTo(String property) throws ParseException
	{
		String[] out;
		while ((out = nextTokens()) != null)
		{
			if (out[0].equalsIgnoreCase(property))
				return out;
		}
		return out;
	}
	
	@Override
	public void readText(Reader reader) throws IOException 
	{
		StringWriter writer = new StringWriter();
		IOUtils.relay(reader, writer, 16384, -1);
		writer.flush();
		this.builder = new StringBuilder(writer.toString());
		refreshParser();
	}

	@Override
	public void writeText(Writer writer) throws IOException
	{
		StringReader reader = new StringReader(builder.toString());
		IOUtils.relay(reader, writer, 16384, -1);
		writer.flush();
	}
	
	private static class InfoKernel extends Kernel
	{
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

		private InfoKernel()
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
			
			addCommentLineDelimiter(";");
			
			addRawStringDelimiter('"', '"'); // Yes, strings can be multiline.

			addDelimiter("{", TYPE_LBRACE);
			addDelimiter("}", TYPE_RBRACE);
			addDelimiter(",", TYPE_COMMA);
		}
	}
	
	private static class InfoParser extends Parser
	{
		private static final String COMPAT_PREFIX = "compat_";

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
			"autousehealth",
			"spawnmulti",
			"instantreaction"
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

		private static final int STATE_START = 0;
		private static final int STATE_PROPERTIES = 1;
		private static final int STATE_VALUE1 = 2;
		private static final int STATE_VALUE2 = 3;
		private static final int STATE_VALUESPECIAL = 4;

		private static final int STATE_VALUE1NEXT = 5;
		private static final int STATE_VALUE1NEXTENDGAME = 6;
		private static final int STATE_VALUEENDGAME = 7;
		
		private int state;
		
		private InfoParser(Reader in)
		{
			super(new Lexer(new InfoKernel(), in));
			state = STATE_START;
			nextToken();
		}
		
		@SafeVarargs
		private static <T extends Comparable<T>> Set<T> setOf(T ... values)
		{
			Set<T> out = new TreeSet<T>();
			for (T t : values)
				out.add(t);
			return out;
		}
		
		private boolean isArgumentProperty(String prop)
		{
			return isZeroArgumentProperty(prop)
				|| isSingleArgumentProperty(prop)
				|| isDoubleArgumentProperty(prop)
				|| isSpecialArgumentProperty(prop);
		}
		
		private boolean isZeroArgumentProperty(String prop)
		{
			prop = prop.toLowerCase();
			return (prop.startsWith(COMPAT_PREFIX) || ZEROARG_SET.contains(prop));
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
		
		private String[] parse() throws ParseException
		{
			String property = null;
			while (currentToken() != null)
			{
				switch (state)
				{
					case STATE_START:
					{
						if (currentType(InfoKernel.TYPE_ADDDEFAULTMAP))
						{
							String[] out = new String[]{SETTYPE_ADDDEFAULTMAP};
							nextToken();
							return out;
						}
						else if (currentType(InfoKernel.TYPE_CLEAREPISODES))
						{
							String[] out = new String[]{SETTYPE_CLEAREPISODES};
							nextToken();
							return out;
						}
						else if (currentType(InfoKernel.TYPE_CLEARSKILLS))
						{
							String[] out = new String[]{SETTYPE_CLEARSKILLS};
							nextToken();
							return out;
						}
						else if (currentType(InfoKernel.TYPE_CLUSTERDEF))
						{
							List<String> tokens = new LinkedList<>();
							
							tokens.add(SETTYPE_CLUSTERDEF);
							nextToken();
							
							tokens.add(currentLexeme()); // cluster id
							nextToken();
							
							state = STATE_PROPERTIES;
							return tokens.toArray(new String[tokens.size()]);
						}
						else if (currentType(InfoKernel.TYPE_DEFAULTMAP))
						{
							String[] out = new String[]{SETTYPE_DEFAULTMAP};
							nextToken();
							state = STATE_PROPERTIES;
							return out;
						}
						else if (currentType(InfoKernel.TYPE_EPISODE))
						{
							List<String> tokens = new LinkedList<>(); 
							tokens.add(SETTYPE_EPISODE);
							nextToken();

							tokens.add(currentLexeme()); // episode lump
							nextToken();
							
							state = STATE_PROPERTIES;
							return tokens.toArray(new String[tokens.size()]);
						}
						else if (currentType(InfoKernel.TYPE_GAMEDEFAULTS))
						{
							String[] out = new String[]{SETTYPE_GAMEDEFAULTS};
							nextToken();
							state = STATE_PROPERTIES;
							return out;
						}
						else if (currentType(InfoKernel.TYPE_MAP))
						{
							List<String> tokens = new LinkedList<>();
							tokens.add(SETTYPE_MAP);
							nextToken();
							
							tokens.add(currentLexeme()); // map lump
							nextToken();
							
							if (currentLexeme().equalsIgnoreCase("lookup"))
							{
								tokens.add(currentLexeme()); // "lookup"
								nextToken();
								tokens.add(currentLexeme()); // language lookup
								nextToken();
							}
							else
							{
								tokens.add(currentLexeme()); // name
								nextToken();
							}
							
							state = STATE_PROPERTIES;
							return tokens.toArray(new String[tokens.size()]);
						}
						else if (currentType(InfoKernel.TYPE_SKILL))
						{
							List<String> tokens = new LinkedList<>(); 
							tokens.add(SETTYPE_SKILL);
							nextToken();
							
							tokens.add(currentLexeme()); // skill type
							nextToken();
							
							state = STATE_PROPERTIES;
							return tokens.toArray(new String[tokens.size()]);
						}
						else if (currentLexeme().startsWith("cd_")) // Hexen CD Track data
						{
							String name = currentLexeme();
							
							List<String> tokens = new LinkedList<>(); 
							tokens.add(name);
							nextToken();
							
							tokens.add(currentLexeme()); // track id
							nextToken();
							
							return tokens.toArray(new String[tokens.size()]);
						}
						else
						{
							throw new ParseException(getTokenInfoLine("Expected definition type."));
						}
					}
					
					case STATE_PROPERTIES:
					{
						if (currentType(
							InfoKernel.TYPE_ADDDEFAULTMAP,
							InfoKernel.TYPE_CLEAREPISODES,
							InfoKernel.TYPE_CLEARSKILLS,
							InfoKernel.TYPE_CLUSTERDEF,
							InfoKernel.TYPE_DEFAULTMAP,
							InfoKernel.TYPE_EPISODE,
							InfoKernel.TYPE_GAMEDEFAULTS,
							InfoKernel.TYPE_MAP,
							InfoKernel.TYPE_SKILL
						) || currentLexeme().startsWith("cd_")){
							state = STATE_START;
						}
						else
						{
							property = currentLexeme();
							
							if (isZeroArgumentProperty(property))
							{
								// Gross hack for compat
								if (property.toLowerCase().startsWith(COMPAT_PREFIX))
								{
									nextToken();
									if (currentType(InfoKernel.TYPE_NUMBER))
										state = STATE_VALUE1;
									else
										state = STATE_PROPERTIES;
									continue;
								}
								
								nextToken();
								return new String[]{property};
							}
							else if (isSingleArgumentProperty(property))
							{
								// Gross hack for optional keywords
								if (property.equalsIgnoreCase("mustconfirm"))
								{
									nextToken();
									if (currentType(InfoKernel.TYPE_STRING))
										state = STATE_VALUE1;
									else
										state = STATE_PROPERTIES;
									continue;
								}
								else if (property.equalsIgnoreCase("entertext"))
								{
									nextToken();
									if (currentLexeme().equalsIgnoreCase("lookup"))
										state = STATE_VALUE2;
									else
										state = STATE_VALUE1;
									continue;
								}
								else if (property.equalsIgnoreCase("exittext"))
								{
									nextToken();
									if (currentLexeme().equalsIgnoreCase("lookup"))
										state = STATE_VALUE2;
									else
										state = STATE_VALUE1;
									continue;
								}

								if (property.equalsIgnoreCase("next"))
									state = STATE_VALUE1NEXT;
								else if (property.equalsIgnoreCase("secretnext"))
									state = STATE_VALUE1NEXT;
								else
									state = STATE_VALUE1;
							}
							else if (isDoubleArgumentProperty(property))
							{
								state = STATE_VALUE2;
							}
							else if (isSpecialArgumentProperty(property))
							{
								state = STATE_VALUESPECIAL;
							}
							else
							{
								throw new ParseException(getTokenInfoLine("Unknown property - can't parse."));
							}
								
							nextToken();
						}
					}
					break;
					
					case STATE_VALUE1:
					{
						if (currentToken() == null)
							throw new ParseException("Expected argument after property \"" + property + "\"");
						
						String value1 = currentLexeme();
						nextToken();
						
						state = STATE_PROPERTIES;
						return new String[]{property, value1};
					}

					case STATE_VALUE1NEXT:
					{
						if (currentToken() == null)
							throw new ParseException("Expected argument after property \"" + property + "\"");
						
						String value1 = currentLexeme();
						nextToken();
						
						if (value1.equalsIgnoreCase("endgame")) // oh god help me
						{
							state = STATE_VALUE1NEXTENDGAME;
							return new String[]{property, value1};
						}
						else
						{
							state = STATE_PROPERTIES;
							return new String[]{property, value1};
						}
					}

					case STATE_VALUE1NEXTENDGAME:
					{
						if (!matchType(InfoKernel.TYPE_LBRACE))
							throw new ParseException(getTokenInfoLine("Expected '{'."));
						
						state = STATE_VALUEENDGAME;
					}
					break;
					
					case STATE_VALUEENDGAME:
					{
						if (currentToken() == null)
							throw new ParseException("Expected ENDGAME property.");

						else if (currentLexeme().equalsIgnoreCase("cast"))
						{
							property = currentLexeme();
							nextToken();
							
							return new String[]{property};
						}
						else if (currentLexeme().equalsIgnoreCase("pic"))
						{
							property = currentLexeme();
							nextToken();

							if (currentToken() == null)
								throw new ParseException("Expected argument after property \"" + property + "\"");
							
							String value1 = currentLexeme();
							nextToken();

							return new String[]{property, value1};
						}
						else if (currentLexeme().equalsIgnoreCase("hscroll"))
						{
							property = currentLexeme();
							nextToken();

							if (currentToken() == null)
								throw new ParseException("Expected first argument after property \"" + property + "\"");
							
							String value1 = currentLexeme();
							nextToken();

							if (currentToken() == null)
								throw new ParseException("Expected second argument after property \"" + property + "\"");
							
							String value2 = currentLexeme();
							nextToken();

							return new String[]{property, value1, value2};
						}
						else if (currentLexeme().equalsIgnoreCase("vscroll"))
						{
							property = currentLexeme();
							nextToken();

							if (currentToken() == null)
								throw new ParseException("Expected first argument after property \"" + property + "\"");
							
							String value1 = currentLexeme();
							nextToken();

							if (currentToken() == null)
								throw new ParseException("Expected second argument after property \"" + property + "\"");
							
							String value2 = currentLexeme();
							nextToken();

							return new String[]{property, value1, value2};
						}
						else if (currentLexeme().equalsIgnoreCase("music"))
						{
							property = currentLexeme();
							nextToken();

							if (currentToken() == null)
								throw new ParseException("Expected argument after property \"" + property + "\"");
							
							String value1 = currentLexeme();
							nextToken();

							if (currentToken() == null)
								throw new ParseException("Expected '}' or valid EndGame property.");
							
							if (currentLexeme().equalsIgnoreCase("loop"))
							{
								String loop = currentLexeme();
								nextToken();
								return new String[]{property, value1, loop};
							}
							
							String value2 = currentLexeme();
							nextToken();

							return new String[]{property, value1, value2};
						}
						else
						{
							if (!matchType(InfoKernel.TYPE_RBRACE))
								throw new ParseException(getTokenInfoLine("Expected '}' or valid EndGame property."));
						}
						
						state = STATE_PROPERTIES;
					}
					break;

					case STATE_VALUE2:
					{
						if (currentToken() == null)
							throw new ParseException("Expected first argument after property \"" + property + "\"");

						String value1 = currentLexeme();
						nextToken();
						
						// Gross hack for sky
						if (property.equalsIgnoreCase("sky1") || property.equalsIgnoreCase("sky2"))
						{
							if (isArgumentProperty(currentLexeme()))
							{
								state = STATE_PROPERTIES;
								continue;
							}
						}
						
						if (currentToken() == null)
							throw new ParseException("Expected second argument after property \"" + property + "\"");

						String value2 = currentLexeme();
						nextToken();

						state = STATE_PROPERTIES;
						return new String[]{property, value1, value2};
					}
					
					case STATE_VALUESPECIAL:
					{
						List<String> tokens = new LinkedList<>();
						tokens.add(property);
						
						if (currentToken() == null)
							throw new ParseException("Expected first argument after property \"" + property + "\"");

						tokens.add(currentLexeme());
						nextToken();
						
						while (currentType(InfoKernel.TYPE_COMMA))
						{
							nextToken();
							if (currentToken() == null)
								throw new ParseException("Expected first argument after property \"" + property + "\"");
						}
						
						state = STATE_PROPERTIES;
						return tokens.toArray(new String[tokens.size()]);
					}
					
					default:
						throw new ParseException("INTERNAL ERROR: BAD STATE");
					
				}
				
			}

			return null;
		}

	}
	
}
