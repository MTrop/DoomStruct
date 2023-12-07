package net.mtrop.doom.text;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import net.mtrop.doom.object.TextObject;
import net.mtrop.doom.struct.Lexer;
import net.mtrop.doom.struct.Lexer.Kernel;
import net.mtrop.doom.struct.Lexer.Parser;
import net.mtrop.doom.text.data.DefaultValueSet;
import net.mtrop.doom.text.data.ValueSet;

/**
 * Abstraction of Hexen-style MAPINFO data (also called "old MAPINFO" in ZDoom).
 * All of the typed value sets in this info are stored in order, as the ordering matters when read.
 * @author Matthew Tropiano
 * @since [NOW]
 */
public class HexenMapInfo implements TextObject, Iterable<HexenMapInfo.TypedValueSet>
{
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
	private List<TypedValueSet> typedValueSetList;

	/**
	 * Creates a new, blank Hexen-style MapInfo.
	 */
	public HexenMapInfo()
	{
		this.typedValueSetList = new ArrayList<>(16);
	}
	
	/**
	 * Creates a new, empty value set at the end of the list of sets.
	 * @param type the set type.
	 * @param typeValues the set type's values (if any).
	 * @return the created empty set.
	 * @throws IndexOutOfBoundsException if the index is less than 0 or greater than or equal to the amount of sets in this MapInfo.
	 */
	public TypedValueSet addValueSet(String type, Object ... typeValues)
	{
		TypedValueSet out = new TypedValueSet(type, typeValues);
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
	public TypedValueSet addValueSetAt(int index, String type, Object ... typeValues)
	{
		TypedValueSet out = new TypedValueSet(type, typeValues);
		typedValueSetList.add(index, out);
		return out;
	}
	
	/**
	 * Gets a specific typed value set from this MapInfo by its index.
	 * @param index the index of the value set.
	 * @return the value set at the index.
	 * @throws IndexOutOfBoundsException if the index is less than 0 or greater than or equal to the amount of sets in this MapInfo.
	 */
	public TypedValueSet getValueSetAt(int index)
	{
		return typedValueSetList.get(index);
	}
	
	/**
	 * Removes a value set at a specific index.
	 * @param index the index.
	 * @return the removed set.
	 * @throws IndexOutOfBoundsException if the index is less than 0 or greater than or equal to the amount of sets in this MapInfo.
	 */
	public TypedValueSet removeValueSetAt(int index)
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
	public Iterator<TypedValueSet> iterator()
	{
		return typedValueSetList.iterator();
	}

	@Override
	public void readText(Reader reader) throws IOException
	{
		typedValueSetList.clear();
		(new HexenMapInfoParser(reader)).parseInto(this);
	}

	@Override
	public void writeText(Writer writer) throws IOException 
	{
		for (TypedValueSet set : this)
		{
			writer.append(set.getType());
			for (String value : set.getTypeValues())
			{
				if (value.contains(" "))
					writer.append(" \"").append(value).append("\"");
				else
					writer.append(" ").append(value);
			}
			writer.append("\r\n");
			writer.flush();
			for (String prop : set.properties())
			{
				writer.append(prop);
				for (String value : set.getValues(prop))
				{
					if (value.contains(" "))
						writer.append(" \"").append(value).append("\"");
					else
						writer.append(" ").append(value);
				}
				writer.append("\r\n");
				writer.flush();
			}
			for (String prop : set.valueSetProperties())
			{
				writer.append(prop);
				ValueSet<?> vset = set.getValueSet(prop);
				if (vset.getName() != null)
					writer.append(" ").append(vset.getName());
				writer.append(" {");
				for (String vprop : vset.properties())
				{
					writer.append(prop);
					for (String value : vset.getValues(vprop))
					{
						if (value.contains(" "))
							writer.append(" \"").append(value).append("\"");
						else
							writer.append(" ").append(value);
					}
					writer.append("\r\n");
				}
				writer.append("}\r\n");
				writer.flush();
			}
			writer.append("\r\n");
			writer.flush();
		}
	}
	
	/**
	 * A typed value set.
	 */
	public static class TypedValueSet extends DefaultValueSet
	{
		private String type;
		private String[] typeValues;
		
		private TypedValueSet(String type, Object ... typeValues)
		{
			super();
			this.type = type;
			this.typeValues = new String[typeValues.length];
			for (int i = 0; i < typeValues.length; i++)
				this.typeValues[i] = String.valueOf(typeValues[i]);
		}
		
		/**
		 * @return the type of this value set (map, skill, etc.).
		 */
		public String getType() 
		{
			return type;
		}
		
		/**
		 * @return this set's type values.
		 */
		public String[] getTypeValues() 
		{
			return typeValues;
		}
		
	}
	
	private static class HexenMapInfoKernel extends Kernel
	{
		private static final int TYPE_ADDDEFAULTMAP = 0;
		private static final int TYPE_CLEAREPISODES = 1;
		private static final int TYPE_CLEARSKILLS = 2;
		private static final int TYPE_CLUSTERDEF = 3;
		private static final int TYPE_DEFAULTMAP = 4;
		private static final int TYPE_EPISODE = 5;
		private static final int TYPE_GAMEDEFAULTS = 6;
		private static final int TYPE_MAP = 7;
		private static final int TYPE_SKILL = 8;
		private static final int TYPE_LBRACE = 9;
		private static final int TYPE_RBRACE = 10;
		private static final int TYPE_COMMENT = 11;

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
			
			addStringDelimiter('"', '"');

			addDelimiter("{", TYPE_LBRACE);
			addDelimiter("}", TYPE_RBRACE);
			
			setEmitNewlines(true);
		}
	}
	
	private static class HexenMapInfoParser extends Parser
	{
		private HexenMapInfoParser(Reader in)
		{
			super(new Lexer(new HexenMapInfoKernel(), in));
		}
		
		private void parseInto(HexenMapInfo mapinfo) throws ParseException
		{
			final int STATE_START = 0;
			final int STATE_PROPERTIES = 1;
			final int STATE_VALUES = 2;
			
			// skip newlines until we hit data.
			nextToken();
			while (currentType(HexenMapInfoKernel.TYPE_DELIM_NEWLINE))
				nextToken();
			
			int state = STATE_START;
			Stack<TypedValueSet> currentSet = new Stack<>();
			String key = null;
			while (currentToken() != null)
			{
				switch (state)
				{
					case STATE_START:
					{
						if (currentType(HexenMapInfoKernel.TYPE_ADDDEFAULTMAP))
						{
							mapinfo.addValueSet(SETTYPE_ADDDEFAULTMAP);
							nextToken();
							if (!matchType(HexenMapInfoKernel.TYPE_DELIM_NEWLINE))
								throw new ParseException(getTokenInfoLine("Expected newline."));
						}
						else if (currentType(HexenMapInfoKernel.TYPE_CLEAREPISODES))
						{
							mapinfo.addValueSet(SETTYPE_CLEAREPISODES);
							nextToken();
							if (!matchType(HexenMapInfoKernel.TYPE_DELIM_NEWLINE))
								throw new ParseException(getTokenInfoLine("Expected newline."));
						}
						else if (currentType(HexenMapInfoKernel.TYPE_CLEARSKILLS))
						{
							mapinfo.addValueSet(SETTYPE_CLEARSKILLS);
							nextToken();
							if (!matchType(HexenMapInfoKernel.TYPE_DELIM_NEWLINE))
								throw new ParseException(getTokenInfoLine("Expected newline."));
						}
						else if (currentType(HexenMapInfoKernel.TYPE_CLUSTERDEF))
						{
							List<String> tokens = new LinkedList<>(); 
							nextToken();
							while (!currentType(HexenMapInfoKernel.TYPE_DELIM_NEWLINE))
							{
								tokens.add(currentToken().getLexeme());
								nextToken();
							}
							nextToken();
							currentSet.push(mapinfo.addValueSet(SETTYPE_CLUSTERDEF, tokens.toArray(new Object[tokens.size()])));
							state = STATE_PROPERTIES;
						}
						else if (currentType(HexenMapInfoKernel.TYPE_DEFAULTMAP))
						{
							nextToken();
							if (!matchType(HexenMapInfoKernel.TYPE_DELIM_NEWLINE))
								throw new ParseException(getTokenInfoLine("Expected newline."));
							currentSet.push(mapinfo.addValueSet(SETTYPE_DEFAULTMAP));
							state = STATE_PROPERTIES;
						}
						else if (currentType(HexenMapInfoKernel.TYPE_EPISODE))
						{
							List<String> tokens = new LinkedList<>(); 
							nextToken();
							while (!currentType(HexenMapInfoKernel.TYPE_DELIM_NEWLINE))
							{
								tokens.add(currentToken().getLexeme());
								nextToken();
							}
							nextToken();
							currentSet.push(mapinfo.addValueSet(SETTYPE_EPISODE, tokens.toArray(new Object[tokens.size()])));
							state = STATE_PROPERTIES;
						}
						else if (currentType(HexenMapInfoKernel.TYPE_GAMEDEFAULTS))
						{
							nextToken();
							if (!matchType(HexenMapInfoKernel.TYPE_DELIM_NEWLINE))
								throw new ParseException(getTokenInfoLine("Expected newline."));
							currentSet.push(mapinfo.addValueSet(SETTYPE_GAMEDEFAULTS));
							state = STATE_PROPERTIES;
						}
						else if (currentType(HexenMapInfoKernel.TYPE_MAP))
						{
							List<String> tokens = new LinkedList<>(); 
							nextToken();
							while (!currentType(HexenMapInfoKernel.TYPE_DELIM_NEWLINE))
							{
								tokens.add(currentToken().getLexeme());
								nextToken();
							}
							nextToken();
							currentSet.push(mapinfo.addValueSet(SETTYPE_MAP, tokens.toArray(new Object[tokens.size()])));
							state = STATE_PROPERTIES;
						}
						else if (currentType(HexenMapInfoKernel.TYPE_SKILL))
						{
							List<String> tokens = new LinkedList<>(); 
							nextToken();
							while (!currentType(HexenMapInfoKernel.TYPE_DELIM_NEWLINE))
							{
								tokens.add(currentToken().getLexeme());
								nextToken();
							}
							nextToken();
							currentSet.push(mapinfo.addValueSet(SETTYPE_SKILL, tokens.toArray(new Object[tokens.size()])));
							state = STATE_PROPERTIES;
						}
						else if (currentToken().getLexeme().startsWith("cd_")) // Hexen CD Track data
						{
							String name = currentToken().getLexeme();
							List<String> tokens = new LinkedList<>(); 
							nextToken();
							while (!currentType(HexenMapInfoKernel.TYPE_DELIM_NEWLINE))
							{
								tokens.add(currentToken().getLexeme());
								nextToken();
							}
							nextToken();
							currentSet.push(mapinfo.addValueSet(name, tokens.toArray(new Object[tokens.size()])));
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
							key = currentToken().getLexeme();
							nextToken();
							state = STATE_VALUES;
						}
					}
					break;
					
					case STATE_VALUES:
					{
						List<String> tokens = new LinkedList<>(); 
						while (!currentType(HexenMapInfoKernel.TYPE_DELIM_NEWLINE))
						{
							tokens.add(currentToken().getLexeme());
							nextToken();
						}
						nextToken();
						currentSet.peek().setValues(key, tokens.toArray(new Object[tokens.size()]));
						state = STATE_PROPERTIES;
					}
					break;
				}
			}
		}
	}
	
}
