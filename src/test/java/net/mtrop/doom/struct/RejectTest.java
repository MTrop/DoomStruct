/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.struct;

import java.io.IOException;

import net.mtrop.doom.BinaryObject;
import net.mtrop.doom.WadFile;
import net.mtrop.doom.map.binary.DoomSector;
import net.mtrop.doom.map.bsp.BSPReject;

import net.mtrop.doom.util.Utils;

public final class RejectTest
{
	public static void main(String[] args) throws IOException
	{
		WadFile wad = new WadFile(args[0]);
		byte[] data = wad.getData("SECTORS");
		byte[] data2 = wad.getData("REJECT");

		DoomSector[] sectors = BinaryObject.create(DoomSector.class, data, data.length / 26);
		BSPReject reject = new BSPReject(sectors.length);
		reject.fromBytes(data2);
		
		Utils.close(wad);
	}
}
