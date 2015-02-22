package net.mtrop.doom.map;

import net.mtrop.doom.map.binary.DoomLinedef;
import net.mtrop.doom.map.binary.DoomSector;
import net.mtrop.doom.map.binary.DoomSidedef;
import net.mtrop.doom.map.binary.DoomThing;
import net.mtrop.doom.map.binary.DoomVertex;

import com.blackrook.commons.list.List;

/**
 * Doom map in Doom Format.
 * @author Matthew Tropiano
 */
public class DoomMap
{
	
	/** List of Things. */
	private List<DoomThing> things;
	/** List of Sectors. */
	private List<DoomSector> sectors;
	/** List of Vertices. */
	private List<DoomVertex> vertices;
	/** List of Sidedefs. */
	private List<DoomSidedef> sidedefs;
	/** List of Linedefs. */
	private List<DoomLinedef> linedefs;
	
	/**
	 * Creates a blank map.
	 */
	public DoomMap()
	{
		things = new List<DoomThing>(100);
		sectors = new List<DoomSector>(33);
		vertices = new List<DoomVertex>(200);
		sidedefs = new List<DoomSidedef>(200);
		linedefs = new List<DoomLinedef>(100);
	}
	
	/**
	 * @return the list of things.
	 */
	public List<DoomThing> getThings()
	{
		return things;
	}

	/**
	 * Sets the things on this map. 
	 * Input objects are copied to the underlying list.
	 */
	public void setThings(DoomThing ... things)
	{
		this.things.clear();
		for (DoomThing obj : things)
			this.things.add(obj);
	}

	public List<DoomSector> getSectors()
	{
		return sectors;
	}

	public void setSectors(DoomSector ... sectors)
	{
		this.sectors.clear();
		for (DoomSector obj : sectors)
			this.sectors.add(obj);
	}

	public List<DoomVertex> getVertices()
	{
		return vertices;
	}

	public void setVertices(DoomVertex ... vertices)
	{
		this.vertices.clear();
		for (DoomVertex obj : vertices)
			this.vertices.add(obj);
	}

	public List<DoomSidedef> getSidedefs()
	{
		return sidedefs;
	}

	public void setSidedefs(DoomSidedef ... sidedefs)
	{
		this.sidedefs.clear();
		for (DoomSidedef obj : sidedefs)
			this.sidedefs.add(obj);
	}

	public List<DoomLinedef> getLinedefs()
	{
		return linedefs;
	}

	public void setLinedefs(DoomLinedef ... linedefs)
	{
		this.linedefs.clear();
		for (DoomLinedef obj : linedefs)
			this.linedefs.add(obj);
	}
	
	
	
	
	
	
}
