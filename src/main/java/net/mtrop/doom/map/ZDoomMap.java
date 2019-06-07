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

import net.mtrop.doom.map.data.ZDoomLinedef;
import net.mtrop.doom.map.data.ZDoomThing;

/**
 * Doom map in Doom Format.
 * @author Matthew Tropiano
 */
public class ZDoomMap extends CommonMap
{
	/** List of Things. */
	private List<ZDoomThing> things;
	/** List of Linedefs. */
	private List<ZDoomLinedef> linedefs;
	
	/**
	 * Creates a blank map.
	 */
	public ZDoomMap()
	{
		super();
		this.things = new ArrayList<ZDoomThing>(100);
		this.linedefs = new ArrayList<ZDoomLinedef>(100);
	}
	
	/**
	 * @return the underlying list of things.
	 */
	public List<ZDoomThing> getThings()
	{
		return things;
	}

	/**
	 * Sets the things on this map. 
	 * Input objects are copied to the underlying list.
	 * @param things the new list of things.
	 */
	public void setThings(ZDoomThing ... things)
	{
		this.things.clear();
		for (ZDoomThing obj : things)
			this.things.add(obj);
	}

	/**
	 * @return the underlying list of linedefs.
	 */
	public List<ZDoomLinedef> getLinedefs()
	{
		return linedefs;
	}

	/**
	 * Replaces the list of linedefs in the map.
	 * Input objects are copied to the underlying list.
	 * @param linedefs the new list of linedefs.
	 */
	public void setLinedefs(ZDoomLinedef ... linedefs)
	{
		this.linedefs.clear();
		for (ZDoomLinedef obj : linedefs)
			this.linedefs.add(obj);
	}
	
}
