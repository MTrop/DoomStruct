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
