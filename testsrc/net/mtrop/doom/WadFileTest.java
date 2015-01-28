package net.mtrop.doom;

import com.blackrook.commons.Common;
import com.blackrook.commons.logging.Logger;
import com.blackrook.commons.logging.LoggingFactory;

public final class WadFileTest
{
	private static final Logger out = LoggingFactory.createConsoleLoggerFor(WadFileTest.class); 
	
	public static void main(String[] args) throws Exception
	{
		Wad wad;
		
		wad = new WadBuffer("H:\\DoomDev\\Iwads\\doom2.wad");
		for (WadEntry e : wad)
			out.info(e.toString());
		out.info("*************************************************************");
		wad = new WadFile("H:\\DoomDev\\Iwads\\doom2.wad");
		for (WadEntry e : wad)
			out.info(e.toString());
		Common.close((WadFile)wad);
		out.info("*************************************************************");
		wad = new WadMap("H:\\DoomDev\\Iwads\\doom2.wad");
		for (WadEntry e : wad)
			out.info(e.toString());
	}

}
