package net.mtrop.doom;

import com.blackrook.commons.Common;
import com.blackrook.commons.logging.Logger;
import com.blackrook.commons.logging.LoggingFactory;

public final class PK3FileTest
{
	private static final Logger out = LoggingFactory.createConsoleLoggerFor(PK3FileTest.class); 
	
	public static void main(String[] args) throws Exception
	{
		DoomPK3 pk3 = new DoomPK3(args[0]);
		for (String path : pk3.getEntriesStartingWith("sprites/"))
			out.info(path);
		
		Common.close(pk3);
	}

}
