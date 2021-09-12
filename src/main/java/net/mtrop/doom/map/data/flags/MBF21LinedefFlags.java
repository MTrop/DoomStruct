/*******************************************************************************
 * Copyright (c) 2015-2020 Matt Tropiano
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map.data.flags;

/**
 * Linedef flag constants for MBF21.
 * The constant value is how many places to bit shift 1 to equal the flag bit.  
 * @author Matthew Tropiano
 * @since [NOW]
 */
public interface MBF21LinedefFlags extends BoomLinedefFlags
{
	/** Linedef flag: Block land monsters. */
	public static final int BLOCK_LAND_MONSTERS = 12;
	/** Linedef flag: Block players. */
	public static final int BLOCK_PLAYERS = 13;
}
