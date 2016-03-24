/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.util;

import java.util.regex.Pattern;

import net.mtrop.doom.Wad;
import net.mtrop.doom.WadEntry;

import com.blackrook.commons.list.List;
import com.blackrook.io.SuperReader;

/**
 * WAD utility methods and functions.
 * @author Matthew Tropiano
 */
public final class WadUtils
{
	private WadUtils() {}

	/**
	 * Finds all entries within a WAD entry namespace.
	 * A namespace is marked by one or two characters and "_START" or "_END" as a suffix.
	 * All entries in between are considered part of the "namespace."
	 * <p>
	 * The returned entries are valid only to the provided WAD. Using entry information with unassociated WADs
	 * could create undesired results.
	 * @param prefix the namespace prefix to use (e.g. "f" or "ff" for flats, "p" or "pp" for textures, etc.).
	 * @param wad the WAD file to scan.
	 * @return an array of all entries in the namespace, or an empty array if none are found.
	 */
	public static WadEntry[] getEntriesInNamespace(String prefix, Wad wad)
	{
		return getEntriesInNamespace(prefix, null, wad);
	}

	/**
	 * Finds all entries within a WAD entry namespace.
	 * A namespace is marked by one or two characters and "_START" or "_END" as a suffix.
	 * All entries in between are considered part of the "namespace."
	 * <p>
	 * The returned entries are valid only to the provided WAD. Using entry information with unassociated WADs
	 * could create undesired results.
	 * @param prefix the namespace prefix to use (e.g. "f" or "ff" for flats, "p" or "pp" for textures, etc.).
	 * @param wad the WAD file to scan.
	 * @param ignorePattern the regex pattern to use for deciding which entries in the namespace to ignore.
	 * @return an array of all entries in the namespace, or an empty array if none are found.
	 */
	public static WadEntry[] getEntriesInNamespace(String prefix, Pattern ignorePattern, Wad wad)
	{
		List<WadEntry> entryList = new List<WadEntry>(100);
		
		int start = wad.getIndexOf(prefix+"_start");
		if (start > 0)
		{
			int end = wad.getIndexOf(prefix+"_end");
			if (end > 0)
			{
				for (int i = start + 1; i < end; i++)
				{
					WadEntry entry = wad.getEntry(i);
					if (ignorePattern != null && ignorePattern.matcher(entry.getName()).matches())
						continue;
					entryList.add(entry);
				}
			}
		}
		
		WadEntry[] entry = new WadEntry[entryList.size()];
		entryList.toArray(entry);
		return entry;
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
		int textureCount = SuperReader.bytesToInt(buf, SuperReader.LITTLE_ENDIAN);
		ptr = (textureCount * 4) + 20;
		
		boolean good = true;
		while (ptr < b.length && good)
		{
			System.arraycopy(b, ptr, buf, 0, 4);
			
			// test for unused texture data.
			if (SuperReader.bytesToInt(buf, SuperReader.LITTLE_ENDIAN) != 0)
				good = false;
	
			// test for unused patch data.
			else
			{
				ptr += 4;
				System.arraycopy(b, ptr, buf, 0, 2);
				int patches = SuperReader.bytesToInt(buf, SuperReader.LITTLE_ENDIAN);
				ptr += 2;
				while (patches > 0)
				{
					ptr += 6;
					System.arraycopy(b, ptr, buf, 0, 4);
					int x = SuperReader.bytesToInt(buf, SuperReader.LITTLE_ENDIAN);
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
