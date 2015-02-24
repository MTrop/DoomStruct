package net.mtrop.doom.map;

import net.mtrop.doom.map.binary.DoomLinedef;
import net.mtrop.doom.map.binary.DoomThing;

import com.blackrook.commons.list.List;

/**
 * Doom map in Doom Format.
 * @author Matthew Tropiano
 */
public class DoomMap extends CommonMap
{
	/** List of Things. */
	private List<DoomThing> things;
	/** List of Linedefs. */
	private List<DoomLinedef> linedefs;
	
	/**
	 * Creates a blank map.
	 */
	public DoomMap()
	{
		super();
		things = new List<DoomThing>(100);
		linedefs = new List<DoomLinedef>(100);
	}
	
	/**
	 * @return the underlying list of things.
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

	/**
	 * @return the underlying list of linedefs.
	 */
	public List<DoomLinedef> getLinedefs()
	{
		return linedefs;
	}

	/**
	 * Replaces the list of linedefs in the map.
	 * Input objects are copied to the underlying list.
	 */
	public void setLinedefs(DoomLinedef ... linedefs)
	{
		this.linedefs.clear();
		for (DoomLinedef obj : linedefs)
			this.linedefs.add(obj);
	}
	
}
