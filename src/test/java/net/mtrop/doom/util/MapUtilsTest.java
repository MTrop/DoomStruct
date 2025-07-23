/*******************************************************************************
 * Copyright (c) 2015-2023 Matt Tropiano
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.util;

import java.io.IOException;

import net.mtrop.doom.Wad;
import net.mtrop.doom.WadEntry;
import net.mtrop.doom.WadFile;

public final class MapUtilsTest
{
	public static void main(String[] args) throws IOException
	{
		Wad wad = new WadFile(args[0]);
		for (WadEntry e : MapUtils.getMapEntries(wad, args[1]))
			System.out.println(e.getName());
		wad.close();
	}
}
