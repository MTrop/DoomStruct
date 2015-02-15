package net.mtrop.doom.map.binary;

import net.mtrop.doom.map.MapObject;

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
	 */
	public void setVertexStartIndex(int vertexStartIndex)
	{
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
	 */
	public void setVertexEndIndex(int vertexEndIndex)
	{
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
	 */
	public void setSidedefFrontIndex(int sidedefFrontIndex)
	{
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
	 */
	public void setSidedefBackIndex(int sidedefBackIndex)
	{
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
	 */
	public void setSpecial(int special)
	{
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
