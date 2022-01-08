/*******************************************************************************
 * Copyright (c) 2015-2022 Matt Tropiano
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.mtrop.doom.WadFile;
import net.mtrop.doom.graphics.EndDoom;
import net.mtrop.doom.graphics.Flat;
import net.mtrop.doom.graphics.Palette;
import net.mtrop.doom.graphics.Picture;
import net.mtrop.doom.object.BinaryObject;
import net.mtrop.doom.util.GraphicUtils;

public final class TallGraphicTest
{
	public static void main(String[] args) throws IOException
	{
		WadFile wad = new WadFile(args[0]);
		Palette pal = wad.getDataAs("PLAYPAL", Palette.class);
		wad.close();

		GraphicUtils.createPicture(ImageIO.read(new File(args[1])), pal).writeFile(new File("junk.lmp"));
		ImageIO.write(GraphicUtils.createImage(BinaryObject.read(Picture.class, new File("junk.lmp")), pal), "png", new File("junk.png"));
	}

}
