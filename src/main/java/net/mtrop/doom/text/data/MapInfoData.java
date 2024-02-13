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
	private static final String[] NO_VALUES = new String[0];
	
	/** Item name. */
	private String name;
	/** Item values (if any). */
	private String[] values;
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
	 * The values are converted to Strings.
	 * @param name the name of the data.
	 * @param values the values.
	 */
	public MapInfoData(String name, Object ... values)
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
	 * The values are converted to Strings.
	 * @param values the values to set.
	 */
	public void setValues(Object ... values)
	{
		this.values = new String[values.length];
		for (int i = 0; i < values.length; i++)
			this.values[i] = String.valueOf(values[i]);
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
	public String[] getValues() 
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
	public MapInfoData addChild(String type, Object ... typeValues)
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
	public MapInfoData addChildAt(int index, String type, Object ... typeValues)
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
		return !children.isEmpty();
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
	 * Gets a value from this item as a String.
	 * @param valueIndex the value index.
	 * @return the property's value as a String, or null if no value.
	 */
	public String getStringValue(int valueIndex)
	{
		return values != null && values.length > valueIndex ? values[valueIndex] : null;
	}

	/**
	 * Gets the first value from this item as a String.
	 * @return the property's value as a String, or null if no value.
	 */
	public String getStringValue()
	{
		return getStringValue(0);
	}

	/**
	 * Gets a value from this item as a double.
	 * @param valueIndex the value index.
	 * @return the property's value as a double, or NaN if no value, or not parseable as a double.
	 */
	public double getDoubleValue(int valueIndex)
	{
		return values != null && values.length > valueIndex ? ValueUtils.parseDouble(values[valueIndex], Double.NaN) : Double.NaN;
	}

	/**
	 * Gets the first value from this item as a double.
	 * @return the property's value as a double, or NaN if no value, or not parseable as a double.
	 */
	public double getDoubleValue()
	{
		return getDoubleValue(0);
	}

	/**
	 * Gets a value from this item as an integer.
	 * @param valueIndex the value index.
	 * @return the property's value as an integer, or 0 if no value, or not parseable as an integer.
	 */
	public int getIntValue(int valueIndex)
	{
		return values != null && values.length > valueIndex ? ValueUtils.parseInt(values[valueIndex], 0) : 0;
	}

	/**
	 * Gets a value from this item as an integer.
	 * @return the property's value as an integer, or 0 if no value, or not parseable as an integer.
	 */
	public double getIntValue()
	{
		return getIntValue(0);
	}
	
}
