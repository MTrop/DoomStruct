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
import net.mtrop.doom.texture.DoomTextureList;
import net.mtrop.doom.texture.PatchNames;
import net.mtrop.doom.texture.TextureSet;
import net.mtrop.doom.texture.TextureSet.Patch;
import net.mtrop.doom.texture.TextureSet.Texture;
import net.mtrop.doom.LoggingFactory;
import net.mtrop.doom.LoggingFactory.Logger;
import net.mtrop.doom.util.Utils;

public final class TextureTest
{
	public static void main(String[] args) throws IOException
	{
		Logger logger = LoggingFactory.createConsoleLoggerFor(TextureTest.class);
		
		WadFile wad = new WadFile(args[0]);
		TextureSet set = new TextureSet(
			wad.getDataAs("PNAMES", PatchNames.class), 
			wad.getDataAs("TEXTURE1", DoomTextureList.class)
		);
		Utils.close(wad);
		
		for (Texture tex : set)
		{
			logger.infof("%-8s %dx%d %d patches", tex.getName(), tex.getWidth(), tex.getHeight(), tex.getPatchCount());
			for (Patch patch : tex)
				logger.infof("\t%-8s (%d, %d)", patch.getName(), patch.getOriginX(), patch.getOriginY());
		}
	}
}
