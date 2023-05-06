/*******************************************************************************
 * Copyright (c) 2015-2023 Matt Tropiano
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


public final class BlockmapTest
{
	public static void main(String[] args) throws IOException
	{
		Logger logger = LoggingFactory.createConsoleLoggerFor(BlockmapTest.class);
		WadFile wad = new WadFile(args[0]);
		Blockmap blockmap = wad.getDataAs("BLOCKMAP", Blockmap.class);
		logger.info(blockmap.toBytes());
		wad.close();
	}
}
