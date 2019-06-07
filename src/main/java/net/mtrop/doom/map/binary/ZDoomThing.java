/*******************************************************************************
 * Copyright (c) 2015-2019 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map.binary;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import net.mtrop.doom.BinaryObject;
import net.mtrop.doom.util.SerialReader;
import net.mtrop.doom.util.SerialWriter;
import net.mtrop.doom.util.SerializerUtils;
import net.mtrop.doom.util.Utils;

/**
 * ZDoom 20-byte format implementation of Thing.
 * @author Matthew Tropiano
 */
public class ZDoomThing extends HexenThing implements BinaryObject
{
	/** Byte length of this object. */
	public static final int LENGTH = 20;

	/** Flag: Thing spawns in standing mode. */
	protected boolean standing;
	/** Flag: Thing is 25% translucent. */
	protected boolean translucent;
	/** Flag: Thing is 25% translucent. */
	protected boolean invisible;
	/** Flag: Thing is an ally. */
	protected boolean friendly;

	/**
	 * Creates a new thing.
	 */
	public ZDoomThing()
	{
		super();
		this.standing = false;
		this.translucent = false;
		this.invisible = false;
		this.friendly = false;
	}

	/**
	 * @return true if this is flagged as standing, false if not.
	 */
	public boolean isStanding()
	{
		return standing;
	}
	
	/**
	 * Sets if this is flagged as standing, false if not.
	 * @param standing true to set, false to clear.
	 */
	public void setStanding(boolean standing)
	{
		this.standing = standing;
	}
	
	/**
	 * @return true if this is flagged as translucent, false if not.
	 */
	public boolean isTranslucent()
	{
		return translucent;
	}
	
	/**
	 * Sets if this is flagged as translucent, false if not.
	 * @param translucent true to set, false to clear.
	 */
	public void setTranslucent(boolean translucent)
	{
		this.translucent = translucent;
	}
	
	/**
	 * @return true if this is flagged as invisible, false if not.
	 */
	public boolean isInvisible()
	{
		return invisible;
	}
	
	/**
	 * Sets if this is flagged as invisible, false if not.
	 * @param invisible true to set, false to clear.
	 */
	public void setInvisible(boolean invisible)
	{
		this.invisible = invisible;
	}
	
	/**
	 * @return true if this is flagged as friendly, false if not.
	 */
	public boolean isFriendly()
	{
		return friendly;
	}
	
	/**
	 * Sets if this is flagged as friendly, false if not.
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
		id = sr.readUnsignedShort(in);
		x = sr.readShort(in);
		y = sr.readShort(in);
		z = sr.readShort(in);
		angle = sr.readShort(in);
		type = sr.readShort(in);
		
		int flags = sr.readUnsignedShort(in);
		easy = Utils.bitIsSet(flags, (1 << 0));
		medium = Utils.bitIsSet(flags, (1 << 1));
		hard = Utils.bitIsSet(flags, (1 << 2));
		ambush = Utils.bitIsSet(flags, (1 << 3));
		dormant = Utils.bitIsSet(flags, (1 << 4));
		fighter = Utils.bitIsSet(flags, (1 << 5));
		cleric = Utils.bitIsSet(flags, (1 << 6));
		mage = Utils.bitIsSet(flags, (1 << 7));
		singlePlayer = !Utils.bitIsSet(flags, (1 << 8));
		cooperative = !Utils.bitIsSet(flags, (1 << 9));
		deathmatch = !Utils.bitIsSet(flags, (1 << 10));
		standing = !Utils.bitIsSet(flags, (1 << 11));
		translucent = !Utils.bitIsSet(flags, (1 << 12));
		invisible = !Utils.bitIsSet(flags, (1 << 13));
		friendly = !Utils.bitIsSet(flags, (1 << 14));
		
		special = sr.readUnsignedByte(in);
		arguments[0] = sr.readUnsignedByte(in);
		arguments[1] = sr.readUnsignedByte(in);
		arguments[2] = sr.readUnsignedByte(in);
		arguments[3] = sr.readUnsignedByte(in);
		arguments[4] = sr.readUnsignedByte(in);
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		SerialWriter sw = new SerialWriter(SerialWriter.LITTLE_ENDIAN);
		sw.writeUnsignedShort(out, id);
		sw.writeShort(out, (short)x);
		sw.writeShort(out, (short)y);
		sw.writeShort(out, (short)z);
		sw.writeShort(out, (short)angle);
		sw.writeShort(out, (short)type);
		
		sw.writeUnsignedShort(out, SerializerUtils.booleansToInt(
			easy,
			medium,
			hard,
			ambush,
			dormant,
			fighter,
			cleric,
			mage,
			singlePlayer,
			cooperative,
			deathmatch,
			standing,
			translucent,
			invisible,
			friendly
		));
		
		sw.writeByte(out, (byte)special);
		sw.writeByte(out, (byte)arguments[0]);
		sw.writeByte(out, (byte)arguments[1]);
		sw.writeByte(out, (byte)arguments[2]);
		sw.writeByte(out, (byte)arguments[3]);
		sw.writeByte(out, (byte)arguments[4]);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Thing");
		sb.append(" (").append(x).append(", ").append(y).append(")");
		sb.append(" Z:").append(z);
		sb.append(" Type:").append(type);
		sb.append(" Angle:").append(angle);
		sb.append(" ID:").append(id);
		sb.append(" Special ").append(special);
		sb.append(" Args ").append(Arrays.toString(arguments));
		
		if (easy) sb.append(' ').append("EASY");
		if (medium) sb.append(' ').append("MEDIUM");
		if (hard) sb.append(' ').append("HARD");
		if (ambush) sb.append(' ').append("AMBUSH");
		if (dormant) sb.append(' ').append("DORMANT");
		if (fighter) sb.append(' ').append("FIGHTER");
		if (cleric) sb.append(' ').append("CLERIC");
		if (mage) sb.append(' ').append("MAGE");
		if (singlePlayer) sb.append(' ').append("SINGLEPLAYER");
		if (cooperative) sb.append(' ').append("COOPERTIVE");
		if (deathmatch) sb.append(' ').append("DEATHMATCH");
		if (standing) sb.append(' ').append("STANDING");
		if (translucent) sb.append(' ').append("TRANSLUCENT");
		if (invisible) sb.append(' ').append("INVISIBLE");
		if (friendly) sb.append(' ').append("FRIENDLY");
		
		return sb.toString();
	}

}
