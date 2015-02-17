package net.mtrop.doom.struct;

import java.io.IOException;
import java.io.InputStream;

import net.mtrop.doom.WadFile;
import net.mtrop.doom.map.binary.StrifeThingTest;

import com.blackrook.commons.Common;
import com.blackrook.commons.logging.Logger;
import com.blackrook.commons.logging.LoggingFactory;

public final class AnimatedTest
{
	public static void main(String[] args) throws IOException
	{
		Logger logger = LoggingFactory.createConsoleLoggerFor(AnimatedTest.class);
		
		WadFile wad = new WadFile(args[0]);
		InputStream in = wad.getDataAsStream("ANIMATED");

		Animated animated = Animated.read(in);
		for (Animated.Entry entry : animated)
			logger.info(entry);
		
		Common.close(in);
		Common.close(wad);
	}
}
