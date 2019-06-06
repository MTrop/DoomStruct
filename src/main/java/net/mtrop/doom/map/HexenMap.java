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

import net.mtrop.doom.map.binary.HexenLinedef;
import net.mtrop.doom.map.binary.HexenThing;

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
		things = new ArrayList<HexenThing>(100);
		linedefs = new ArrayList<HexenLinedef>(100);
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
	 * @param things the new list of things.
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
	 * @param linedefs the new list of linedefs.
	 */
	public void setLinedefs(HexenLinedef ... linedefs)
	{
		this.linedefs.clear();
		for (HexenLinedef obj : linedefs)
			this.linedefs.add(obj);
	}
	
}
