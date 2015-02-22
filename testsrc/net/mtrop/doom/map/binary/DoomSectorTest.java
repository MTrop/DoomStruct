package net.mtrop.doom.map.binary;

import java.io.IOException;
import java.io.InputStream;

import net.mtrop.doom.WadFile;

import com.blackrook.commons.Common;
import com.blackrook.commons.logging.Logger;
import com.blackrook.commons.logging.LoggingFactory;

public class DoomSectorTest
{
	public static void main(String[] args) throws IOException
	{
		Logger logger = LoggingFactory.createConsoleLoggerFor(DoomSectorTest.class);
		
		WadFile wad = new WadFile(args[0]);
		InputStream in = wad.getInputStream("SECTORS");

		int i = 0;
		byte[] b = new byte[26];
		while (in.read(b) > 0)
		{
			DoomSector object = DoomSector.create(b);
			logger.info((i++) + " " + object);
		}
		
		Common.close(in);
		Common.close(wad);
	}
}
