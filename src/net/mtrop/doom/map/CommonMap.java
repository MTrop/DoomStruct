package net.mtrop.doom.map;

import net.mtrop.doom.map.binary.DoomSector;
import net.mtrop.doom.map.binary.DoomSidedef;
import net.mtrop.doom.map.binary.DoomVertex;

import com.blackrook.commons.list.List;

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
		sectors = new List<DoomSector>(33);
		vertices = new List<DoomVertex>(200);
		sidedefs = new List<DoomSidedef>(200);
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
	 */
	public void setSidedefs(DoomSidedef ... sidedefs)
	{
		this.sidedefs.clear();
		for (DoomSidedef obj : sidedefs)
			this.sidedefs.add(obj);
	}

}
