/*******************************************************************************
 * Copyright (c) 2015-2023 Matt Tropiano
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.graphics;

import java.io.File;
import java.io.IOException;

import net.mtrop.doom.object.BinaryObject;
import net.mtrop.doom.util.GraphicUtils;
import net.mtrop.doom.util.WadUtils;

public final class GraphicDirTest
{
	public static void main(String[] args) throws IOException
	{
		Palette pal = WadUtils.openWadAndGet(args[0], 
			(wad) -> wad.getDataAs("PLAYPAL", Palette.class)
		);

		for (File f : (new File(args[1])).listFiles((file)->file.getName().toLowerCase().endsWith(".png")))
		{
			File out = new File(f.getParent() + File.separator + f.getName() + ".lmp");
			GraphicUtils.createPicture(BinaryObject.read(PNGPicture.class, f), pal).writeFile(out);
		}
	}

}
