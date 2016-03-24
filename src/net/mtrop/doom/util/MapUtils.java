/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.util;

import java.io.IOException;
import java.util.Iterator;

import net.mtrop.doom.Wad;
import net.mtrop.doom.WadEntry;
import net.mtrop.doom.enums.MapFormat;
import net.mtrop.doom.exception.MapException;
import net.mtrop.doom.map.DoomMap;
import net.mtrop.doom.map.HexenMap;
import net.mtrop.doom.map.StrifeMap;
import net.mtrop.doom.map.binary.DoomLinedef;
import net.mtrop.doom.map.binary.DoomSector;
import net.mtrop.doom.map.binary.DoomSidedef;
import net.mtrop.doom.map.binary.DoomThing;
import net.mtrop.doom.map.binary.DoomVertex;
import net.mtrop.doom.map.binary.HexenLinedef;
import net.mtrop.doom.map.binary.HexenThing;
import net.mtrop.doom.map.binary.StrifeThing;
import net.mtrop.doom.map.bsp.BSPNode;
import net.mtrop.doom.map.bsp.BSPSegment;
import net.mtrop.doom.map.bsp.BSPSubsector;
import net.mtrop.doom.map.bsp.BSPTree;

import com.blackrook.commons.hash.CaseInsensitiveHash;
import com.blackrook.commons.list.List;

/**
 * Map utility methods and functions.
 * @author Matthew Tropiano
 */
public final class MapUtils
{
	public static final String LUMP_THINGS = "THINGS";
	public static final String LUMP_SECTORS = "SECTORS";
	public static final String LUMP_VERTICES = "VERTEXES";
	public static final String LUMP_SIDEDEFS = "SIDEDEFS";
	public static final String LUMP_LINEDEFS = "LINEDEFS";

	public static final String LUMP_TEXTMAP = "TEXTMAP";

	public static final String LUMP_SSECTORS = "SSECTORS";
	public static final String LUMP_NODES = "NODES";
	public static final String LUMP_SEGS = "SEGS";
	public static final String LUMP_REJECT = "REJECT";
	public static final String LUMP_BLOCKMAP = "BLOCKMAP";
	
	public static final String LUMP_ZNODES = "ZNODES";

	public static final String LUMP_GL_VERT = "GL_VERT";
	public static final String LUMP_GL_SEGS = "GL_SEGS";
	public static final String LUMP_GL_SSECT = "GL_SSECT";
	public static final String LUMP_GL_NODES = "GL_NODES";
	public static final String LUMP_GL_PVS = "GL_PVS";
	
	public static final String LUMP_BEHAVIOR = "BEHAVIOR";
	public static final String LUMP_SCRIPTS = "SCRIPTS";
	
	public static final String LUMP_DIALOGUE = "DIALOGUE";
	public static final String LUMP_PWADINFO = "PWADINFO";

	public static final String LUMP_ENDMAP = "ENDMAP";
	
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

	private MapUtils() {}

	/**
	 * Creates a {@link DoomMap} from an entry index in a {@link Wad} that denotes a map header.
	 * If there is more than one header in the WAD that matches the header, the last one is found.
	 * @param wad the WAD to read from.
	 * @param index the index of the map header entry.
	 * @return a DoomMap with all objects set.
	 * @throws MapException if map information is incomplete, or can't be found.
	 * @throws IOException if the WAD can't be read from.
	 * @throws UnsupportedOperationException if attempting to read from a {@link Wad} type that does not contain data.
	 */
	public static DoomMap createDoomMap(Wad wad, int index) throws MapException, IOException
	{
		int count = getMapEntryCount(wad, index);
		
		for (int i = 0; i < count; i++)
		{
			String name = wad.getEntry(i + index).getName();
			if (name.equals(LUMP_BEHAVIOR))
				throw new MapException("Map is not a Doom-formatted map.");
			else if (name.equals(LUMP_TEXTMAP))
				throw new MapException("Map is not a Doom-formatted map. Format is UDMF.");
			else if (name.equals(LUMP_ENDMAP))
				throw new MapException("Map is not a Doom-formatted map. Format is UDMF.");
		}

		DoomMap map = new DoomMap();

		for (int i = 0; i < count; i++)
		{
			WadEntry entry = wad.getEntry(i + index);
			String name = entry.getName();
			switch (name)
			{
				case LUMP_THINGS:
				{
					byte[] b = wad.getData(entry);
					map.setThings(DoomThing.create(b, b.length / DoomThing.LENGTH));
				}
				break;

				case LUMP_SECTORS:
				{
					byte[] b = wad.getData(entry);
					map.setSectors(DoomSector.create(b, b.length / DoomSector.LENGTH));
				}
				break;

				case LUMP_VERTICES:
				{
					byte[] b = wad.getData(entry);
					map.setVertices(DoomVertex.create(b, b.length / DoomVertex.LENGTH));
				}
				break;

				case LUMP_SIDEDEFS:
				{
					byte[] b = wad.getData(entry);
					map.setSidedefs(DoomSidedef.create(b, b.length / DoomSidedef.LENGTH));
				}
				break;

				case LUMP_LINEDEFS:
				{
					byte[] b = wad.getData(entry);
					map.setLinedefs(DoomLinedef.create(b, b.length / DoomLinedef.LENGTH));
				}
				break;
			}
		}
		
		return map;
	}
	
		/**
	 * Creates a {@link DoomMap} from a starting entry in a {@link Wad}.
	 * If there is more than one header in the WAD that matches the header, the last one is found.
	 * @param wad the WAD to read from.
	 * @param headerName the map header name to search for.
	 * @return a DoomMap with all objects set.
	 * @throws MapException if map information is incomplete, or can't be found.
	 * @throws IOException if the WAD can't be read from.
	 * @throws UnsupportedOperationException if attempting to read from a {@link Wad} type that does not contain data.
	 */
	public static DoomMap createDoomMap(Wad wad, String headerName) throws MapException, IOException
	{
		int index = wad.getLastIndexOf(headerName);
		if (index < 0)
			throw new MapException("Cannot find map by header name "+headerName);
		
		return createDoomMap(wad, index);
	}
	
	/**
	 * Creates a {@link StrifeMap} from an entry index in a {@link Wad} that denotes a map header.
	 * If there is more than one header in the WAD that matches the header, the last one is found.
	 * @param wad the WAD to read from.
	 * @param index the index of the map header entry.
	 * @return a StrifeMap with all objects set.
	 * @throws MapException if map information is incomplete, or can't be found.
	 * @throws IOException if the WAD can't be read from.
	 * @throws UnsupportedOperationException if attempting to read from a {@link Wad} type that does not contain data.
	 */
	public static StrifeMap createStrifeMap(Wad wad, int index) throws MapException, IOException
	{
		int count = getMapEntryCount(wad, index);
		
		for (int i = 0; i < count; i++)
		{
			String name = wad.getEntry(i + index).getName();
			if (name.equals(LUMP_BEHAVIOR))
				throw new MapException("Map is not a Strife-formatted map.");
			else if (name.equals(LUMP_TEXTMAP))
				throw new MapException("Map is not a Strife-formatted map. Format is UDMF.");
			else if (name.equals(LUMP_ENDMAP))
				throw new MapException("Map is not a Strife-formatted map. Format is UDMF.");
		}
		
		StrifeMap map = new StrifeMap();

		for (int i = 0; i < count; i++)
		{
			WadEntry entry = wad.getEntry(i + index);
			String name = entry.getName();
			switch (name)
			{
				case LUMP_THINGS:
				{
					byte[] b = wad.getData(entry);
					map.setThings(StrifeThing.create(b, b.length / StrifeThing.LENGTH));
				}
				break;

				case LUMP_SECTORS:
				{
					byte[] b = wad.getData(entry);
					map.setSectors(DoomSector.create(b, b.length / DoomSector.LENGTH));
				}
				break;

				case LUMP_VERTICES:
				{
					byte[] b = wad.getData(entry);
					map.setVertices(DoomVertex.create(b, b.length / DoomVertex.LENGTH));
				}
				break;

				case LUMP_SIDEDEFS:
				{
					byte[] b = wad.getData(entry);
					map.setSidedefs(DoomSidedef.create(b, b.length / DoomSidedef.LENGTH));
				}
				break;

				case LUMP_LINEDEFS:
				{
					byte[] b = wad.getData(entry);
					map.setLinedefs(DoomLinedef.create(b, b.length / DoomLinedef.LENGTH));
				}
				break;
			}
		}
		
		return map;
	}
	
	/**
	 * Creates a {@link StrifeMap} from a starting entry in a {@link Wad}.
	 * If there is more than one header in the WAD that matches the header, the last one is found.
	 * @param wad the WAD to read from.
	 * @param headerName the map header name to search for.
	 * @return a StrifeMap with all objects set.
	 * @throws MapException if map information is incomplete, or can't be found.
	 * @throws IOException if the WAD can't be read from.
	 * @throws UnsupportedOperationException if attempting to read from a {@link Wad} type that does not contain data.
	 */
	public static StrifeMap createStrifeMap(Wad wad, String headerName) throws MapException, IOException
	{
		int index = wad.getLastIndexOf(headerName);
		if (index < 0)
			throw new MapException("Cannot find map by header name "+headerName);
	
		return createStrifeMap(wad, index);
	}
	
	
	/**
	 * Creates a {@link HexenMap} from an entry index in a {@link Wad} that denotes a map header.
	 * If there is more than one header in the WAD that matches the header, the last one is found.
	 * @param wad the WAD to read from.
	 * @param index the index of the map header entry.
	 * @return a HexenMap with all objects set.
	 * @throws MapException if map information is incomplete, or can't be found.
	 * @throws IOException if the WAD can't be read from.
	 * @throws UnsupportedOperationException if attempting to read from a {@link Wad} type that does not contain data.
	 */
	public static HexenMap createHexenMap(Wad wad, int index) throws MapException, IOException
	{
		int count = getMapEntryCount(wad, index);

		boolean hasBehavior = false;
		for (int i = 0; i < count; i++)
		{
			String name = wad.getEntry(i + index).getName();
			if (name.equals(LUMP_BEHAVIOR))
				hasBehavior = true;
			else if (name.equals(LUMP_TEXTMAP))
				throw new MapException("Map is not a Hexen-formatted map. Format is UDMF.");
			else if (name.equals(LUMP_ENDMAP))
				throw new MapException("Map is not a Hexen-formatted map. Format is UDMF.");
		}
		
		if (!hasBehavior)
			throw new MapException("Map is not a Hexen-formatted map. Format is Doom.");
		
		HexenMap map = new HexenMap();
		for (int i = 0; i < count; i++)
		{
			WadEntry entry = wad.getEntry(i + index);
			String name = entry.getName();
			switch (name)
			{
				case LUMP_THINGS:
				{
					byte[] b = wad.getData(entry);
					map.setThings(HexenThing.create(b, b.length / HexenThing.LENGTH));
				}
				break;
	
				case LUMP_SECTORS:
				{
					byte[] b = wad.getData(entry);
					map.setSectors(DoomSector.create(b, b.length / DoomSector.LENGTH));
				}
				break;
	
				case LUMP_VERTICES:
				{
					byte[] b = wad.getData(entry);
					map.setVertices(DoomVertex.create(b, b.length / DoomVertex.LENGTH));
				}
				break;
	
				case LUMP_SIDEDEFS:
				{
					byte[] b = wad.getData(entry);
					map.setSidedefs(DoomSidedef.create(b, b.length / DoomSidedef.LENGTH));
				}
				break;
	
				case LUMP_LINEDEFS:
				{
					byte[] b = wad.getData(entry);
					map.setLinedefs(HexenLinedef.create(b, b.length / HexenLinedef.LENGTH));
				}
				break;
			}
		}
		
		return map;
	}
	
	/**
	 * Creates a {@link HexenMap} from a starting entry in a {@link Wad}.
	 * If there is more than one header in the WAD that matches the header, the last one is found.
	 * @param wad the WAD to read from.
	 * @param headerName the map header name to search for.
	 * @return a HexenMap with all objects set.
	 * @throws MapException if map information is incomplete, or can't be found.
	 * @throws IOException if the WAD can't be read from.
	 * @throws UnsupportedOperationException if attempting to read from a {@link Wad} type that does not contain data.
	 */
	public static HexenMap createHexenMap(Wad wad, String headerName) throws MapException, IOException
	{
		int index = wad.getLastIndexOf(headerName);
		if (index < 0)
			throw new MapException("Cannot find map by header name "+headerName);
		
		return createHexenMap(wad, index);
	}

	/**
	 * Creates a {@link BSPTree} from a starting map entry in a {@link Wad}.
	 * If there is more than one header in the WAD that matches the header, the last one is found.
	 * @param wad the WAD to read from.
	 * @param headerName the map header name to search for.
	 * @return a BSPTree with all objects set.
	 * @throws MapException if map information is incomplete, or can't be found.
	 * @throws IOException if the WAD can't be read from.
	 * @throws UnsupportedOperationException if attempting to read from a {@link Wad} type that does not contain data.
	 */
	public static BSPTree createBSPTree(Wad wad, String headerName) throws MapException, IOException
	{
		int index = wad.getLastIndexOf(headerName);
		if (index < 0)
			throw new MapException("Cannot find map by header name "+headerName);
		
		int count = getMapEntryCount(wad, index);
		
		WadEntry ssectors = null;
		WadEntry segs = null;
		WadEntry nodes = null;
		
		for (int i = 0; i < count; i++)
		{
			WadEntry entry = wad.getEntry(i + index);
			String name = entry.getName();
			if (name.equals(LUMP_SSECTORS))
				ssectors = entry;
			else if (name.equals(LUMP_SEGS))
				segs = entry;
			else if (name.equals(LUMP_NODES))
				nodes = entry;
		}
		
		if (segs == null)
			throw new MapException("BSP Tree information is incomplete. Missing SEGS.");
		if (ssectors == null)
			throw new MapException("BSP Tree information is incomplete. Missing SSECTORS.");
		if (nodes == null)
			throw new MapException("BSP Tree information is incomplete. Missing NODES.");
		
		BSPTree out = new BSPTree();
		byte[] data = null;

		data = wad.getData(segs);
		out.setSegs(BSPSegment.create(data, data.length / BSPSegment.LENGTH));
		data = wad.getData(ssectors);
		out.setSubsectors(BSPSubsector.create(data, data.length / BSPSubsector.LENGTH));
		data = wad.getData(nodes);
		out.setNodes(BSPNode.create(data, data.length / BSPNode.LENGTH));
		
		return out;
	}
	
	/**
	 * Returns all of the indices of every map in the wad.
	 * This algorithm scans for map entry names. If it finds one, the previous entry is the probably the header.
	 * @param wad the {@link Wad} to search inside.
	 * @return an array of all of the entry indices of maps. 
	 */
	public static int[] getAllMapIndices(Wad wad)
	{
		List<Integer> indices = new List<Integer>(32);
		WadEntry e = null;
		int z = 0;
		boolean map = false;
		Iterator<WadEntry> it = wad.iterator();
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
	 * This algorithm scans for map entry names. If it finds one, the previous entry is the probably the header.
	 * @param wad the DoomWad to search in.
	 * @return an array of all of the entry indices of maps. 
	 */
	public static String[] getAllMapHeaders(Wad wad)
	{
		int[] entryIndices = getAllMapIndices(wad);
		String[] out = new String[entryIndices.length];
		int i = 0;
		for (int index : entryIndices)
			out[i++] = wad.getEntry(index).getName();
		return out;
	}

	/**
	 * Figures out a map's format by its entry listing.
	 * Due to the nature of this algorithm, this cannot return {@link MapFormat#STRIFE}.
	 * @param wad the WAD to read from.
	 * @param index the index of the map header entry.
	 * @return a {@link MapFormat} that details the map format type, or null if it cannot be figured out.
	 */
	public static MapFormat getMapFormat(Wad wad, int index)
	{
		int count = getMapEntryCount(wad, index);

		if (count <= 1)
			return null;
		
		for (int i = 0; i < count; i++)
		{
			String name = wad.getEntry(i + index).getName();
			if (name.equals(LUMP_BEHAVIOR))
				return MapFormat.HEXEN;
			else if (name.equals(LUMP_TEXTMAP))
				return MapFormat.UDMF;
			else if (name.equals(LUMP_ENDMAP))
				return MapFormat.UDMF;
		}

		return MapFormat.DOOM;
	}

	/**
	 * Figures out a map's format by its entry listing.
	 * Due to the nature of this algorithm, this cannot return {@link MapFormat#STRIFE}.
	 * @param wad the WAD to read from.
	 * @param headerName the map header name to search for.
	 * @return a {@link MapFormat} that details the map format type, or null if it cannot be figured out, nor if the header can be found.
	 */
	public static MapFormat getMapFormat(Wad wad, String headerName)
	{
		int index = wad.getLastIndexOf(headerName);
		if (index < 0)
			return null;
	
		return getMapFormat(wad, index);
	}

	/**
	 * Returns the amount of entries in a map, including the header.
	 * @param wad the WAD to inspect.
	 * @param headerName the map header name.
	 * @return the length, in entries, of the contiguous map data.
	 */
	public static int getMapEntryCount(Wad wad, String headerName)
	{
		int start = wad.getIndexOf(headerName);
		if (start < 0)
			return 0;
		else
			return getMapEntryCount(wad, start);
	}
	
	/**
	 * Returns the amount of entries in a map, including the header.
	 * @param wad the WAD to inspect.
	 * @param startIndex the starting index.
	 * @return the length, in entries, of the contiguous map data.
	 */
	public static int getMapEntryCount(Wad wad, int startIndex)
	{
		int end = 0;
		
		if (startIndex + 1 == wad.getSize())
			return 1;
		else if (wad.getEntry(startIndex + 1).getName().equalsIgnoreCase(LUMP_TEXTMAP))
		{
			end = startIndex + 1;
			while (end < wad.getSize() && !wad.getEntry(end).getName().equalsIgnoreCase(LUMP_ENDMAP))
				end++;
			end++;
		}
		else
		{
			end = startIndex + 1;
			while (end < wad.getSize() && isMapDataLump(wad.getEntry(end).getName()))
				end++;
		}
		
		return end - startIndex;
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
