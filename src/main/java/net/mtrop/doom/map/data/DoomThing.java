/*******************************************************************************
 * Copyright (c) 2015-2019 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.mtrop.doom.BinaryObject;
import net.mtrop.doom.util.SerialReader;
import net.mtrop.doom.util.SerialWriter;
import net.mtrop.doom.util.SerializerUtils;
import net.mtrop.doom.util.Utils;

/**
 * Doom/Boom/MBF 10-byte format implementation of Thing.
 * @author Matthew Tropiano
 */
public class DoomThing extends CommonThing implements BinaryObject
{
	/** Byte length of this object. */
	public static final int LENGTH = 10;
	
	/** Flag: Thing is not in Single Player. */
	protected boolean notSinglePlayer;
	/** Flag: Thing is not in Cooperative. */
	protected boolean notCooperative;
	/** Flag: Thing is not in Deathmatch. */
	protected boolean notDeathmatch;
	/** Flag: Thing is friendly. */
	protected boolean friendly;
	
	/**
	 * Creates a new thing.
	 */
	public DoomThing()
	{
		super();
		this.notSinglePlayer = false;
		this.notCooperative = false;
		this.notDeathmatch = false;
		this.friendly = false;
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
	 * @param notSinglePlayer true to set, false to clear.
	 */
	public void setNotSinglePlayer(boolean notSinglePlayer)
	{
		this.notSinglePlayer = notSinglePlayer;
	}

	/**
	 * @return true if this does NOT appear on cooperative, false if not.
	 */
	public boolean isNotCooperative()
	{
		return notCooperative;
	}

	/**
	 * Sets if this does NOT appear on cooperative.
	 * @param notCooperative true to set, false to clear.
	 */
	public void setNotCooperative(boolean notCooperative)
	{
		this.notCooperative = notCooperative;
	}

	/**
	 * @return true if this does NOT appear on deathmatch, false if not.
	 */
	public boolean isNotDeathmatch()
	{
		return notDeathmatch;
	}

	/**
	 * Sets if this does NOT appear on deathmatch.
	 * @param notDeathmatch true to set, false to clear.
	 */
	public void setNotDeathmatch(boolean notDeathmatch)
	{
		this.notDeathmatch = notDeathmatch;
	}

	/**
	 * @return true if this is flagged as friendly, false if not.
	 */
	public boolean isFriendly()
	{
		return friendly;
	}

	/**
	 * Sets if this is flagged as friendly.
	 * @param friendly true to set, false to clear.
	 */
	public void setFriendly(boolean friendly)
	{
		this.friendly = friendly;
	}

	@Override
	public void readBytes(InputStream in) throws IOException
	{
		SerialReader sr = new SerialReader(SerialReader.LITTLE_ENDIAN);
		x = sr.readShort(in);
		y = sr.readShort(in);
		angle = sr.readUnsignedShort(in);
		type = sr.readUnsignedShort(in);
		
		// bitflags
		int flags = sr.readUnsignedShort(in);
		easy = Utils.bitIsSet(flags, (1 << 0));
		medium = Utils.bitIsSet(flags, (1 << 1));
		hard = Utils.bitIsSet(flags, (1 << 2));
		ambush = Utils.bitIsSet(flags, (1 << 3));
		notSinglePlayer = Utils.bitIsSet(flags, (1 << 4));
		notDeathmatch = Utils.bitIsSet(flags, (1 << 5));
		notCooperative = Utils.bitIsSet(flags, (1 << 6));
		friendly = Utils.bitIsSet(flags, (1 << 7));
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		SerialWriter sw = new SerialWriter(SerialWriter.LITTLE_ENDIAN);
		sw.writeShort(out, (short)x);
		sw.writeShort(out, (short)y);
		sw.writeShort(out, (short)angle);
		sw.writeShort(out, (short)type);
		
		sw.writeUnsignedShort(out, SerializerUtils.booleansToInt(
			easy,
			medium,
			hard,
			ambush,
			notSinglePlayer,
			notDeathmatch,
			notCooperative,
			friendly
		));		
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Thing");
		sb.append(" (").append(x).append(", ").append(y).append(")");
		sb.append(" Type:").append(type);
		sb.append(" Angle:").append(angle);

		if (easy) sb.append(' ').append("EASY");
		if (medium) sb.append(' ').append("MEDIUM");
		if (hard) sb.append(' ').append("HARD");
		if (ambush) sb.append(' ').append("AMBUSH");
		if (notSinglePlayer) sb.append(' ').append("NOTSINGLEPLAYER");
		if (notDeathmatch) sb.append(' ').append("NOTDEATHMATCH");
		if (notCooperative) sb.append(' ').append("NOTCOOPERATIVE");
		if (friendly) sb.append(' ').append("FRIENDLY");
		
		return sb.toString();
	}

}
