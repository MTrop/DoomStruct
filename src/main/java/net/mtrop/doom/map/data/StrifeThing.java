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

import net.mtrop.doom.util.SerialReader;
import net.mtrop.doom.util.SerialWriter;
import net.mtrop.doom.util.SerializerUtils;
import net.mtrop.doom.util.Utils;

/**
 * Doom/Boom 10-byte format implementation of Thing that uses
 * Strife flags. This is essentially the same structure as a {@link DoomThing},
 * except interpreted differently.
 * @author Matthew Tropiano
 */
public class StrifeThing extends CommonThing
{
	/** Byte length of this object. */
	public static final int LENGTH = 10;

	/** Flag: Thing spawns in standing mode. */
	protected boolean standing;
	/** Flag: Thing is only in multiplayer. */
	protected boolean multiplayer;
	/** Flag: Thing is an ally. */
	protected boolean ally;
	/** Flag: Thing is 25% translucent. */
	protected boolean translucent25;
	/** Flag: Thing is 75% translucent. */
	protected boolean translucent75;
	
	/**
	 * Creates a new thing.
	 */
	public StrifeThing()
	{
		this.standing = false;
		this.multiplayer = false;
		this.ally = false;
		this.translucent25 = false;
		this.translucent75 = false;
	}

	/**
	 * @return true if this is flagged as an ally, false if not.
	 */
	public boolean isAlly()
	{
		return ally;
	}

	/**
	 * Sets if this is flagged as an ally.
	 * @param ally true to set, false to clear.
	 */
	public void setAlly(boolean ally)
	{
		this.ally = ally;
	}
	
	/**
	 * @return true if this is flagged as 25% translucent, false if not.
	 */
	public boolean isTranslucent25()
	{
		return translucent25;
	}
	
	/**
	 * Sets if this is flagged as 25% translucent.
	 * @param translucent25 true to set, false to clear.
	 */
	public void setTranslucent25(boolean translucent25)
	{
		this.translucent25 = translucent25;
	}
	
	/**
	 * @return true if this is flagged as 75% translucent, false if not.
	 */
	public boolean isTranslucent75()
	{
		return translucent75;
	}

	/**
	 * Sets if this is flagged as 75% translucent.
	 * @param translucent75 true to set, false to clear.
	 */
	public void setTranslucent75(boolean translucent75)
	{
		this.translucent75 = translucent75;
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
		multiplayer = Utils.bitIsSet(flags, (1 << 4));
		ambush = Utils.bitIsSet(flags, (1 << 5));
		ally = Utils.bitIsSet(flags, (1 << 6));
		translucent25 = Utils.bitIsSet(flags, (1 << 8));
		translucent75 = Utils.bitIsSet(flags, (1 << 9));
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
			standing,
			multiplayer,
			ambush,
			ally,
			false,
			translucent25,
			translucent75
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
		if (standing) sb.append(' ').append("STANDING");
		if (ambush) sb.append(' ').append("AMBUSH");
		if (multiplayer) sb.append(' ').append("NOTSINGLEPLAYER");
		if (ally) sb.append(' ').append("ALLY");
		if (translucent25) sb.append(' ').append("TRANSLUCENT25");
		if (translucent75) sb.append(' ').append("TRANSLUCENT75");
		
		return sb.toString();
	}

}
