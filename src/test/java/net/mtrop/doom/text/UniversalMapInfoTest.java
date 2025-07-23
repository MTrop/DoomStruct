package net.mtrop.doom.text;

import java.io.File;
import java.io.OutputStreamWriter;

public final class UniversalMapInfoTest 
{
	public static void main(String[] args) throws Exception
	{
		UniversalMapInfo info = new UniversalMapInfo();
		info.readFile(new File(args[0]));

		OutputStreamWriter writer = new OutputStreamWriter(System.out);
		info.writeText(writer);
		writer.flush();
	}

}
