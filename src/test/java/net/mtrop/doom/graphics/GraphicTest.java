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
import java.io.IOException;

import javax.imageio.ImageIO;

import net.mtrop.doom.WadFile;
import net.mtrop.doom.util.GraphicUtils;

public final class GraphicTest
{
	public static void main(String[] args) throws IOException
	{
		WadFile wad = new WadFile(args[0]);

		Palette pal = wad.getDataAs("PLAYPAL", Palette.class);
		Flat f = Flat.create(64, 64, wad.getData("FWATER1"));
		Picture p = wad.getDataAs("TROOA1", Picture.class);
		EndDoom endoom = wad.getDataAs("ENDOOM", EndDoom.class);

		wad.close();

		BufferedImage fi = GraphicUtils.createImage(f, pal);
		BufferedImage pi = GraphicUtils.createImage(p, pal);
		BufferedImage ei = GraphicUtils.createImageForEndDoom(endoom, true);
		
		ImageIO.write(fi, "PNG", new File("out.png"));
		ImageIO.write(pi, "PNG", new File("out2.png"));
		ImageIO.write(ei, "PNG", new File("endoom.png"));
		
	}

}
