package net.mtrop.doom.map;

import net.mtrop.doom.map.binary.DoomLinedef;
import net.mtrop.doom.map.binary.StrifeThing;

import com.blackrook.commons.list.List;

/**
 * Doom map in Doom Format, but using Strife-flagged things.
 * @author Matthew Tropiano
 */
public class StrifeMap extends CommonMap
{
	/** List of Things. */
	private List<StrifeThing> things;
	/** List of Linedefs. */
	private List<DoomLinedef> linedefs;
	
	/**
	 * Creates a blank map.
	 */
	public StrifeMap()
	{
		super();
		things = new List<StrifeThing>(100);
		linedefs = new List<DoomLinedef>(100);
	}
	
	/**
	 * @return the underlying list of things.
	 */
	public List<StrifeThing> getThings()
	{
		return things;
	}

	/**
	 * Sets the things on this map. 
	 * Input objects are copied to the underlying list.
	 */
	public void setThings(StrifeThing ... things)
	{
		this.things.clear();
		for (StrifeThing obj : things)
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
