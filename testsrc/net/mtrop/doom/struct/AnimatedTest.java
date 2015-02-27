/*******************************************************************************
 * Copyright (c) 2015 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.struct;

import java.io.IOException;
import java.io.InputStream;

import net.mtrop.doom.WadFile;
import net.mtrop.doom.map.binary.StrifeThingTest;

import com.blackrook.commons.Common;
import com.blackrook.commons.logging.Logger;
import com.blackrook.commons.logging.LoggingFactory;

public final class AnimatedTest
{
	public static void main(String[] args) throws IOException
	{
		Logger logger = LoggingFactory.createConsoleLoggerFor(AnimatedTest.class);
		
		WadFile wad = new WadFile(args[0]);
		InputStream in = wad.getInputStream("ANIMATED");

		Animated animated = Animated.read(in);
		for (Animated.Entry entry : animated)
			logger.info(entry);
		
		Common.close(in);
		Common.close(wad);
	}
}
