/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.enums;

/**
 * Enumeration of internal map format types.
 * @author Matthew Tropiano
 */
public enum MapFormat
{
	/** Format commonly used by Doom, Boom, and MBF/SMMU ports, plus Heretic. */
	DOOM,
	/** Format commonly used by Hexen and ZDoom-derivative ports. */
	HEXEN,
	/** Format commonly used by all extensible ports. */
	UDMF;
}
