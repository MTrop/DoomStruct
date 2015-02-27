/*******************************************************************************
 * Copyright (c) 2015 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map.binary;

import java.io.IOException;
import java.io.InputStream;

import net.mtrop.doom.WadFile;

import com.blackrook.commons.Common;
import com.blackrook.commons.logging.Logger;
import com.blackrook.commons.logging.LoggingFactory;

public class HexenLinedefTest
{
	public static void main(String[] args) throws IOException
	{
		Logger logger = LoggingFactory.createConsoleLoggerFor(HexenLinedefTest.class);
		
		WadFile wad = new WadFile(args[0]);
		InputStream in = wad.getInputStream("LINEDEFS");

		int i = 0;
		byte[] b = new byte[16];
		while (in.read(b) > 0)
			logger.info((i++) + " " + HexenLinedef.create(b));
		
		Common.close(in);
		Common.close(wad);
	}
}
