package net.mtrop.doom.text;

import java.io.File;
import java.io.OutputStreamWriter;

public final class ZDoomMapInfoTest 
{
	public static void main(String[] args) throws Exception
	{
		ZDoomMapInfo info = new ZDoomMapInfo();
		info.readFile(new File(args[0]));

		OutputStreamWriter writer = new OutputStreamWriter(System.out);
		info.writeText(writer);
		writer.flush();
	}

}
