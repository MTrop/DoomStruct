package net.mtrop.doom;

public final class WadFileTest
{
	public static void main(String[] args) throws Exception
	{
		Wad wad = new WadMap("H:\\DoomDev\\Iwads\\doom2.wad");
		for (WadEntry e : wad)
			System.out.println(e);
	}

}
