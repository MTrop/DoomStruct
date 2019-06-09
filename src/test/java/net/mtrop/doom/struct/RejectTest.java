/*******************************************************************************
 * Copyright (c) 2015-2019 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.struct;

import java.io.IOException;

import net.mtrop.doom.WadFile;

import net.mtrop.doom.map.bsp.BSPReject;
import net.mtrop.doom.map.data.DoomSector;

public final class RejectTest
{
	public static void main(String[] args) throws IOException
	{
		WadFile wad = new WadFile(args[0]);
		DoomSector[] sectors = wad.getDataAs("SECTORS", DoomSector.class, DoomSector.LENGTH);
		BSPReject reject = new BSPReject(sectors.length);
		reject.fromBytes(wad.getData("REJECT"));
		
		wad.close();
	}
}
