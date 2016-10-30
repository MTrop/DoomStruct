/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map.binary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import com.blackrook.commons.Common;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

import net.mtrop.doom.BinaryObject;
import net.mtrop.doom.util.RangeUtils;

/**
 * Hexen/ZDoom 20-byte format implementation of Thing.
 * @author Matthew Tropiano
 */
public class HexenThing extends DoomThing implements BinaryObject
{
	/** Byte length of this object. */
	public static final int LENGTH = 20;

	/** Thing ID. */
	protected int id;
	/** Thing Z position relative to sector plane. */
	protected int z;

	/** Flag: Thing is dormant. */
	protected boolean dormant;
	/** Flag: Thing appears for fighters. */
	protected boolean fighter;
	/** Flag: Thing appears for cleric. */
	protected boolean cleric;
	/** Flag: Thing appears for mage. */
	protected boolean mage;
	
	/** Thing action special. */
	protected int special;
	/** Thing action special arguments. */
	protected int[] arguments;
	
	/**
	 * Creates a new thing.
	 */
	public HexenThing()
	{
		arguments = new int[5];
	}

	/**
	 * Reads and creates a new HexenThing from an array of bytes.
	 * This reads from the first 20 bytes of the array.
	 * @param bytes the byte array to read.
	 * @return a new HexenThing with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static HexenThing create(byte[] bytes) throws IOException
	{
		HexenThing out = new HexenThing();
		out.fromBytes(bytes);
		return out;
	}
	
	/**
	 * Reads and creates a new HexenThing from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for a {@link HexenThing} are read.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @return a new HexenThing with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static HexenThing read(InputStream in) throws IOException
	{
		HexenThing out = new HexenThing();
		out.readBytes(in);
		return out;
	}
	
	/**
	 * Reads and creates new HexenThings from an array of bytes.
	 * This reads from the first 20 * <code>count</code> bytes of the array.
	 * @param bytes the byte array to read.
	 * @param count the amount of objects to read.
	 * @return an array of HexenThing objects with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static HexenThing[] create(byte[] bytes, int count) throws IOException
	{
		return read(new ByteArrayInputStream(bytes), count);
	}
	
	/**
	 * Reads and creates new HexenThings from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for <code>count</code> {@link HexenThing}s are read.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @param count the amount of objects to read.
	 * @return an array of HexenThing objects with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static HexenThing[] read(InputStream in, int count) throws IOException
	{
		HexenThing[] out = new HexenThing[count];
		for (int i = 0; i < count; i++)
		{
			out[i] = new HexenThing();
			out[i].readBytes(in);
		}
		return out;
	}
	
	/**
	 * @return the Z position relative to sector plane.
	 */
	public int getZ()
	{
		return z;
	}

	/**
	 * Sets the Z position relative to sector plane.
	 * @param z the new Z position.
	 * @throws IllegalArgumentException if <code>z</code> is not between -32768 and 32767.
	 */
	public void setZ(int z)
	{
		RangeUtils.checkShort("Position Z", z);
		this.z = z;
	}

	/**
	 * @return the thing's id (for tagged specials).
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * Sets the thing's id. 
	 * @param id the new id.
	 * @throws IllegalArgumentException if <code>id</code> is not between 0 and 65535.
	 */
	public void setId(int id)
	{
		RangeUtils.checkShortUnsigned("Thing ID", id);
		this.id = id;
	}

	/**
	 * @return true if this is dormant, false if not.
	 */
	public boolean isDormant()
	{
		return dormant;
	}

	/**
	 * Sets if this is dormant.
	 * @param dormant true to set, false to clear.
	 */
	public void setDormant(boolean dormant)
	{
		this.dormant = dormant;
	}

	/**
	 * @return true if this appears on single player, false if not.
	 */
	public boolean isSinglePlayer()
	{
		return !notSinglePlayer;
	}

	/**
	 * Sets if this appears on single player.
	 * @param singlePlayer true to set, false to clear.
	 */
	public void setSinglePlayer(boolean singlePlayer)
	{
		this.notSinglePlayer = !singlePlayer;
	}

	/**
	 * @return true if this appears on cooperative, false if not.
	 */
	public boolean isCooperative()
	{
		return !notCooperative;
	}

	/**
	 * Sets if this appears on cooperative.
	 * @param cooperative true to set, false to clear.
	 */
	public void setCooperative(boolean cooperative)
	{
		this.notCooperative = !cooperative;
	}

	/**
	 * @return true if this appears on deathmatch, false if not.
	 */
	public boolean isDeathmatch()
	{
		return !notDeathmatch;
	}

	/**
	 * Sets if this appears on deathmatch.
	 * @param deathmatch true to set, false to clear.
	 */
	public void setDeathmatch(boolean deathmatch)
	{
		this.notDeathmatch = !deathmatch;
	}

	/**
	 * @return true if this appears for Fighters, false if not.
	 */
	public boolean isFighter()
	{
		return fighter;
	}
	
	/**
	 * Sets if this appears for Fighters.
	 * @param fighter true to set, false to clear.
	 */
	public void setFighter(boolean fighter)
	{
		this.fighter = fighter;
	}
	
	/**
	 * @return true if this appears for Clerics, false if not.
	 */
	public boolean isCleric()
	{
		return cleric;
	}
	
	/**
	 * Sets if this appears for Cleric.
	 * @param cleric true to set, false to clear.
	 */
	public void setCleric(boolean cleric)
	{
		this.cleric = cleric;
	}
	
	/**
	 * @return true if this appears for Mages, false if not.
	 */
	public boolean isMage()
	{
		return mage;
	}
	
	/**
	 * Sets if this appears for Mages.
	 * @param mage true to set, false to clear.
	 */
	public void setMage(boolean mage)
	{
		this.mage = mage;
	}
	
	/**
	 * @return the special action for this thing.
	 */
	public int getSpecial()
	{
		return special;
	}

	/**
	 * Sets the special action for this thing.
	 * @param special the thing special to call on activation.
	 * @throws IllegalArgumentException if special is outside the range 0 to 255.
	 */
	public void setSpecial(int special)
	{
		RangeUtils.checkByteUnsigned("Special", special);
		this.special = special;
	}
	
	/**
	 * Gets the special arguments copied into a new array. 
	 * @return gets the array of special arguments.
	 */
	public int[] getArguments()
	{
		int[] out = new int[5];
		System.arraycopy(arguments, 0, out, 0, 5);
		return out;
	}

	/**
	 * Gets a special argument.
	 * @param n the argument index (up to 4) 
	 * @return the argument value.
	 * @throws ArrayIndexOutOfBoundsException if <code>n</code> is less than 0 or greater than 4. 
	 */
	public int getArgument(int n)
	{
		return arguments[n];
	}

	/**
	 * Sets the special arguments.
	 * @param arguments the argument values to set.
	 * @throws IllegalArgumentException if length of arguments is greater than 5, or any argument is less than 0 or greater than 255. 
	 */
	public void setArguments(int ... arguments)
	{
		if (arguments.length > 5)
			 throw new IllegalArgumentException("Length of arguments is greater than 5.");

		int i;
		for (i = 0; i < arguments.length; i++)
		{
			RangeUtils.checkByteUnsigned("Argument " + i, arguments[i]);
			this.arguments[i] = arguments[i];
		}
		for (; i < 5; i++)
		{
			this.arguments[i] = 0;
		}
		
	}
	
	@Override
	public byte[] toBytes()
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try { writeBytes(bos); } catch (IOException e) { /* Shouldn't happen. */ }
		return bos.toByteArray();
	}

	@Override
	public void fromBytes(byte[] data) throws IOException
	{
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		readBytes(bin);
		Common.close(bin);
	}

	@Override
	public void readBytes(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in, SuperReader.LITTLE_ENDIAN);
		id = sr.readUnsignedShort();
		x = sr.readShort();
		y = sr.readShort();
		z = sr.readShort();
		angle = sr.readShort();
		type = sr.readShort();
		
		int flags = sr.readUnsignedShort();
		easy = Common.bitIsSet(flags, (1 << 0));
		medium = Common.bitIsSet(flags, (1 << 1));
		hard = Common.bitIsSet(flags, (1 << 2));
		ambush = Common.bitIsSet(flags, (1 << 3));
		dormant = Common.bitIsSet(flags, (1 << 4));
		fighter = Common.bitIsSet(flags, (1 << 5));
		cleric = Common.bitIsSet(flags, (1 << 6));
		mage = Common.bitIsSet(flags, (1 << 7));
		notSinglePlayer = !Common.bitIsSet(flags, (1 << 8));
		notCooperative = !Common.bitIsSet(flags, (1 << 9));
		notDeathmatch = !Common.bitIsSet(flags, (1 << 10));
		
		special = sr.readUnsignedByte();
		arguments[0] = sr.readUnsignedByte();
		arguments[1] = sr.readUnsignedByte();
		arguments[2] = sr.readUnsignedByte();
		arguments[3] = sr.readUnsignedByte();
		arguments[4] = sr.readUnsignedByte();
		
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);
		sw.writeUnsignedShort(id);
		sw.writeShort((short)x);
		sw.writeShort((short)y);
		sw.writeShort((short)z);
		sw.writeShort((short)angle);
		sw.writeShort((short)type);
		
		sw.writeUnsignedShort(Common.booleansToInt(
			easy,
			medium,
			hard,
			ambush,
			dormant,
			fighter,
			cleric,
			mage,
			!notSinglePlayer,
			!notCooperative,
			!notDeathmatch
		));
		
		sw.writeByte((byte)special);
		sw.writeByte((byte)arguments[0]);
		sw.writeByte((byte)arguments[1]);
		sw.writeByte((byte)arguments[2]);
		sw.writeByte((byte)arguments[3]);
		sw.writeByte((byte)arguments[4]);
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
		
		if (easy) sb.append(' ').append("EASY");
		if (medium) sb.append(' ').append("MEDIUM");
		if (hard) sb.append(' ').append("HARD");
		if (ambush) sb.append(' ').append("AMBUSH");
		if (dormant) sb.append(' ').append("DORMANT");
		if (!notSinglePlayer) sb.append(' ').append("SINGLEPLAYER");
		if (!notCooperative) sb.append(' ').append("COOPERTIVE");
		if (!notDeathmatch) sb.append(' ').append("DEATHMATCH");
		if (fighter) sb.append(' ').append("FIGHTER");
		if (cleric) sb.append(' ').append("CLERIC");
		if (mage) sb.append(' ').append("MAGE");
		
		sb.append(' ').append("Special ").append(special);
		sb.append(' ').append("Args ").append(Arrays.toString(arguments));
		
		return sb.toString();
	}

}
