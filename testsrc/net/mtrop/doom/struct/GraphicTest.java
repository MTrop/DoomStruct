package net.mtrop.doom.struct;

import java.awt.image.BufferedImage;
import java.io.IOException;

import net.mtrop.doom.WadFile;
import net.mtrop.doom.graphics.Flat;
import net.mtrop.doom.graphics.Picture;
import net.mtrop.doom.util.GraphicUtils;

import com.blackrook.commons.Common;

public final class GraphicTest
{
	public static void main(String[] args) throws IOException
	{
		WadFile wad = new WadFile(args[0]);

		Palette pal = Palette.create(wad.getData("PLAYPAL"));
		Flat f = Flat.create(320, 200, wad.getData("TITLEPIC"));
		Picture p = Picture.create(wad.getData("TROOA1"));

		Common.close(wad);

		BufferedImage fi = GraphicUtils.createImage(f, pal);
		BufferedImage pi = GraphicUtils.createImage(p, pal);
		
	}

}
