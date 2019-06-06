/*******************************************************************************
 * Copyright (c) 2015-2019 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map.binary;

import java.io.IOException;

import net.mtrop.doom.WadFile;
import net.mtrop.doom.LoggingFactory;
import net.mtrop.doom.LoggingFactory.Logger;
import net.mtrop.doom.util.Utils;

public class StrifeThingTest
{
	public static void main(String[] args) throws IOException
	{
		Logger logger = LoggingFactory.createConsoleLoggerFor(StrifeThingTest.class);
		
		WadFile wad = new WadFile(args[0]);
		int i = 0;
		for (StrifeThing object : wad.getDataAs("THINGS", StrifeThing.class, StrifeThing.LENGTH))
			logger.info((i++) + " " + object);
		Utils.close(wad);
	}
}
