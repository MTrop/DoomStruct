/*******************************************************************************
 * Copyright (c) 2015 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map.binary;

import net.mtrop.doom.map.MapObject;
import net.mtrop.doom.util.RangeUtils;

/**
 * Contains common elements of all binary things.
 * @author Matthew Tropiano
 */
public abstract class CommonThing implements MapObject
{
	/** Thing X position. */
	protected int x;
	/** Thing Y position. */
	protected int y;
	/** Thing angle. */
	protected int angle;
	/** Thing type (editor number). */
	protected int type;

	/** Flag: Thing is present on easy skill. */
	protected boolean easy;
	/** Flag: Thing is present on medium skill. */
	protected boolean medium;
	/** Flag: Thing is present on hard skill. */
	protected boolean hard;
	/** Flag: Thing ambushes player (does not activate on sound). */
	protected boolean ambush;
	/** Flag: Thing is not in Single Player. */
	protected boolean notSinglePlayer;

	/**
	 * Creates a new thing.
	 */
	public CommonThing()
	{
	}

	/**
	 * Sets the coordinates of this thing.
	 * @param x the x-coordinate value.
	 * @param y the y-coordinate value.
	 */
	public void set(int x, int y)
	{
		setX(x);
		setY(y);
	}

	/**
	 * @return the position X-coordinate.
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * Sets the position X-coordinate.
	 * @throws IllegalArgumentException if x is outside of the range -32768 to 32767.
	 */
	public void setX(int x)
	{
		RangeUtils.checkShort("Position X", x);
		this.x = x;
	}

	/**
	 * @return the position Y-coordinate.
	 */
	public int getY()
	{
		return y;
	}

	/**
	 * Sets the position Y-coordinate.
	 * @throws IllegalArgumentException if y is outside of the range -32768 to 32767.
	 */
	public void setY(int y)
	{
		RangeUtils.checkShort("Position Y", y);
		this.y = y;
	}

	/**
	 * @return the angle (in degrees).
	 */
	public int getAngle()
	{
		return angle;
	}

	/**
	 * Sets the angle (in degrees). 
	 * @throws IllegalArgumentException if angle is outside of the range -32768 to 32767.
	 */
	public void setAngle(int angle)
	{
		RangeUtils.checkShort("Angle", angle);
		this.angle = angle;
	}

	/**
	 * @return thing type (a.k.a. editor number).
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * Sets thing type (a.k.a. editor number). 
	 * @throws IllegalArgumentException if type is outside of the range 0 to 65535.
	 */
	public void setType(int type)
	{
		RangeUtils.checkShortUnsigned("Type", type);
		this.type = type;
	}

	/**
	 * @return true if this appears on skills considered "easy," false if not.
	 */
	public boolean isEasy()
	{
		return easy;
	}

	/**
	 * Sets if this appears on skills considered "easy."
	 */
	public void setEasy(boolean easy)
	{
		this.easy = easy;
	}

	/**
	 * @return true if this appears on skills considered "medium," false if not.
	 */
	public boolean isMedium()
	{
		return medium;
	}

	/**
	 * Sets if this appears on skills considered "medium."
	 */
	public void setMedium(boolean medium)
	{
		this.medium = medium;
	}

	/**
	 * @return true if this appears on skills considered "hard," false if not.
	 */
	public boolean isHard()
	{
		return hard;
	}

	/**
	 * Sets if this appears on skills considered "hard."
	 */
	public void setHard(boolean hard)
	{
		this.hard = hard;
	}

	/**
	 * @return true if this ambushes players, false if not.
	 */
	public boolean isAmbush()
	{
		return ambush;
	}

	/**
	 * Sets if this ambushes players.
	 */
	public void setAmbush(boolean ambush)
	{
		this.ambush = ambush;
	}

	/**
	 * @return true if this does NOT appear on single player, false if not.
	 */
	public boolean isNotSinglePlayer()
	{
		return notSinglePlayer;
	}

	/**
	 * Sets if this does NOT appear on single player.
	 */
	public void setNotSinglePlayer(boolean notSinglePlayer)
	{
		this.notSinglePlayer = notSinglePlayer;
	}
	

}
