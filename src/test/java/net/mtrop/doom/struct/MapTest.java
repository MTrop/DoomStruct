/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.struct;

import java.io.IOException;

import net.mtrop.doom.WadFile;
import net.mtrop.doom.enums.MapFormat;
import net.mtrop.doom.exception.MapException;
import net.mtrop.doom.util.MapUtils;

import com.blackrook.commons.Common;
import com.blackrook.commons.logging.Logger;
import com.blackrook.commons.logging.LoggingFactory;
import com.blackrook.lang.json.JSONWriter;

public final class MapTest
{
	public static void main(String[] args) throws MapException, IOException
	{
		Logger logger = LoggingFactory.createConsoleLoggerFor(MapTest.class);
		
		WadFile wad = new WadFile(args[0]);
		int[] mapindices = MapUtils.getAllMapIndices(wad);
		for (int i : mapindices)
		{
			MapFormat format = MapUtils.getMapFormat(wad, i);
			logger.info(wad.getEntry(i).getName() + " " + format);
			switch (format)
			{
				case DOOM:
					logger.info(JSONWriter.writeJSONString(MapUtils.createDoomMap(wad, i)));
					break;
				case STRIFE:
					logger.info(JSONWriter.writeJSONString(MapUtils.createStrifeMap(wad, i)));
					break;
				case HEXEN:
					logger.info(JSONWriter.writeJSONString(MapUtils.createHexenMap(wad, i)));
					break;
			}
			
		}
		Common.close(wad);
	}
}
