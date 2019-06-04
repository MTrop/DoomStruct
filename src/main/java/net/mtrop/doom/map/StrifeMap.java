/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map;

import java.util.ArrayList;
import java.util.List;

import net.mtrop.doom.map.binary.DoomLinedef;
import net.mtrop.doom.map.binary.StrifeThing;

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
		things = new ArrayList<StrifeThing>(100);
		linedefs = new ArrayList<DoomLinedef>(100);
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
	 * @param things the new list of things.
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
	 * @param linedefs the new list of linedefs.
	 */
	public void setLinedefs(DoomLinedef ... linedefs)
	{
		this.linedefs.clear();
		for (DoomLinedef obj : linedefs)
			this.linedefs.add(obj);
	}
	
}
