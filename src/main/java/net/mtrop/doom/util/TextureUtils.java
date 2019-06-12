/*******************************************************************************
 * Copyright (c) 2015-2019 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.util;

import java.io.IOException;
import java.util.Set;

import net.mtrop.doom.BinaryObject;
import net.mtrop.doom.Wad;
import net.mtrop.doom.exception.TextureException;
import net.mtrop.doom.exception.WadException;
import net.mtrop.doom.texture.CommonPatch;
import net.mtrop.doom.texture.CommonTexture;
import net.mtrop.doom.texture.CommonTextureList;
import net.mtrop.doom.texture.DoomTextureList;
import net.mtrop.doom.texture.PatchNames;
import net.mtrop.doom.texture.StrifeTextureList;
import net.mtrop.doom.texture.TextureSet;
import net.mtrop.doom.texture.TextureSet.Texture;
import net.mtrop.doom.texture.TextureSet.Patch;

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
		if (WadUtils.isStrifeTextureData(textureData))
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
	 * Exports a {@link TextureSet}'s contents into a PNAMES and TEXTUREx lump.
	 * This looks up patch indices as it exports - if a patch name does not exist in <code>pnames</code>,
	 * it is added.
	 * <p>
	 * In the end, <code>pnames</code> and <code>texture1</code>/<code>texture2</code> will be the objects whose contents will change.
	 * @param <P> the inferred patch type of the provided TextureLists.
	 * @param <T> the inferred texture type of the provided TextureLists.
	 * @param textureSet the set of textures to export.
	 * @param pnames the patch names lump.
	 * @param texture1 the first texture list to write to.
	 * @param texture2 the second texture list to write to. Can be null.
	 * @param texture1NameSet the set of texture names that will be written to the first texture list. Can be null (exports all names to <code>texture1</code>).
	 */
	public static <P extends CommonPatch, T extends CommonTexture<P>> void exportTextureSet(
			TextureSet textureSet, 
			PatchNames pnames, 
			CommonTextureList<T> texture1, 
			CommonTextureList<T> texture2, 
			Set<String> texture1NameSet
	){
		for (Texture texture : textureSet)
		{
			CommonTexture<P> ndt;
			
			String tname = texture.getName();
			
			if (texture1NameSet == null || texture1NameSet.contains(tname))
				ndt = texture1.createTexture(tname);
			else
				ndt = texture2.createTexture(tname);

			ndt.setWidth(texture.getWidth());
			ndt.setHeight(texture.getHeight());
			
			int index = -1;
			for (int i = 0; i < texture.getPatchCount(); i++)
			{
				Patch patch = texture.getPatch(i);
				
				String pname = patch.getName();
				
				index = pnames.getIndexOfEntry(pname);
				if (index == -1)
				{
					pnames.addEntry(pname);
					index = pnames.getIndexOfEntry(pname);
				}	
				
				P ndtp = ndt.createPatch();
				ndtp.setOriginX(patch.getOriginX());
				ndtp.setOriginY(patch.getOriginY());
				ndtp.setPatchIndex(index);
			}

		}
	}
		
}
