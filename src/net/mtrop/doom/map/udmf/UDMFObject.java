package net.mtrop.doom.map.udmf;

import com.blackrook.commons.AbstractMap;

/**
 * Main descriptor for all UDMF objects.
 * @author Matthew Tropiano
 */
public interface UDMFObject extends AbstractMap<String, Object>
{
	/**
	 * @return the type name of this UDMF structure. 
	 */
	public String getStructureType();
	
	/**
	 * Gets the boolean value of an arbitrary object attribute.
	 * Non-empty strings and non-zero numbers are <code>true</code>.
	 * 
	 * @param attributeName the attribute name (may be standardized, depending on implementation).
	 * @param value the attribute value.
	 * @throws NumberFormatException if the value was originally a String and can't be converted.
	 * @throws MapException if the attribute is not settable or the value is invalid.
	 */
	public void setBooleanAttribute(String attributeName, Boolean value);

	/**
	 * Gets the boolean value of an arbitrary object attribute.
	 * Non-empty strings and non-zero numbers are <code>true</code>.
	 * 
	 * @param attributeName the attribute name (may be standardized, depending on implementation).
	 * @return the integer value of an object attribute, or <code>null</code> if the attribute is not implemented nor exists.
	 * @throws NumberFormatException if the value was originally a String and can't be converted.
	 */
	public Boolean getBooleanAttribute(String attributeName);

	/**
	 * Gets the boolean value of an arbitrary object attribute.
	 * Non-empty strings and non-zero numbers are <code>true</code>.
	 * 
	 * @param attributeName the attribute name (may be standardized, depending on implementation).
	 * @param def the default value if one does not exist.
	 * @return the integer value of an object attribute, or <code>def</code> if the attribute is not implemented nor exists.
	 * @throws NumberFormatException if the value was originally a String and can't be converted.
	 */
	public Boolean getBooleanAttribute(String attributeName, Boolean def);

	/**
	 * Gets the integer value of an arbitrary object attribute.
	 * If the value is castable to Integer, it is cast to an Integer.
	 * <p>
	 * Strings are attempted to be parsed as integers.
	 * Floating-point values are chopped.
	 * Booleans are 1 if true, 0 if false.
	 * @param attributeName the attribute name (may be standardized, depending on implementation).
	 * @param value the attribute value.
	 * @throws NumberFormatException if the value was originally a String and can't be converted.
	 * @throws MapException if the attribute is not settable or the value is invalid.
	 */
	public void setIntegerAttribute(String attributeName, Integer value);

	/**
	 * Gets the integer value of an arbitrary object attribute.
	 * If the value is castable to Integer, it is cast to an Integer.
	 * <p>
	 * Strings are attempted to be parsed as integers.
	 * Floating-point values are chopped.
	 * Booleans are 1 if true, 0 if false.
	 * @param attributeName the attribute name (may be standardized, depending on implementation).
	 * @return the integer value of an object attribute, or <code>null</code> if the attribute is not implemented nor exists.
	 * @throws NumberFormatException if the value was originally a String and can't be converted.
	 */
	public Integer getIntegerAttribute(String attributeName);

	/**
	 * Gets the integer value of an arbitrary object attribute.
	 * If the value is castable to Integer, it is cast to an Integer.
	 * <p>
	 * Strings are attempted to be parsed as integers.
	 * Floating-point values are chopped.
	 * Booleans are 1 if true, 0 if false.
	 * @param attributeName the attribute name (may be standardized, depending on implementation).
	 * @param def the default value if one does not exist.
	 * @return the integer value of an object attribute, or <code>def</code> if the attribute is not implemented nor exists.
	 * @throws NumberFormatException if the value was originally a String and can't be converted.
	 */
	public Integer getIntegerAttribute(String attributeName, Integer def);

	/**
	 * Gets the integer value of an arbitrary object attribute.
	 * If the value is castable to Float, it is cast to a Float.
	 * <p>
	 * Strings are attempted to be parsed as floating point numbers. Integers are promoted.
	 * Booleans are 1.0 if true, 0.0 if false.
	 * @param attributeName the attribute name (may be standardized, depending on implementation).
	 * @param value the attribute value.
	 * @throws NumberFormatException if the value was originally a String and can't be converted.
	 * @throws MapException if the attribute is not settable or the value is invalid.
	 */
	public void setFloatAttribute(String attributeName, Float value);

	/**
	 * Gets the integer value of an arbitrary object attribute.
	 * If the value is castable to Float, it is cast to a Float.
	 * <p>
	 * Strings are attempted to be parsed as floating point numbers. Integers are promoted.
	 * Booleans are 1.0 if true, 0.0 if false.
	 * @param attributeName the attribute name (may be standardized, depending on implementation).
	 * @return the floating-point value of an object attribute, or <code>null</code> if the attribute is not implemented nor exists.
	 * @throws NumberFormatException if the value was originally a String and can't be converted.
	 */
	public Float getFloatAttribute(String attributeName);

	/**
	 * Gets the integer value of an arbitrary object attribute.
	 * If the value is castable to Float, it is cast to a Float.
	 * <p>
	 * Strings are attempted to be parsed as floating point numbers. Integers are promoted.
	 * Booleans are 1.0 if true, 0.0 if false.
	 * @param attributeName the attribute name (may be standardized, depending on implementation).
	 * @param def the default value if one does not exist.
	 * @return the floating-point value of an object attribute, or <code>def</code> if the attribute is not implemented nor exists.
	 * @throws NumberFormatException if the value was originally a String and can't be converted.
	 */
	public Float getFloatAttribute(String attributeName, Float def);

	/**
	 * Sets the string value of an arbitrary object attribute.
	 * If the value is promotable to String (integers/floats/booleans), it is promoted to a String.
	 * @param attributeName the attribute name (may be standardized, depending on implementation).
	 * @param value the attribute value.
	 * @throws MapException if the attribute is not settable or the value is invalid.
	 */
	public void setStringAttribute(String attributeName, String value);
	
	/**
	 * Gets the string value of an arbitrary object attribute.
	 * If the value is promotable to String (integers/floats/booleans), it is promoted to a String.
	 * @param attributeName the attribute name (may be standardized, depending on implementation).
	 * @return the string value of an object attribute, or <code>null</code> if the attribute is not implemented nor exists.
	 */
	public String getStringAttribute(String attributeName);
	
	/**
	 * Gets the string value of an arbitrary object attribute.
	 * If the value is promotable to String (integers/floats/booleans), it is promoted to a String.
	 * @param attributeName the attribute name (may be standardized, depending on implementation).
	 * @param def the default value if one does not exist.
	 * @return the string value of an object attribute, or <code>def</code> if the attribute is not implemented nor exists.
	 */
	public String getStringAttribute(String attributeName, String def);

}
