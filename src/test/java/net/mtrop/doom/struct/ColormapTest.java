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
import net.mtrop.doom.graphics.Colormap;
import net.mtrop.doom.LoggingFactory;
import net.mtrop.doom.LoggingFactory.Logger;
import net.mtrop.doom.util.Utils;

public final class ColormapTest
{
	public static void main(String[] args) throws IOException
	{
		Logger logger = LoggingFactory.createConsoleLoggerFor(ColormapTest.class);
		WadFile wad = new WadFile(args[0]);
		for (Colormap colormap : wad.getDataAs("COLORMAP", Colormap.class, Colormap.LENGTH))
			logger.info(colormap);
		Utils.close(wad);
	}
}
