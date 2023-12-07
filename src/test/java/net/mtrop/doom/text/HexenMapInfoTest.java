package net.mtrop.doom.text;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import net.mtrop.doom.LoggingFactory;
import net.mtrop.doom.WadFile;
import net.mtrop.doom.LoggingFactory.Logger;
import net.mtrop.doom.sound.MUSTest;

public final class HexenMapInfoTest 
{
	public static void main(String[] args) throws Exception
	{
		Logger logger = LoggingFactory.createConsoleLoggerFor(MUSTest.class);
		WadFile wad = new WadFile(args[0]);
		HexenMapInfo info = wad.getTextDataAs("MAPINFO", StandardCharsets.US_ASCII, HexenMapInfo.class);
		wad.close();
		OutputStreamWriter writer = new OutputStreamWriter(System.out);
		info.writeText(writer);
		writer.flush();
	}

}
