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
		InputStream in = wad.getDataAsStream("TEXTMAP");

		UDMFTable udmftable = UDMFReader.readData(in);
		StringWriter writer = new StringWriter();
		UDMFWriter.writeData(udmftable, writer);
		logger.info("\n"+writer.toString());
		
		Common.close(in);
		Common.close(wad);
	}
}
