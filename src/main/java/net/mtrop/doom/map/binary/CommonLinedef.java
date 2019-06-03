/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map.binary;

import net.mtrop.doom.map.MapObject;
import net.mtrop.doom.util.RangeUtils;

/**
 * Contains common elements of all binary linedef.
 * @author Matthew Tropiano
 */
public abstract class CommonLinedef implements MapObject
{
	/** Vertex start. */
	protected int vertexStartIndex;
	/** Vertex end. */
	protected int vertexEndIndex;

	/** Front sidedef. */
	protected int sidedefFrontIndex;
	/** Back sidedef. */
	protected int sidedefBackIndex;

	/** Linedef special. */
	protected int special;

	/** Flag: Creatures (players and monsters) cannot pass. */
	protected boolean impassable;
	/** Flag: Monsters cannot pass. */
	protected boolean monsterBlocking;
	/** Flag: Line has two sides: projectiles/hitscans can pass. */
	protected boolean twoSided;
	/** Flag: Line's upper texture is drawn from top to bottom. */
	protected boolean upperUnpegged;
	/** Flag: Line's lower texture is drawn from bottom to top. */
	protected boolean lowerUnpegged;
	/** Flag: Line's drawn like one-sided impassable ones on automap. */
	protected boolean secret;
	/** Flag: Line's immediately drawn on automap. */
	protected boolean mapped;
	/** Flag: Line's NEVER drawn on automap. */
	protected boolean notDrawn;
	/** Flag: Line blocks sound propagation. */
	protected boolean soundBlocking;

	protected CommonLinedef()
	{
		vertexStartIndex = NULL_REFERENCE;
		vertexEndIndex = NULL_REFERENCE;
		sidedefFrontIndex = NULL_REFERENCE;
		sidedefBackIndex = NULL_REFERENCE;
	}
	
	/**
	 * Sets the starting vertex index.
	 * @param vertexStartIndex the index of the start vertex.
	 * @throws IllegalArgumentException if index is outside the range 0 to 65535.
	 */
	public void setVertexStartIndex(int vertexStartIndex)
	{
		RangeUtils.checkShortUnsigned("Vertex Start Index", vertexStartIndex);
		this.vertexStartIndex = vertexStartIndex;
	}
	
	/**
	 * @return the starting vertex index.
	 */
	public int getVertexStartIndex()
	{
		return vertexStartIndex;
	}
	
	/**
	 * Sets the ending vertex index.
	 * @param vertexEndIndex the index of the end vertex.
	 * @throws IllegalArgumentException if index is outside the range 0 to 65535.
	 */
	public void setVertexEndIndex(int vertexEndIndex)
	{
		RangeUtils.checkShortUnsigned("Vertex End Index", vertexEndIndex);
		this.vertexEndIndex = vertexEndIndex;
	}
	
	/**
	 * @return the ending vertex index.
	 */
	public int getVertexEndIndex()
	{
		return vertexEndIndex;
	}
	
	/**
	 * Sets the front sidedef index.
	 * @param sidedefFrontIndex the index of the front sidedef.
	 * @throws IllegalArgumentException if special is outside the range -1 to 32767.
	 */
	public void setSidedefFrontIndex(int sidedefFrontIndex)
	{
		RangeUtils.checkRange("Sidedef Front Index", -1, Short.MAX_VALUE, sidedefFrontIndex);
		this.sidedefFrontIndex = sidedefFrontIndex;
	}
	/**
	 * @return the front sidedef index.
	 */
	public int getSidedefFrontIndex()
	{
		return sidedefFrontIndex;
	}
	
	/**
	 * Sets the back sidedef index.
	 * @param sidedefBackIndex the index of the back sidedef.
	 * @throws IllegalArgumentException if special is outside the range -1 to 32767.
	 */
	public void setSidedefBackIndex(int sidedefBackIndex)
	{
		RangeUtils.checkRange("Sidedef Back Index", -1, Short.MAX_VALUE, sidedefBackIndex);
		this.sidedefBackIndex = sidedefBackIndex;
	}
	
	/**
	 * @return the back sidedef index.
	 */
	public int getSidedefBackIndex()
	{
		return sidedefBackIndex;
	}
	
	/**
	 * Sets the linedef special type.
	 * @param special the number of the special. 
	 * @throws IllegalArgumentException if special is outside the range 0 to 65535.
	 */
	public void setSpecial(int special)
	{
		RangeUtils.checkShortUnsigned("Special", special);
		this.special = special;
	}
	
	/**
	 * @return the linedef special type. 
	 */
	public int getSpecial()
	{
		return special;
	}
	
	/**
	 * Sets if this blocks - at the very least - player and monster movement.
	 * @param impassable true to set, false to clear.
	 */
	public void setImpassable(boolean impassable)
	{
		this.impassable = impassable;
	}
	
	/**
	 * @return true if this blocks - at the very least - player and monster movement, false if not.
	 */
	public boolean isImpassable()
	{
		return impassable;
	}
	
	/**
	 * Sets if this line blocks monsters.
	 * @param monsterBlocking true to set, false to clear.
	 */
	public void setMonsterBlocking(boolean monsterBlocking)
	{
		this.monsterBlocking = monsterBlocking;
	}
	
	/**
	 * @return true if this line blocks monsters, false if not.
	 */
	public boolean isMonsterBlocking()
	{
		return monsterBlocking;
	}
	
	/**
	 * Sets if this line is two-sided.
	 * @param twoSided true to set, false to clear.
	 */
	public void setTwoSided(boolean twoSided)
	{
		this.twoSided = twoSided;
	}
	
	/**
	 * @return true if this line is two-sided, false if not.
	 */
	public boolean isTwoSided()
	{
		return twoSided;
	}
	
	/**
	 * Sets if this line's upper texture is unpegged.
	 * @param upperUnpegged true to set, false to clear.
	 */
	public void setUpperUnpegged(boolean upperUnpegged)
	{
		this.upperUnpegged = upperUnpegged;
	}
	
	/**
	 * @return true if this line's upper texture is unpegged, false if not.
	 */
	public boolean isUpperUnpegged()
	{
		return upperUnpegged;
	}
	
	/**
	 * Sets if this line's lower texture is unpegged.
	 * @param lowerUnpegged true to set, false to clear.
	 */
	public void setLowerUnpegged(boolean lowerUnpegged)
	{
		this.lowerUnpegged = lowerUnpegged;
	}
	
	/**
	 * @return true if this line's lower texture is unpegged, false if not.
	 */
	public boolean isLowerUnpegged()
	{
		return lowerUnpegged;
	}
	
	/**
	 * Sets if this line is shown as one-sided on the automap.
	 * @param secret true to set, false to clear.
	 */
	public void setSecret(boolean secret)
	{
		this.secret = secret;
	}
	
	/**
	 * @return true if this line is shown as one-sided on the automap, false if not.
	 */
	public boolean isSecret()
	{
		return secret;
	}
	
	/**
	 * Sets if this line is always drawn on the automap.
	 * @param mapped true to set, false to clear.
	 */
	public void setMapped(boolean mapped)
	{
		this.mapped = mapped;
	}
	
	/**
	 * @return true if this line is always drawn on the automap, false if not.
	 */
	public boolean isMapped()
	{
		return mapped;
	}
	/**
	 * Sets if this line is not drawn on the automap. 
	 * @param notDrawn true to set, false to clear.
	 */
	public void setNotDrawn(boolean notDrawn)
	{
		this.notDrawn = notDrawn;
	}
	
	/**
	 * @return true if this line is not drawn on the automap, false if so.
	 */
	public boolean isNotDrawn()
	{
		return notDrawn;
	}
	
	/**
	 * Sets if this line blocks sound (must be doubled-up to block sound completely).
	 * @param soundBlocking true to set, false to clear.
	 */
	public void setSoundBlocking(boolean soundBlocking)
	{
		this.soundBlocking = soundBlocking;
	}
	
	/**
	 * @return true if this line blocks sound (must be doubled-up to block sound completely), false if not.
	 */
	public boolean isSoundBlocking()
	{
		return soundBlocking;
	}

}
