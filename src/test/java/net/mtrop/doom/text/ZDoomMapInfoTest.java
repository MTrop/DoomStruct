package net.mtrop.doom.text;

import java.io.File;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import net.mtrop.doom.LoggingFactory;
import net.mtrop.doom.WadFile;
import net.mtrop.doom.LoggingFactory.Logger;
import net.mtrop.doom.sound.MUSTest;

public final class ZDoomMapInfoTest 
{
	public static void main(String[] args) throws Exception
	{
		Logger logger = LoggingFactory.createConsoleLoggerFor(ZDoomMapInfoTest.class);
		ZDoomMapInfoParser info = new ZDoomMapInfoParser();
		info.readFile(new File(args[0]));

		OutputStreamWriter writer = new OutputStreamWriter(System.out);
		info.writeText(writer);
		writer.flush();
	}

}
