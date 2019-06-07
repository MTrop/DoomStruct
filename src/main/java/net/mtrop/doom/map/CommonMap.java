/*******************************************************************************
 * Copyright (c) 2015-2019 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map;

import java.util.ArrayList;
import java.util.List;

import net.mtrop.doom.map.binary.DoomSector;
import net.mtrop.doom.map.binary.DoomSidedef;
import net.mtrop.doom.map.binary.DoomVertex;

/**
 * Common structures for binary maps loaded from a Wad.
 * @author Matthew Tropiano
 */
public abstract class CommonMap
{
	/** List of Sectors. */
	private List<DoomSector> sectors;
	/** List of Vertices. */
	private List<DoomVertex> vertices;
	/** List of Sidedefs. */
	private List<DoomSidedef> sidedefs;

	protected CommonMap()
	{
		this.sectors = new ArrayList<DoomSector>(33);
		this.vertices = new ArrayList<DoomVertex>(200);
		this.sidedefs = new ArrayList<DoomSidedef>(200);
	}
	
	/**
	 * @return the underlying list of sectors.
	 */
	public List<DoomSector> getSectors()
	{
		return sectors;
	}

	/**
	 * Replaces the list of sectors in the map.
	 * Input objects are copied to the underlying list.
	 * @param sectors the new list of sectors.
	 */
	public void setSectors(DoomSector ... sectors)
	{
		this.sectors.clear();
		for (DoomSector obj : sectors)
			this.sectors.add(obj);
	}

	/**
	 * @return the underlying list of vertices.
	 */
	public List<DoomVertex> getVertices()
	{
		return vertices;
	}

	/**
	 * Replaces the list of vertices in the map.
	 * Input objects are copied to the underlying list.
	 * @param vertices the new list of vertices.
	 */
	public void setVertices(DoomVertex ... vertices)
	{
		this.vertices.clear();
		for (DoomVertex obj : vertices)
			this.vertices.add(obj);
	}

	/**
	 * @return the underlying list of sidedefs.
	 */
	public List<DoomSidedef> getSidedefs()
	{
		return sidedefs;
	}

	/**
	 * Replaces the list of sidedefs in the map.
	 * Input objects are copied to the underlying list.
	 * @param sidedefs the new list of sidedefs.
	 */
	public void setSidedefs(DoomSidedef ... sidedefs)
	{
		this.sidedefs.clear();
		for (DoomSidedef obj : sidedefs)
			this.sidedefs.add(obj);
	}

}
