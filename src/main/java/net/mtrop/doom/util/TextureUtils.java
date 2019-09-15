/*******************************************************************************
 * Copyright (c) 2015-2019 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.util;

import java.io.IOException;

import net.mtrop.doom.Wad;
import net.mtrop.doom.exception.TextureException;
import net.mtrop.doom.exception.WadException;
import net.mtrop.doom.object.BinaryObject;
import net.mtrop.doom.struct.io.SerializerUtils;
import net.mtrop.doom.texture.CommonTextureList;
import net.mtrop.doom.texture.DoomTextureList;
import net.mtrop.doom.texture.PatchNames;
import net.mtrop.doom.texture.StrifeTextureList;
import net.mtrop.doom.texture.TextureSet;

/**
 * Graphics utility methods for image types.
 * @author Matthew Tropiano
 */
public final class TextureUtils
{
	private TextureUtils()
	{
	}
	
	/**
	 * Imports a {@link TextureSet} from a WAD File.
	 * This searches for the TEXTURE1/2 lumps and the PNAMES entry, and builds a new TextureSet
	 * from them. If the WAD does NOT contain a TEXTUREx entry, the returned set will be empty.
	 * If TEXTURE1/2 is present, but NOT PNAMES, a {@link TextureException} will be thrown.
	 * @param wf the WAD file to read from containing the required entries.
	 * @return a new texture set equivalent to the parsed data.
	 * @throws TextureException if a texture lump was found, but not PNAMES.
	 * @throws WadException if the WAD itself cannot be read.
	 * @throws IOException if an entry in a WAD file cannot be read.
	 */
	public static TextureSet importTextureSet(Wad wf) throws WadException, IOException
	{
		PatchNames patchNames = null;
		CommonTextureList<?> textureList1 = null;
		CommonTextureList<?> textureList2 = null;
		
		byte[] textureData = wf.getData("TEXTURE1");
		boolean isStrife = false;
		
		// figure out if Strife or Doom Texture Lump.
		if (TextureUtils.isStrifeTextureData(textureData))
		{
			textureList1 = BinaryObject.create(StrifeTextureList.class, textureData);
			isStrife = true;
		}
		else
		{
			textureList1 = BinaryObject.create(DoomTextureList.class, textureData);
			isStrife = false;
		}

		textureData = wf.getData("TEXTURE2");
		
		if (textureData != null)
		{
			if (isStrife)
				textureList2 = BinaryObject.create(StrifeTextureList.class, textureData);
			else
				textureList2 = BinaryObject.create(DoomTextureList.class, textureData);
		}
		
		textureData = wf.getData("PNAMES");
		if (textureData == null)
			throw new TextureException("Found TEXTUREx without PNAMES!\n");

		patchNames = BinaryObject.create(PatchNames.class, textureData);
		
		TextureSet out;
		
		if (textureList2 != null)
			out = new TextureSet(patchNames, textureList1, textureList2);
		else
			out = new TextureSet(patchNames, textureList1);

		return out;
	}

	/**
	 * Scans through texture lump data in order to detect whether it is for Strife or not.
	 * @param b the texture lump data.
	 * @return true if it is Strife texture data, false if not.
	 */
	public static boolean isStrifeTextureData(byte[] b)
	{
		int ptr = 0;
		byte[] buf = new byte[4];
	
		System.arraycopy(b, ptr, buf, 0, 4);
		int textureCount = SerializerUtils.bytesToInt(buf, 0, SerializerUtils.LITTLE_ENDIAN);
		ptr = (textureCount * 4) + 20;
		
		boolean good = true;
		while (ptr < b.length && good)
		{
			System.arraycopy(b, ptr, buf, 0, 4);
			
			// test for unused texture data.
			if (SerializerUtils.bytesToInt(buf, 0, SerializerUtils.LITTLE_ENDIAN) != 0)
				good = false;
	
			// test for unused patch data.
			else
			{
				ptr += 4;
				System.arraycopy(b, ptr, buf, 0, 2);
				int patches = SerializerUtils.bytesToInt(buf, 0, SerializerUtils.LITTLE_ENDIAN);
				ptr += 2;
				while (patches > 0)
				{
					ptr += 6;
					System.arraycopy(b, ptr, buf, 0, 4);
					int x = SerializerUtils.bytesToInt(buf, 0, SerializerUtils.LITTLE_ENDIAN);
					if (x > 1 || x < 0)
						good = false;
					ptr += 4;
					patches--;
				}
				ptr += 16;
			}
		}
		
		return !good;
	}

}
