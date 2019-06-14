/*******************************************************************************
 * Copyright (c) 2015-2019 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.mtrop.doom.Wad;
import net.mtrop.doom.WadEntry;
import net.mtrop.doom.WadFile;

/**
 * WAD utility methods and functions.
 * @author Matthew Tropiano
 */
public final class WadUtils
{
	private WadUtils() {}

	/**
	 * Creates a new WAD file by copying the contents of an existing WAD to another file,
	 * which discards all un-addressed data from the first. The source Wad must be an 
	 * implementation that supports retrieving data from it.
	 * @param source the source Wad.
	 * @param destination the destination file.
	 * @throws UnsupportedOperationException if the provided Wad is not an implementation that you can read data from.
	 * @throws SecurityException if the target file cannot be written to due to security reasons.
	 * @throws IOException if a read or write error occurs.
	 */
	public static void cleanEntries(Wad source, File destination) throws IOException
	{
		WadFile wadFile = WadFile.createWadFile(destination);
		for (WadEntry entry : source)
			wadFile.addData(entry.getName(), source.getData(entry), true);
		wadFile.flushEntries();
		wadFile.close();
	}

	/**
	 * Finds all entries within a WAD entry namespace.
	 * A namespace is marked by one or two characters and "_START" or "_END" as a suffix.
	 * All entries in between are considered part of the "namespace."
	 * <p>
	 * The returned entries are valid only to the provided WAD. Using entry information with unassociated WADs
	 * could create undesired results.
	 * @param prefix the namespace prefix to use (e.g. "F" or "FF" for flats, "P" or "PP" for patches, etc.).
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
	 * @param prefix the namespace prefix to use (e.g. "F" or "FF" for flats, "P" or "PP" for texture patches, etc.).
	 * @param wad the WAD file to scan.
	 * @param ignorePattern the regex pattern to use for deciding which entries in the namespace to ignore.
	 * @return an array of all entries in the namespace, or an empty array if none are found.
	 */
	public static WadEntry[] getEntriesInNamespace(String prefix, Pattern ignorePattern, Wad wad)
	{
		List<WadEntry> entryList = new ArrayList<WadEntry>(100);
		
		int start = wad.indexOf(prefix+"_START");
		if (start > 0)
		{
			int end = wad.indexOf(prefix+"_END");
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

}
