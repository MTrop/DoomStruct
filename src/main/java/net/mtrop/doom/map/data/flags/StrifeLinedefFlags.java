/*******************************************************************************
 * Copyright (c) 2015-2020 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map.data.flags;

/**
 * Linedef flag constants for Strife.
 * The constant value is how many places to bit shift 1 to equal the flag bit.  
 * @author Matthew Tropiano
 */
public interface StrifeLinedefFlags extends DoomLinedefFlags
{
	/** Linedef flag: Line is a jump-over railing. */
	public static final int RAILING = 9;
	/** Linedef flag: Line blocks floating actors. */
	public static final int BLOCK_FLOATERS = 10;
	
}
