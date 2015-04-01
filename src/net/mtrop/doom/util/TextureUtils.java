package net.mtrop.doom.util;

import com.blackrook.commons.AbstractSet;
import com.blackrook.commons.list.SortedList;

import net.mtrop.doom.texture.DoomTextureList;
import net.mtrop.doom.texture.PatchNames;
import net.mtrop.doom.texture.TextureSet;
import net.mtrop.doom.texture.TextureSet.Texture;
import net.mtrop.doom.texture.TextureSet.Texture.Patch;

/**
 * Holds a series of helpful methods for texture set export.
 * @author Matthew Tropiano
 */
public final class TextureUtils
{
	private TextureUtils()
	{
	}

	/**
	 * Generates a patch name lump from all of the patch names in the texture set.
	 * @param textureSet the source texture set to generate the patch name lump from.
	 * @return a new {@link PatchNames} object from the texture set data.
	 */
	public static PatchNames createPatchNames(TextureSet textureSet)
	{
		SortedList<String> textureList = new SortedList<String>();
		
		for (Texture texture : textureSet)
			for (Patch patch : texture)
				textureList.add(patch.getName());
		
		PatchNames out = new PatchNames(textureList.size());
		for (String name : textureList)
			out.add(name);
		return out;
	}
	
	/**
	 * Generates a patch name lump from all of the patch names in the texture set.
	 * @param patchNames the patch name lump to use as a patch index for the texture patch indexing.
	 * @param textureNameSet the set of texture names to export.
	 * @return a new {@link DoomTextureList} object with indexed textures.
	 */
	public static DoomTextureList createDoomTextureList(PatchNames patchNames, AbstractSet<String> textureNameSet)
	{
		//TODO: Finish this.
		return null;
	}
	
}
