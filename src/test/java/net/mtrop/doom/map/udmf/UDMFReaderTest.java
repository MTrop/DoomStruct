/*******************************************************************************
 * Copyright (c) 2015-2020 Matt Tropiano
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map.udmf;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import net.mtrop.doom.WadFile;
import net.mtrop.doom.map.udmf.UDMFReader;
import net.mtrop.doom.map.udmf.UDMFTable;
import net.mtrop.doom.map.udmf.UDMFWriter;

import net.mtrop.doom.LoggingFactory;
import net.mtrop.doom.LoggingFactory.Logger;


public class UDMFReaderTest
{
	public static void main(String[] args) throws IOException
	{
		Logger logger = LoggingFactory.createConsoleLoggerFor(UDMFReaderTest.class);
		
		WadFile wad = new WadFile(args[0]);
		InputStream in = wad.getInputStream("TEXTMAP");

		UDMFTable udmftable = UDMFReader.readData(wad.getInputStream("TEXTMAP"));
		StringWriter writer = new StringWriter();
		UDMFWriter.writeTable(udmftable, writer);
		logger.info("\n"+writer.toString());
		
		in.close();
		wad.close();
	}
}
