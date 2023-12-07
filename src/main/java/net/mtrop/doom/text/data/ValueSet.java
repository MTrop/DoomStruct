package net.mtrop.doom.text.data;

import java.util.Set;

import net.mtrop.doom.struct.utils.ValueUtils;

/**
 * The policy for all value sets in MAPINFO.
 * @author Matthew Tropiano
 * @param <SELF> this ValueSet's type.
 * @since [NOW]
 */
public interface ValueSet<SELF extends ValueSet<?>>
{
	/**
	 * @return this set's name, if any.  
	 */
	String getName();
	
	/**
	 * Checks if a property is present for this set.
	 * If this returns false, then <code>getValues(property)</code> will return <code>null</code>.
	 * @param property the property name.
	 * @return true if so, false if not.
	 */
	boolean hasProperty(String property);

	/**
	 * @return this ValueSet's properties as a set.
	 */
	Set<String> properties();
	
	/**
	 * @return this ValueSet's properties that store ValueSets as a set.
	 */
	Set<String> valueSetProperties();
	
	/**
	 * Gets the values for a property.
	 * @param property the property name.
	 * @return the property's values, or null if not a valid property.
	 */
	String[] getValues(String property);

	/**
	 * Gets a value set for a property.
	 * If the property is not a value set, this return null.
	 * @param property the property name.
	 * @return the value set, or null if not a value set.
	 */
	ValueSet<?> getValueSet(String property);
	
	/**
	 * Sets the values for a property.
	 * If the property exists, its values are overwritten.
	 * @param property the property.
	 * @param values the values (converted to Strings). Can be unspecified.
	 * @return this set.
	 */
	SELF setValues(String property, Object ... values);
	
	/**
	 * Sets the value set for a property.
	 * @param property the property name.
	 * @param valueSet the value set.
	 * @return this set.
	 */
	SELF setValueSet(String property, SELF valueSet);
	
	/**
	 * Checks if a property is a directive (has no values attached to it).
	 * @param property the property name.
	 * @return true if so, false if not.
	 */
	default boolean isDirective(String property)
	{
		return hasProperty(property) && getValues(property).length == 0;
	}

	/**
	 * Gets a value for a property as a String.
	 * @param property the property name.
	 * @param valueIndex the value index.
	 * @return the property's value as a String, or null if no value.
	 */
	default String getStringValue(String property, int valueIndex)
	{
		String[] values = getValues(property);
		return values.length > valueIndex ? values[valueIndex] : null;
	}
	
	/**
	 * Gets the first value for a property as a String.
	 * @param property the value property.
	 * @return the property's value as a String, or null if no value.
	 */
	default String getStringValue(String property)
	{
		return getStringValue(property, 0);
	}
	
	/**
	 * Gets a value for a property as a double.
	 * @param property the property name.
	 * @param valueIndex the value index.
	 * @return the property's value as a double, or NaN if no value, or not parseable as a double.
	 */
	default double getDoubleValue(String property, int valueIndex)
	{
		String[] values = getValues(property);
		return values.length > valueIndex ? ValueUtils.parseDouble(values[valueIndex], Double.NaN) : Double.NaN;
	}

	/**
	 * Gets the first value for a property as a double.
	 * @param property the property name.
	 * @return the property's value as a double, or NaN if no value, or not parseable as a double.
	 */
	default double getDoubleValue(String property)
	{
		return getDoubleValue(property, 0);
	}

	/**
	 * Gets a value for a property as an integer.
	 * @param property the property name.
	 * @param valueIndex the value index.
	 * @return the property's value as an integer, or 0 if no value, or not parseable as an integer.
	 */
	default int getIntValue(String property, int valueIndex)
	{
		String[] values = getValues(property);
		return values.length > valueIndex ? ValueUtils.parseInt(values[valueIndex], 0) : 0;
	}

	/**
	 * Gets a value for a property as an integer.
	 * @param property the property name.
	 * @return the property's value as an integer, or 0 if no value, or not parseable as an integer.
	 */
	default double getIntValue(String property)
	{
		return getIntValue(property, 0);
	}

}

