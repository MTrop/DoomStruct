/*******************************************************************************
 * Copyright (c) 2015-2020 Matt Tropiano
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map.data;

import java.io.IOException;

import net.mtrop.doom.WadFile;
import net.mtrop.doom.LoggingFactory;
import net.mtrop.doom.LoggingFactory.Logger;

import net.mtrop.doom.map.data.DoomSidedef;

public class DoomSidedefTest
{
	public static void main(String[] args) throws IOException
	{
		Logger logger = LoggingFactory.createConsoleLoggerFor(DoomSidedefTest.class);
		
		WadFile wad = new WadFile(args[0]);
		int i = 0;
		for (DoomSidedef object : wad.getDataAs("SIDEDEFS", DoomSidedef.class, DoomSidedef.LENGTH))
			logger.info((i++) + " " + object);
		wad.close();
	}
}
