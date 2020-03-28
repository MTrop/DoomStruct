/*******************************************************************************
 * Copyright (c) 2015-2020 Matt Tropiano
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map.udmf.attributes;

/**
 * Contains Hexen thing attributes on some UDMF structures.
 * @author Matthew Tropiano
 * @since 2.8.0
 */
public interface UDMFHexenThingAttributes extends UDMFCommonThingAttributes
{
	/** Thing position: height. */
	public static final String ATTRIB_HEIGHT = "height";
	
	/** Thing flag: Appears for class 1. */
	public static final String ATTRIB_FLAG_CLASS1 = "class1";
	/** Thing flag: Appears for class 2. */
	public static final String ATTRIB_FLAG_CLASS2 = "class2";
	/** Thing flag: Appears for class 3. */
	public static final String ATTRIB_FLAG_CLASS3 = "class3";
	
}
