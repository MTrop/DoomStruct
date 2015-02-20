package net.mtrop.doom.util;

import java.util.Iterator;

import net.mtrop.doom.Wad;
import net.mtrop.doom.WadEntry;

import com.blackrook.commons.hash.CaseInsensitiveHash;
import com.blackrook.commons.list.List;

/**
 * Map utility methods and functions.
 * @author Matthew Tropiano
 */
public final class MapUtils
{
	private MapUtils() {}
	
	public static final CaseInsensitiveHash MAP_SPECIAL = new CaseInsensitiveHash(20) 
	{{
		put("THINGS");
		put("LINEDEFS");
		put("SIDEDEFS");
		put("VERTEXES");
		put("SECTORS");
		put("SSECTORS");
		put("NODES");
		put("SEGS");
		put("REJECT");
		put("BLOCKMAP");
		put("BEHAVIOR");
		put("SCRIPTS");
		put("TEXTMAP");
		put("ENDMAP");
		put("ZNODES");
		put("DIALOGUE");
		put("GL_VERT");
		put("GL_SEGS");
		put("GL_SSECT");
		put("GL_NODES");
		put("GL_PVS");
		put("PWADINFO");
	}};

	/**
	 * Returns all of the indices of every map in the wad.
	 * This algorithm checks for "THINGS" or "TEXTMAP" lumps - the first 
	 * entry in a map. If it finds one, the previous entry is the header.
	 * @param wf the {@link Wad} to search inside.
	 * @return an array of all of the entry indices of maps. 
	 */
	public static int[] getAllMapIndices(Wad wf)
	{
		List<Integer> indices = new List<Integer>(32);
		WadEntry e = null;
		int z = 0;
		boolean map = false;
		Iterator<WadEntry> it = wf.iterator();
		while (it.hasNext())
		{
			e = it.next();
			String name = e.getName();
			if (isMapDataLump(name) && z > 0)
			{
				if (!map)
				{
					indices.add(z - 1);
					map = true;
				}
			}
			else
			{
				map = false;
			}
			
			z++;
		}
		
		int[] out = new int[indices.size()];
		int x = 0;
		for (Integer i : indices)
			out[x++] = i;
		return out;
	}

	/**
	 * Returns all of the entry names of every map in the wad.
	 * This algorithm checks for "THINGS" or "TEXTMAP" lumps - the typical 
	 * first entry in a map. If it finds one, the previous entry is the header.
	 * @param wf the DoomWad to search in.
	 * @return an array of all of the entry indices of maps. 
	 */
	public static String[] getAllMapEntries(Wad wf)
	{
		int[] entryIndices = getAllMapIndices(wf);
		String[] out = new String[entryIndices.length];
		int i = 0;
		for (int index : entryIndices)
			out[i++] = wf.getEntry(index).getName();
		return out;
	}

	/**
	 * Returns the amount of entries in a map, including the header.
	 * @param wad the WAD to inspect.
	 * @param headerName the map header name.
	 * @return the length, in entries, of the contiguous map data.
	 */
	public static int getMapContentIndices(Wad wad, String headerName)
	{
		int start = wad.getIndexOf(headerName);
		int end = 0;
		
		if (start + 1 == wad.getSize())
			return 1;
		else if (wad.getEntry(start + 1).getName().equalsIgnoreCase("textmap"))
		{
			end = start + 1;
			while (end < wad.getSize() && !wad.getEntry(end).getName().equalsIgnoreCase("endmap"))
				end++;
			end++;
		}
		else
		{
			end = start + 1;
			while (end < wad.getSize() && isMapDataLump(wad.getEntry(end).getName()))
				end++;
		}
		
		return end - start;
	}
	
	/**
	 * Tests if the entry name provided is a valid part of a map.
	 * @param name the lump name to test.
	 * @return if this is the name of a map data lump.
	 */
	public static boolean isMapDataLump(String name)
	{
		return NameUtils.isValidEntryName(name) 
			&& (
				name.startsWith("GX_") 
				|| name.startsWith("SCRIPT") 
				|| MAP_SPECIAL.contains(name.toUpperCase())
			);
	}
	

}
