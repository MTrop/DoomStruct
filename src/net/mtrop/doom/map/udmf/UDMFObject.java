/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map.udmf;

import com.blackrook.commons.Reflect;
import com.blackrook.commons.hash.CaseInsensitiveHashMap;

/**
 * Main descriptor for all UDMF objects.
 * @author Matthew Tropiano
 */
public class UDMFObject extends CaseInsensitiveHashMap<Object>
{
	/** Creates a new UDMFObject. */
	public UDMFObject()
	{
		super(DEFAULT_CAPACITY, 1.0f);
	}
	
	/**
	 * Gets the boolean value of an arbitrary object attribute.
	 * Non-empty strings and non-zero numbers are <code>true</code>.
	 * 
	 * @param attributeName the attribute name (may be standardized, depending on implementation).
	 * @param value the attribute value.
	 * @throws NumberFormatException if the value was originally a String and can't be converted.
	 */
	public void setBoolean(String attributeName, Boolean value)
	{
		put(attributeName, value);
	}

	/**
	 * Gets the boolean value of an arbitrary object attribute.
	 * Non-empty strings and non-zero numbers are <code>true</code>.
	 * 
	 * @param attributeName the attribute name (may be standardized, depending on implementation).
	 * @return the integer value of an object attribute, or <code>null</code> if the attribute is not implemented nor exists.
	 * @throws NumberFormatException if the value was originally a String and can't be converted.
	 */
	public Boolean getBoolean(String attributeName)
	{
		return getBoolean(attributeName, null);
	}

	/**
	 * Gets the boolean value of an arbitrary object attribute.
	 * Non-empty strings and non-zero numbers are <code>true</code>.
	 * 
	 * @param attributeName the attribute name (may be standardized, depending on implementation).
	 * @param def the default value if one does not exist.
	 * @return the integer value of an object attribute, or <code>def</code> if the attribute is not implemented nor exists.
	 * @throws NumberFormatException if the value was originally a String and can't be converted.
	 */
	public Boolean getBoolean(String attributeName, Boolean def)
	{
		Object obj = get(attributeName);
		if (obj == null)
			return def;
		return Reflect.createForType(attributeName, obj, Boolean.class);
	}

	/**
	 * Gets the integer value of an arbitrary object attribute.
	 * If the value is castable to Integer, it is cast to an Integer.
	 * <p>
	 * Strings are attempted to be parsed as integers.
	 * Floating-point values are chopped.
	 * Booleans are 1 if true, 0 if false.
	 * @param attributeName the attribute name (may be standardized, depending on implementation).
	 * @param value the attribute value.
	 */
	public void setInteger(String attributeName, Integer value)
	{
		put(attributeName, value);
	}

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
	public Integer getInteger(String attributeName)
	{
		return getInteger(attributeName, null);
	}

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
	public Integer getInteger(String attributeName, Integer def)
	{
		Object obj = get(attributeName);
		if (obj == null)
			return def;
		return Reflect.createForType(attributeName, obj, Integer.class);
	}

	/**
	 * Gets the integer value of an arbitrary object attribute.
	 * If the value is castable to Float, it is cast to a Float.
	 * <p>
	 * Strings are attempted to be parsed as floating point numbers. Integers are promoted.
	 * Booleans are 1.0 if true, 0.0 if false.
	 * @param attributeName the attribute name (may be standardized, depending on implementation).
	 * @param value the attribute value.
	 * @throws NumberFormatException if the value was originally a String and can't be converted.
	 */
	public void setFloat(String attributeName, Float value)
	{
		put(attributeName, value);
	}

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
	public Float getFloat(String attributeName)
	{
		return getFloat(attributeName, null);
	}

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
	public Float getFloat(String attributeName, Float def)
	{
		Object obj = get(attributeName);
		if (obj == null)
			return def;
		return Reflect.createForType(attributeName, obj, Float.class);
	}

	/**
	 * Sets the string value of an arbitrary object attribute.
	 * If the value is promotable to String (integers/floats/booleans), it is promoted to a String.
	 * @param attributeName the attribute name (may be standardized, depending on implementation).
	 * @param value the attribute value.
	 */
	public void setString(String attributeName, String value)
	{
		put(attributeName, value);
	}

	/**
	 * Gets the string value of an arbitrary object attribute.
	 * If the value is promotable to String (integers/floats/booleans), it is promoted to a String.
	 * @param attributeName the attribute name (may be standardized, depending on implementation).
	 * @return the string value of an object attribute, or <code>null</code> if the attribute is not implemented nor exists.
	 */
	public String getString(String attributeName)
	{
		return getString(attributeName, null);
	}
	
	/**
	 * Gets the string value of an arbitrary object attribute.
	 * If the value is promotable to String (integers/floats/booleans), it is promoted to a String.
	 * @param attributeName the attribute name (may be standardized, depending on implementation).
	 * @param def the default value if one does not exist.
	 * @return the string value of an object attribute, or <code>def</code> if the attribute is not implemented nor exists.
	 */
	public String getString(String attributeName, String def)
	{
		Object obj = get(attributeName);
		if (obj == null)
			return def;
		return String.valueOf(obj);
	}

}
