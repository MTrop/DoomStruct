package net.mtrop.doom.map.binary;

import java.io.IOException;
import java.io.InputStream;

import net.mtrop.doom.WadFile;

import com.blackrook.commons.Common;
import com.blackrook.commons.logging.Logger;
import com.blackrook.commons.logging.LoggingFactory;

public class DoomSidedefTest
{
	public static void main(String[] args) throws IOException
	{
		Logger logger = LoggingFactory.createConsoleLoggerFor(DoomSidedefTest.class);
		
		WadFile wad = new WadFile(args[0]);
		InputStream in = wad.getDataAsStream("SIDEDEFS");

		int i = 0;
		byte[] b = new byte[30];
		while (in.read(b) > 0)
			logger.info((i++) + " " + DoomSidedef.create(b));
		
		Common.close(in);
		Common.close(wad);
	}
}
