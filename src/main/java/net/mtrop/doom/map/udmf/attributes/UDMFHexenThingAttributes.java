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
public interface UDMFHexenThingAttributes extends UDMFDoomThingAttributes
{
	/** Thing position: height. */
	public static final String ATTRIB_HEIGHT = "height";
	
	/** Thing flag: Appears for class 1. */
	public static final String ATTRIB_FLAG_CLASS1 = "class1";
	/** Thing flag: Appears for class 2. */
	public static final String ATTRIB_FLAG_CLASS2 = "class2";
	/** Thing flag: Appears for class 3. */
	public static final String ATTRIB_FLAG_CLASS3 = "class3";

	/** 
	 * Linedef id. 
	 * @since 2.8.1
	 */
	public static final String ATTRIB_ID = "id";
	/** 
	 * Linedef Special type. 
	 * @since 2.8.1
	 */
	public static final String ATTRIB_SPECIAL = "special";

	/** 
	 * Linedef special argument 0. 
	 * @since 2.8.1
	 */
	public static final String ATTRIB_ARG0 = "arg0";
	/** 
	 * Linedef special argument 1. 
	 * @since 2.8.1
	 */
	public static final String ATTRIB_ARG1 = "arg1";
	/** 
	 * Linedef special argument 2. 
	 * @since 2.8.1
	 */
	public static final String ATTRIB_ARG2 = "arg2";
	/** 
	 * Linedef special argument 3. 
	 * @since 2.8.1
	 */
	public static final String ATTRIB_ARG3 = "arg3";
	/** 
	 * Linedef special argument 4. 
	 * @since 2.8.1
	 */
	public static final String ATTRIB_ARG4 = "arg4";

}
