package net.mtrop.doom;

import java.io.File;

public final class WadFileDeletionTest
{
	public static void main(String[] args) throws Exception
	{
		File f = new File("test.wad");
		WadFile wf = WadFile.createWadFile(f);
		
		wf.addData("README", new File("README.md"));
		wf.addData("BUILDXML", new File("build.xml"));
		wf.addData("PHILOSOP", new File("PHILOSOPHY.md"));
		wf.deleteEntry(2);
		
		wf.close();
		f.delete();
	}
}
