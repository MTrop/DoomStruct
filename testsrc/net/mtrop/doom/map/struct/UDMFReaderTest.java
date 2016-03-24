/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map.struct;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import net.mtrop.doom.WadFile;
import net.mtrop.doom.map.udmf.UDMFReader;
import net.mtrop.doom.map.udmf.UDMFTable;
import net.mtrop.doom.map.udmf.UDMFWriter;

import com.blackrook.commons.Common;
import com.blackrook.commons.logging.Logger;
import com.blackrook.commons.logging.LoggingFactory;

public class UDMFReaderTest
{
	public static void main(String[] args) throws IOException
	{
		Logger logger = LoggingFactory.createConsoleLoggerFor(UDMFReaderTest.class);
		
		WadFile wad = new WadFile(args[0]);
		InputStream in = wad.getInputStream("TEXTMAP");

		UDMFTable udmftable = UDMFReader.readData(in);
		StringWriter writer = new StringWriter();
		UDMFWriter.writeData(udmftable, writer);
		logger.info("\n"+writer.toString());
		
		Common.close(in);
		Common.close(wad);
	}
}
