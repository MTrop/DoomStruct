/*******************************************************************************
 * Copyright (c) 2015-2022 Matt Tropiano
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map.data;

import java.io.IOException;

import net.mtrop.doom.WadFile;
import net.mtrop.doom.map.data.DoomSector;
import net.mtrop.doom.map.data.Reject;

public final class RejectTest
{
	public static void main(String[] args) throws IOException
	{
		WadFile wad = new WadFile(args[0]);
		DoomSector[] sectors = wad.getDataAs("SECTORS", DoomSector.class, DoomSector.LENGTH);
		Reject reject = new Reject(sectors.length);
		reject.fromBytes(wad.getData("REJECT"));
		
		wad.close();
	}
}
