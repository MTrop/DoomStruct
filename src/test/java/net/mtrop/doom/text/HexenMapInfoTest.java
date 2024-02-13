package net.mtrop.doom.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import net.mtrop.doom.LoggingFactory;
import net.mtrop.doom.WadFile;
import net.mtrop.doom.LoggingFactory.Logger;
import net.mtrop.doom.sound.MUSTest;

public final class HexenMapInfoTest 
{
	public static void main(String[] args) throws Exception
	{
		Logger logger = LoggingFactory.createConsoleLoggerFor(HexenMapInfoTest.class);
		HexenMapInfoParser info = new HexenMapInfoParser();
		info.readFile(new File(args[0]));
		
		String[] out;
		while ((out = info.nextTokens()) != null)
			logger.info(Arrays.toString(out));
		
		OutputStreamWriter writer = new OutputStreamWriter(System.out);
		info.writeText(writer);
		writer.flush();
	}

}
