package net.mtrop.doom.text;

import java.io.File;
import java.io.OutputStreamWriter;

public final class EternityMapInfoTest 
{
	public static void main(String[] args) throws Exception
	{
		EternityMapInfo info = new EternityMapInfo();
		info.readFile(new File(args[0]));

		OutputStreamWriter writer = new OutputStreamWriter(System.out);
		info.writeText(writer);
		writer.flush();
	}

}
