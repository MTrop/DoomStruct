/*******************************************************************************
 * Copyright (c) 2015-2019 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.util;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import net.mtrop.doom.Wad;
import net.mtrop.doom.WadEntry;
import net.mtrop.doom.exception.TextureException;
import net.mtrop.doom.exception.WadException;
import net.mtrop.doom.object.BinaryObject;
import net.mtrop.doom.struct.io.SerializerUtils;
import net.mtrop.doom.texture.CommonTexture;
import net.mtrop.doom.texture.CommonTextureList;
import net.mtrop.doom.texture.DoomTextureList;
import net.mtrop.doom.texture.PatchNames;
import net.mtrop.doom.texture.StrifeTextureList;
import net.mtrop.doom.texture.TextureSet;
import net.mtrop.doom.texture.TextureSet.Patch;
import net.mtrop.doom.texture.TextureSet.Texture;

/**
 * Graphics utility methods for image types.
 * @author Matthew Tropiano
 */
public final class TextureUtils
{
	private TextureUtils() {}

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
		return importTextureSet(wf, null, null);
	}
	
	/**
	 * This private method loads TEXTURE1/TEXTURE2.
	 */
	private static TextureSet importTextureSet(Wad wf, boolean[] strifeType, Set<String> texture1Names) throws WadException, IOException
	{
		PatchNames patchNames = null;
		CommonTextureList<?> textureList1 = null;
		CommonTextureList<?> textureList2 = null;
		
		byte[] textureData = wf.getData("TEXTURE1");
		boolean isStrife = false;
		
		if (textureData == null)
			throw new TextureException("Could not find TEXTURE1!\n");

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

		if (strifeType != null)
			strifeType[0] = isStrife;

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
		{
			if (texture1Names != null)
				for (CommonTexture<?> t : textureList1)
					texture1Names.add(t.getName());
			out = new TextureSet(patchNames, textureList1, textureList2);
		}
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

	/**
	 * Creates a texture copier object for moving one or more textures (and associated data) to another Wad.
	 * @param sourceWad the source Wad.
	 * @param destinationWad the destination Wad.
	 * @return the new copier.
	 * @throws TextureException if the source or destination does not have TEXTUREx or a PNAMES lump. 
	 * @throws IOException if a read error happens.
	 */
	public static TextureCopier createTextureCopier(Wad sourceWad, Wad destinationWad) throws IOException
	{
		TextureCopier out = new TextureCopier();
		
		out.sourceWad = sourceWad;
		out.destinationWad = destinationWad;
		
		if ((out.sourcePatchStartIndex = sourceWad.indexOf("P_START")) < 0)
			out.sourcePatchStartIndex = sourceWad.indexOf("PP_START");
		if ((out.sourcePatchEndIndex = sourceWad.indexOf("P_END")) < 0)
			out.sourcePatchEndIndex = sourceWad.indexOf("PP_END");
		
		if (out.sourcePatchStartIndex < 0 || out.sourcePatchEndIndex < 0)
			throw new TextureException("Source Wad does not have P_START/P_END");

		out.sourceTextureSet = importTextureSet(sourceWad);
		
		boolean[] strife = new boolean[1];
		if (destinationWad.contains("TEXTURE2"))
			out.destinationTexture1Set = new HashSet<String>();

		out.strife = strife[0];
		out.destinationTextureSet = importTextureSet(destinationWad, strife, out.destinationTexture1Set);
		int didx = destinationWad.indexOf("P_END");
		if (didx < 0)
			didx = destinationWad.indexOf("PP_END");
		if (didx >= 0)
			out.destinationPatchEndIndex = didx;
		
		out.destinationPatches = new HashSet<String>();
		for (WadEntry e : WadUtils.getEntriesInNamespace(destinationWad, "P", Pattern.compile("P[1-9]_(START|END)")))
			out.destinationPatches.add(e.getName());
		for (WadEntry e : WadUtils.getEntriesInNamespace(destinationWad, "PP"))
			out.destinationPatches.add(e.getName());
		
		return out;
	}
	
	/**
	 * A texture copying context for copying one or more textures to another Wad.
	 */
	public static class TextureCopier
	{
		/** Source Wad */
		private Wad sourceWad;
		/** Source Texture Set */
		private TextureSet sourceTextureSet;
		/** The index of the source "P[P]_START" index. */
		private int sourcePatchStartIndex;
		/** The index of the source "P[P]_END" index. */
		private int sourcePatchEndIndex;

		/** If Strife format, this is true. */
		private boolean strife;
		
		/** Destination Wad */
		private Wad destinationWad;
		/** Destination Texture Set */
		private TextureSet destinationTextureSet;
		/** Set of textures to add to TEXTURE1 */
		private Set<String> destinationTexture1Set;
		/** The index of the destination "PP_END" index. */
		private Integer destinationPatchEndIndex;
		/** Set of patch entry names in destination. */
		private Set<String> destinationPatches;

		/**
		 * Creates the copier. 
		 */
		private TextureCopier()
		{
			this.sourceWad = null;
			this.sourceTextureSet = null;
			this.sourcePatchStartIndex = -1;
			this.sourcePatchEndIndex = -1;
			
			this.strife = false;
			this.destinationWad = null;
			this.destinationTextureSet = null;
			this.destinationTexture1Set = null;
			this.destinationPatchEndIndex = null;
		}

		/**
		 * Copies one texture to another texture set, and copies 
		 * the associated patch entries from the source Wad to the destination Wad.
		 * @param textureName the name of the texture to copy over.
		 * @throws IOException if a read or write error occurs.
		 * @return true if the texture was copied over, false if the texture name was not found in the source.
		 */
		public boolean copyTexture(String textureName) throws IOException
		{
			if (!sourceTextureSet.contains(textureName) || destinationTextureSet.contains(textureName))
				return false;
			
			Texture copied = destinationTextureSet.addTexture(sourceTextureSet.getTextureByName(textureName));
			
			// if PP_END (and PP_START) does not exist, add them to the destination.
			if (destinationPatchEndIndex == null)
			{
				if (destinationWad.isPWAD())
				{
					destinationWad.addMarker("PP_START");
					destinationWad.addMarker("PP_END");
				}
				else // is IWAD
				{
					destinationWad.addMarker("P_START");
					destinationWad.addMarker("P_END");
				}
				destinationPatchEndIndex = destinationWad.getEntryCount() - 1; // added to end.
			}
			
			for (Patch p : copied)
			{
				String name = p.getName();
				
				if (destinationPatches.contains(name))
					continue;
				
				int entryIndex = sourceWad.indexOf(name, sourcePatchStartIndex);
				// ensure patch namespace.
				if (entryIndex > sourcePatchStartIndex && entryIndex < sourcePatchEndIndex)
				{
					destinationWad.addDataAt(destinationPatchEndIndex++, name, sourceWad.getData(entryIndex));
					destinationPatches.add(name);
				}
			}
			
			return true;
		}
		
		/**
		 * Commits the destination texture set to the destination Wad.
		 * This will replace TEXTURE1/2 and PNAMES in the destination.
		 * @throws IOException if a write error occurs.
		 */
		public void finish() throws IOException
		{
			int texture1Index = destinationWad.indexOf("TEXTURE1");
			int texture2Index = destinationWad.indexOf("TEXTURE2");
			int pnamesIndex = destinationWad.indexOf("PNAMES");
			
			PatchNames pnames = new PatchNames();
			CommonTextureList<?> texture1, texture2;
			
			if (texture2Index < 0) // no TEXTURE2
			{
				texture2 = null;
				if (strife)
					destinationTextureSet.export(pnames, (StrifeTextureList)(texture1 = new StrifeTextureList(1024)));
				else
					destinationTextureSet.export(pnames, (DoomTextureList)(texture1 = new DoomTextureList(1024)));
			}
			else
			{
				if (strife)
					destinationTextureSet.export(
						pnames, 
						(StrifeTextureList)(texture1 = new StrifeTextureList(1024)), 
						(StrifeTextureList)(texture2 = new StrifeTextureList(1024)), 
						destinationTexture1Set
					);
				else
					destinationTextureSet.export(
						pnames, 
						(DoomTextureList)(texture1 = new DoomTextureList(1024)), 
						(DoomTextureList)(texture2 = new DoomTextureList(1024)), 
						destinationTexture1Set
					);
			}
			
			destinationWad.replaceEntry(texture1Index, texture1);
			if (texture2Index >= 0)
				destinationWad.replaceEntry(texture2Index, texture2);
			destinationWad.replaceEntry(pnamesIndex, pnames);			
		}
		
	}
	
}
