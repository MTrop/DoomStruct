package net.mtrop.doom.struct;

import java.io.IOException;

import net.mtrop.doom.WadFile;

import com.blackrook.commons.Common;
import com.blackrook.commons.logging.Logger;
import com.blackrook.commons.logging.LoggingFactory;

public final class MUSTest
{
	public static void main(String[] args) throws IOException
	{
		Logger logger = LoggingFactory.createConsoleLoggerFor(MUSTest.class);
		
		WadFile wad = new WadFile(args[0]);
		DMXMUS mus = DMXMUS.create(wad.getData("D_RUNNIN"));
		
		for (DMXMUS.Event event : mus.getEventList())
			logger.info(event);
		
		Common.close(wad);
	}
}
