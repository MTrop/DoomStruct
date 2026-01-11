/*******************************************************************************
 * Copyright (c) 2015-2026 Matt Tropiano
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.graphics;

import java.io.IOException;

import net.mtrop.doom.WadFile;
import net.mtrop.doom.object.BinaryObject;

public final class PictureTest
{
	public static void main(String[] args) throws IOException
	{
		WadFile wad = new WadFile(args[0]);

		byte[] data = wad.getData("CPOSC1");
		Picture p = BinaryObject.create(Picture.class, data);
		byte[] picdata = p.toBytes();
		Picture p2 = BinaryObject.create(Picture.class, picdata);
		
		for (int y = 0; y < p2.getHeight(); y++)
			for (int x = 0; x < p2.getWidth(); x++)
				if (p.getPixel(x, y) != p2.getPixel(x, y))
					System.out.println("(" + x + ", " + y + "): " + p.getPixel(x, y) + " vs " + p2.getPixel(x, y));
		wad.close();
	}
	
}
