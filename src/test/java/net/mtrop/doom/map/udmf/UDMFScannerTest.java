/*******************************************************************************
 * Copyright (c) 2015-2022 Matt Tropiano
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map.udmf;

import java.io.IOException;
import java.io.InputStream;

import net.mtrop.doom.WadFile;

import net.mtrop.doom.LoggingFactory;
import net.mtrop.doom.LoggingFactory.LogLevel;
import net.mtrop.doom.LoggingFactory.Logger;


public class UDMFScannerTest
{
	public static void main(String[] args) throws IOException
	{
		Logger logger = LoggingFactory.createConsoleLoggerFor(UDMFScannerTest.class);
		logger.setLoggingLevel(LogLevel.INFO);
		
		WadFile wad = new WadFile(args[0]);
		InputStream in = wad.getInputStream("TEXTMAP");

		UDMFScanner scanner = UDMFScanner.createScanner(in);
		while (scanner.hasNext())
		{
			UDMFScanner.Element element = scanner.next();
			switch (element.getType())
			{
				case GLOBAL_ATTRIBUTE:
					logger.infof("ATTRIB: %s = \"%s\"", element.getName(), element.getValue().toString());
					break;
				case OBJECT:
					logger.infof("OBJECT: Type %s", element.getName());
					break;
			}
		}
		
		in.close();
		wad.close();
	}
}
