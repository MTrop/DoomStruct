/*******************************************************************************
 * Copyright (c) 2015-2023 Matt Tropiano
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.texture;

import java.io.IOException;

import net.mtrop.doom.texture.TextureSet.Patch;
import net.mtrop.doom.texture.TextureSet.Texture;
import net.mtrop.doom.util.WadUtils;
import net.mtrop.doom.LoggingFactory;
import net.mtrop.doom.LoggingFactory.Logger;


public final class TextureTest
{
	public static void main(String[] args) throws IOException
	{
		Logger logger = LoggingFactory.createConsoleLoggerFor(TextureTest.class);
		
		TextureSet set = WadUtils.openWadAndGet(args[0], (wad) ->
			new TextureSet(
				wad.getDataAs("PNAMES", PatchNames.class), 
				wad.getDataAs("TEXTURE1", DoomTextureList.class)
			)
		);
		
		for (Texture tex : set)
		{
			logger.infof("%-8s %dx%d %d patches", tex.getName(), tex.getWidth(), tex.getHeight(), tex.getPatchCount());
			for (Patch patch : tex)
				logger.infof("\t%-8s (%d, %d)", patch.getName(), patch.getOriginX(), patch.getOriginY());
		}
	}
}
