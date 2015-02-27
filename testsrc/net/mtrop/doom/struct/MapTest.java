/*******************************************************************************
 * Copyright (c) 2015 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.struct;

import java.io.IOException;

import net.mtrop.doom.WadFile;
import net.mtrop.doom.texture.DoomTexture;
import net.mtrop.doom.texture.DoomTextureList;
import net.mtrop.doom.texture.PatchNames;

import com.blackrook.commons.Common;
import com.blackrook.commons.logging.Logger;
import com.blackrook.commons.logging.LoggingFactory;

public final class MapTest
{
	public static void main(String[] args) throws IOException
	{
		Logger logger = LoggingFactory.createConsoleLoggerFor(MapTest.class);
		
		WadFile wad = new WadFile(args[0]);
		
		DoomTextureList texture1 = DoomTextureList.create(wad.getData("TEXTURE1"));
		//DoomTextureList texture2 = DoomTextureList.create(wad.getData("TEXTURE2"));
		PatchNames pnames = PatchNames.create(wad.getData("PNAMES"));

		Common.close(wad);

		for (DoomTexture tex : texture1)
		{
			logger.infof("%-8s %dx%d %d patches", tex.getName(), tex.getWidth(), tex.getHeight(), tex.getPatchCount());
			for (DoomTexture.Patch patch : tex)
				logger.infof("\t%-8s (%d, %d)", pnames.getByIndex(patch.getPatchIndex()), patch.getOriginX(), patch.getOriginY());
		}
		/*
		for (DoomTexture tex : texture2)
		{
			logger.infof("%-8s %dx%d %d patches", tex.getName(), tex.getWidth(), tex.getHeight(), tex.getPatchCount());
			for (DoomTexture.Patch patch : tex)
				logger.infof("\t%-8s (%d, %d)", pnames.getByIndex(patch.getPatchIndex()), patch.getOriginX(), patch.getOriginY());
		}
		*/
	}
}
