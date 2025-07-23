package net.mtrop.doom.text.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.mtrop.doom.struct.utils.ValueUtils;

/**
 * An abstraction of MapInfo data.
 * MapInfo data is mostly a mapping of names to values, however they are stored in an order that
 * may matter to the host program (especially structures).
 * @author Matthew Tropiano
 * @since [NOW]
 */
public class MapInfoData implements Iterable<MapInfoData>
{
	private static final Value[] NO_VALUES = new Value[0];
	
	/** Item name. */
	private String name;
	/** Item values (if any). */
	private Value[] values;
	/** Item data (if any). */
	private List<MapInfoData> children;

	/**
	 * Creates MapInfoData with a specific name.
	 * @param name the name of the data.
	 */
	public MapInfoData(String name)
	{
		this.name = name;
		this.values = NO_VALUES;
		this.children = null;
	}
	
	/**
	 * Creates MapInfoData with a specific name and values.
	 * @param name the name of the data.
	 * @param values the values.
	 */
	public MapInfoData(String name, Value ... values)
	{
		this(name);
		setValues(values);
	}

	/**
	 * Sets this data's name.
	 * @param name the new name.
	 */
	public void setName(String name)
	{
		this.name = name;
		this.values = NO_VALUES;
	}
	
	/**
	 * Sets/replaces the values on this data.
	 * @param values the values to set.
	 */
	public void setValues(Value ... values)
	{
		this.values = new Value[values.length];
		System.arraycopy(values, 0, this.values, 0, values.length);
	}
	
	/**
	 * @return this item's name, or null if no name.
	 */
	public String getName() 
	{
		return name;
	}

	/**
	 * @return the array of values, or null if purely a data structure or directive.
	 */
	public Value[] getValues() 
	{
		return values;
	}

	/**
	 * Creates a new, empty value set at the end of the list of sets.
	 * @param type the set type.
	 * @param typeValues the set type's values (if any).
	 * @return the created empty set.
	 * @throws IndexOutOfBoundsException if the index is less than 0 or greater than or equal to the amount of sets in this MapInfo.
	 */
	public MapInfoData addChild(String type, Value ... typeValues)
	{
		if (children == null)
			children = new ArrayList<>(4);
		
		MapInfoData out = new MapInfoData(type, typeValues);
		children.add(out);
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
	public MapInfoData addChildAt(int index, String type, Value ... typeValues)
	{
		if (children == null)
			children = new ArrayList<>(4);

		MapInfoData out = new MapInfoData(type, typeValues);
		children.add(index, out);
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
		if (children == null)
			throw new IndexOutOfBoundsException("no children in list");
		
		return children.get(index);
	}
	
	/**
	 * Removes a value set at a specific index.
	 * @param index the index.
	 * @return the removed set.
	 * @throws IndexOutOfBoundsException if the index is less than 0 or greater than or equal to the amount of sets in this MapInfo.
	 */
	public MapInfoData removeChildAt(int index)
	{
		if (children == null)
			throw new IndexOutOfBoundsException("no children in list");
		
		return children.remove(index);
	}
	
	/**
	 * @return the amount of data sets in this MapInfo.
	 */
	public int getChildCount()
	{
		if (children == null)
			return 0;
		
		return children.size();
	}
	
	@Override
	public Iterator<MapInfoData> iterator()
	{
		if (children == null)
			return Collections.emptyIterator();
		
		return children.iterator();
	}

	/**
	 * Checks if this data has child data. 
	 * @return true if so, false if not.
	 */
	public boolean hasChildren()
	{
		return children != null && !children.isEmpty();
	}

	/**
	 * Checks if this item is a directive (has no values/children attached to it).
	 * @return true if so, false if not.
	 */
	public boolean isDirective()
	{
		return (values == null || values.length == 0) && !hasChildren();
	}
	
	/**
	 * Checks if this is just values (just values, no children);  
	 * @return true if so, false if not.
	 */
	public boolean isValues()
	{
		return (values != null && values.length > 0) && !hasChildren();
	}
	
	/**
	 * Gets the value at a particular index.
	 * @param valueIndex the value's index.
	 * @return the corresponding value, or null if no value.
	 */
	public Value getValue(int valueIndex)
	{
		return values != null && values.length > valueIndex ? values[valueIndex] : null;
	}
	
	/**
	 * @return the first value.
	 */
	public Value getValue()
	{
		return getValue(0);
	}
	
	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		if (!isDirective())
		{
			boolean first = false;
			sb.append(" [");
			for (Value value : values)
			{
				if (first)
					sb.append(", ");
				sb.append(value);
				first = true;
			}
			sb.append("]");
		}
		if (hasChildren())
			sb.append(" ...");
		return sb.toString();
	}
	
	public static class Value
	{
		private final String token;
		private final Type type;
		
		public enum Type
		{
			NUMERIC,
			IDENTIFIER,
			STRING;
		}
		
		/**
		 * Creates a new value.
		 * @param token the token value.
		 * @param type the value type.
		 */
		private Value(String token, Type type)
		{
			this.token = token;
			this.type = type;
		}

		/**
		 * Creates a new value.
		 * @param number the numeric value.
		 * @return a new value.
		 */
		public static Value create(Number number)
		{
			return new Value(String.valueOf(number), Type.NUMERIC);
		}
		
		/**
		 * Creates a new value (string type).
		 * @param token the token value.
		 * @return a new value.
		 */
		public static Value create(String token)
		{
			return new Value(token, Type.STRING);
		}
		
		/**
		 * Creates a new value.
		 * @param token the token value.
		 * @param identifier if true, this is an identifier.
		 * @return a new value.
		 */
		public static Value create(String token, boolean identifier)
		{
			return new Value(token, identifier ? Type.IDENTIFIER : Type.STRING);
		}
		
		/**
		 * @return this value's string token.
		 */
		public String getToken() 
		{
			return token;
		}
		
		/**
		 * @return this value's type.
		 */
		public Type getType() 
		{
			return type;
		}
		
		/**
		 * Gets this value as a String.
		 * @return the value as a String, or null if no value.
		 */
		public String getStringValue()
		{
			return token;
		}

		/**
		 * Checks if this value is a numeric one.
		 * @return true if so, false if not.
		 */
		public boolean isNumeric()
		{
			return type == Type.NUMERIC;
		}
		
		/**
		 * Gets this value as a double.
		 * @return the value as a double, or NaN if no value, or not parseable as a double.
		 */
		public double getDoubleValue()
		{
			return ValueUtils.parseDouble(token, Double.NaN);
		}

		/**
		 * Gets this value as an integer.
		 * @return the value as an integer, or 0 if no value, or not parseable as an integer.
		 */
		public double getIntValue()
		{
			return ValueUtils.parseInt(token, 0);
		}
		
		@Override
		public String toString() 
		{
			return type == Type.STRING ? '"' + token + '"' : token;
		}
		
	}
	
}
