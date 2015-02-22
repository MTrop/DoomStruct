package net.mtrop.doom.struct;

import java.io.IOException;
import java.io.InputStream;

import net.mtrop.doom.WadFile;
import net.mtrop.doom.map.bsp.BSPBlockmap;

import com.blackrook.commons.Common;
import com.blackrook.commons.logging.Logger;
import com.blackrook.commons.logging.LoggingFactory;

public final class BlockmapTest
{
	public static void main(String[] args) throws IOException
	{
		Logger logger = LoggingFactory.createConsoleLoggerFor(BlockmapTest.class);
		
		WadFile wad = new WadFile(args[0]);
		InputStream in = wad.getInputStream("BLOCKMAP");

		BSPBlockmap blockmap = BSPBlockmap.read(in);
		
		Common.close(in);
		Common.close(wad);
	}
}
