package net.mtrop.doom.struct;

import java.io.IOException;
import java.io.InputStream;

import net.mtrop.doom.WadFile;

import com.blackrook.commons.Common;
import com.blackrook.commons.logging.Logger;
import com.blackrook.commons.logging.LoggingFactory;

public final class PaletteTest
{
	public static void main(String[] args) throws IOException
	{
		Logger logger = LoggingFactory.createConsoleLoggerFor(PaletteTest.class);
		
		WadFile wad = new WadFile(args[0]);
		InputStream in = wad.getInputStream("PLAYPAL");

		logger.info(Palette.read(in));
			
		Common.close(in);
		Common.close(wad);
	}
}
