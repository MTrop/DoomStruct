package net.mtrop.doom.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
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
		HexenMapInfo info = new HexenMapInfo();
		info.readFile(new File(args[0]));
		
		OutputStreamWriter writer = new OutputStreamWriter(System.out);
		info.writeText(writer);
		writer.flush();
	}

}
