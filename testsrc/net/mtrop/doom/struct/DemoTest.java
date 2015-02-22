package net.mtrop.doom.struct;

import java.io.IOException;
import java.io.InputStream;

import net.mtrop.doom.WadFile;

import com.blackrook.commons.Common;
import com.blackrook.commons.logging.Logger;
import com.blackrook.commons.logging.LoggingFactory;

public final class DemoTest
{
	public static void main(String[] args) throws IOException
	{
		Logger logger = LoggingFactory.createConsoleLoggerFor(DemoTest.class);
		
		WadFile wad = new WadFile(args[0]);
		InputStream in = wad.getInputStream("DEMO1");

		Demo demo = Demo.read(in);
		logger.info(demo);
		for (Demo.Tic[] tics : demo.getTics())
			for (Demo.Tic tic : tics)
				logger.info(tic);
				
		
		Common.close(in);
		Common.close(wad);
	}
}
