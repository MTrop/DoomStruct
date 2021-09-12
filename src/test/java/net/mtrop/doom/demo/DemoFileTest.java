/*******************************************************************************
 * Copyright (c) 2015-2020 Matt Tropiano
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.demo;

import java.io.File;
import java.io.IOException;

import net.mtrop.doom.WadFile;
import net.mtrop.doom.demo.Demo;
import net.mtrop.doom.object.BinaryObject;
import net.mtrop.doom.LoggingFactory;
import net.mtrop.doom.LoggingFactory.Logger;

public final class DemoFileTest
{
	public static void main(String[] args) throws IOException
	{
		Logger logger = LoggingFactory.createConsoleLoggerFor(DemoFileTest.class);
		
		Demo demo = BinaryObject.read(Demo.class, new File(args[0]));
		logger.info(demo);
		for (Demo.Tic[] tics : demo)
			for (Demo.Tic tic : tics)
				logger.info(tic);
	}
}
