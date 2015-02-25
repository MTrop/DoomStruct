package net.mtrop.doom.map;

import net.mtrop.doom.map.binary.HexenLinedef;
import net.mtrop.doom.map.binary.HexenThing;

import com.blackrook.commons.list.List;

/**
 * Doom map in Doom Format.
 * @author Matthew Tropiano
 */
public class HexenMap extends CommonMap
{
	/** List of Things. */
	private List<HexenThing> things;
	/** List of Linedefs. */
	private List<HexenLinedef> linedefs;
	
	/**
	 * Creates a blank map.
	 */
	public HexenMap()
	{
		super();
		things = new List<HexenThing>(100);
		linedefs = new List<HexenLinedef>(100);
	}
	
	/**
	 * @return the underlying list of things.
	 */
	public List<HexenThing> getThings()
	{
		return things;
	}

	/**
	 * Sets the things on this map. 
	 * Input objects are copied to the underlying list.
	 */
	public void setThings(HexenThing ... things)
	{
		this.things.clear();
		for (HexenThing obj : things)
			this.things.add(obj);
	}

	/**
	 * @return the underlying list of linedefs.
	 */
	public List<HexenLinedef> getLinedefs()
	{
		return linedefs;
	}

	/**
	 * Replaces the list of linedefs in the map.
	 * Input objects are copied to the underlying list.
	 */
	public void setLinedefs(HexenLinedef ... linedefs)
	{
		this.linedefs.clear();
		for (HexenLinedef obj : linedefs)
			this.linedefs.add(obj);
	}
	
}
