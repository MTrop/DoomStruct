package net.mtrop.doom.struct;

import java.io.IOException;

import net.mtrop.doom.WadFile;
import net.mtrop.doom.map.binary.DoomSector;
import net.mtrop.doom.map.bsp.BSPReject;

import com.blackrook.commons.Common;
import com.blackrook.commons.logging.Logger;
import com.blackrook.commons.logging.LoggingFactory;

public final class RejectTest
{
	public static void main(String[] args) throws IOException
	{
		Logger logger = LoggingFactory.createConsoleLoggerFor(RejectTest.class);
		
		WadFile wad = new WadFile(args[0]);
		byte[] data = wad.getData("SECTORS");
		byte[] data2 = wad.getData("REJECT");

		DoomSector[] sectors = DoomSector.create(data, data.length / 26);
		BSPReject reject = new BSPReject(sectors.length);
		reject.fromBytes(data2);
		
		Common.close(wad);
	}
}
