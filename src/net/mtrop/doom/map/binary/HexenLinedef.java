/*******************************************************************************
 * Copyright (c) 2015 Matt Tropiano
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
 * Hexen/ZDoom 16-byte format implementation of Linedef.
 * @author Matthew Tropiano
 */
public class HexenLinedef extends CommonLinedef implements BinaryObject 
{
	/** Byte length of this object. */
	public static final int LENGTH = 16;

	/** Special activation: player walks over. */
	public static final int ACTIVATION_PLAYER_CROSSES = 0;
	/** Special activation: player uses. */
	public static final int ACTIVATION_PLAYER_USES = 1;
	/** Special activation: monster walks over. */
	public static final int ACTIVATION_MONSTER_CROSSES = 2;
	/** Special activation: projectile hits. */
	public static final int ACTIVATION_PROJECTILE_HITS = 3;
	/** Special activation: player bumps. */
	public static final int ACTIVATION_PLAYER_BUMPS = 4;
	/** Special activation: projectile crosses. */
	public static final int ACTIVATION_PROJECTILE_CROSSES = 5;
	/** Special activation: player uses (with passthru). */
	public static final int ACTIVATION_PLAYER_USES_PASSTHRU = 6;
	
	public static final String[] ACTIVATION_NAME = new String[]{
		"PlayerCrosses",
		"PlayerUses",
		"MonsterCrosses",
		"ProjectileHits",
		"PlayerBumps",
		"ProjectileCrosses",
		"PlayerUsesPassthru"
	};

	public static final int[] ACTIVATION_FLAGS = new int[]{
		0x00000,
		0x00400,
		0x00800,
		0x00C00,
		0x01000,
		0x01400,
		0x01800
	};
	
	/** Flag: Line special is repeated. */
	protected boolean repeatable;
	/** Flag: (ZDoom) Line is activated by players and monsters. */
	protected boolean activatedByMonsters;
	/** Flag: (ZDoom) Line blocks everything. */
	protected boolean blocksEverything;

	/** Special activation type. */
	protected int activationType;

	/** Thing action special arguments. */
	protected int[] arguments;

	/**
	 * Creates a new linedef.
	 */
	public HexenLinedef()
	{
		arguments = new int[5];
	}

	/**
	 * Reads and creates a new HexenLinedef from an array of bytes.
	 * This reads from the first 14 bytes of the array.
	 * @param bytes the byte array to read.
	 * @return a new HexenLinedef with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static HexenLinedef create(byte[] bytes) throws IOException
	{
		HexenLinedef out = new HexenLinedef();
		out.fromBytes(bytes);
		return out;
	}
	
	/**
	 * Reads and creates a new HexenLinedef from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for a {@link HexenLinedef} are read.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @return a new HexenLinedef with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static HexenLinedef read(InputStream in) throws IOException
	{
		HexenLinedef out = new HexenLinedef();
		out.readBytes(in);
		return out;
	}
	
	/**
	 * Reads and creates new HexenLinedefs from an array of bytes.
	 * This reads from the first 14 * <code>count</code> bytes of the array.
	 * @param bytes the byte array to read.
	 * @param count the amount of objects to read.
	 * @return an array of HexenLinedef objects with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static HexenLinedef[] create(byte[] bytes, int count) throws IOException
	{
		return read(new ByteArrayInputStream(bytes), count);
	}
	
	/**
	 * Reads and creates new HexenLinedefs from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for <code>count</code> {@link HexenLinedef}s are read.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @param count the amount of objects to read.
	 * @return an array of HexenLinedef objects with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static HexenLinedef[] read(InputStream in, int count) throws IOException
	{
		HexenLinedef[] out = new HexenLinedef[count];
		for (int i = 0; i < count; i++)
		{
			out[i] = new HexenLinedef();
			out[i].readBytes(in);
		}
		return out;
	}
	
	/**
	 * @return true if this line's special is repeatable, false if not.
	 */
	public boolean isRepeatable()
	{
		return repeatable;
	}
	
	/**
	 * Sets if this line's special is repeatable.
	 */
	public void setRepeatable(boolean repeatable)
	{
		this.repeatable = repeatable;
	}
	
	/**
	 * @return true if this line's special is activated by monsters, false if not.
	 */
	public boolean isActivatedByMonsters()
	{
		return activatedByMonsters;
	}
	
	/**
	 * Sets if this line's special is activated by monsters.
	 */
	public void setActivatedByMonsters(boolean activatedByMonsters)
	{
		this.activatedByMonsters = activatedByMonsters;
	}
	
	/**
	 * @return true if this line blocks everything, false if not.
	 */
	public boolean isBlocksEverything()
	{
		return blocksEverything;
	}
	
	/**
	 * Sets if this line blocks everything.
	 */
	public void setBlocksEverything(boolean blocksEverything)
	{
		this.blocksEverything = blocksEverything;
	}
	
	/**
	 * Sets the linedef special type. 
	 * @throws IllegalArgumentException if special is outside the range 0 to 255.
	 */
	public void setSpecial(int special)
	{
		RangeUtils.checkByteUnsigned("Special", special);
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
	 * Sets the special arguments.
	 * @throws IllegalArgumentException if length of arguments is greater than 5. 
	 */
	public void setArguments(int ... arguments)
	{
		if (arguments.length > 5)
			 throw new IllegalArgumentException("Length of arguments is greater than 5.");
		
		for (int i = 0; i < arguments.length; i++)
		{
			RangeUtils.checkByteUnsigned("Argument " + i, arguments[i]);
			this.arguments[i] = arguments[i];
		}
	}

	/**
	 * @return gets the array of special arguments.
	 */
	public int[] getArguments()
	{
		return arguments;
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
		
		vertexStartIndex = sr.readUnsignedShort();
		vertexEndIndex = sr.readUnsignedShort();
		
		// bitflags
		int flags = sr.readUnsignedShort();
		impassable = Common.bitIsSet(flags, (1 << 0));
		monsterBlocking = Common.bitIsSet(flags, (1 << 1));
		twoSided = Common.bitIsSet(flags, (1 << 2));
		upperUnpegged = Common.bitIsSet(flags, (1 << 3));
		lowerUnpegged = Common.bitIsSet(flags, (1 << 4));
		secret = Common.bitIsSet(flags, (1 << 5));
		soundBlocking = Common.bitIsSet(flags, (1 << 6));
		notDrawn = Common.bitIsSet(flags, (1 << 7));
		mapped = Common.bitIsSet(flags, (1 << 8));
		repeatable = Common.bitIsSet(flags, (1 << 9));
		activatedByMonsters = Common.bitIsSet(flags, (1 << 13));
		blocksEverything = Common.bitIsSet(flags, (1 << 15));
		
		activationType = (0x01C00 & flags) >> 10;
		
		special = sr.readUnsignedByte();
		arguments[0] = sr.readUnsignedByte();
		arguments[1] = sr.readUnsignedByte();
		arguments[2] = sr.readUnsignedByte();
		arguments[3] = sr.readUnsignedByte();
		arguments[4] = sr.readUnsignedByte();

		sidedefFrontIndex = sr.readShort();
		sidedefBackIndex = sr.readShort();
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException 
	{
		SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);
		sw.writeUnsignedShort(vertexStartIndex);
		sw.writeUnsignedShort(vertexEndIndex);
		
		int flags = Common.booleansToInt(
			impassable,
			monsterBlocking,
			twoSided,
			upperUnpegged,
			lowerUnpegged,
			secret,
			soundBlocking,
			notDrawn,
			mapped,
			repeatable,
			false,
			false,
			false,
			activatedByMonsters,
			false,
			blocksEverything
		);
		
		flags |= ACTIVATION_FLAGS[activationType];
		
		sw.writeUnsignedShort(flags);
		
		sw.writeByte((byte)special);
		sw.writeByte((byte)arguments[0]);
		sw.writeByte((byte)arguments[1]);
		sw.writeByte((byte)arguments[2]);
		sw.writeByte((byte)arguments[3]);
		sw.writeByte((byte)arguments[4]);

		sw.writeShort((short)sidedefFrontIndex);
		sw.writeShort((short)sidedefBackIndex);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Linedef");
		sb.append(' ').append(vertexStartIndex).append(" to ").append(vertexEndIndex);
		sb.append(' ').append("Sidedef ");
		sb.append(' ').append("Front ").append(sidedefFrontIndex);
		sb.append(' ').append("Back ").append(sidedefBackIndex);
		
		if (impassable) sb.append(' ').append("IMPASSABLE");
		if (monsterBlocking) sb.append(' ').append("MONSTERBLOCK");
		if (twoSided) sb.append(' ').append("TWOSIDED");
		if (upperUnpegged) sb.append(' ').append("UPPERUNPEGGED");
		if (lowerUnpegged) sb.append(' ').append("LOWERUNPEGGED");
		if (secret) sb.append(' ').append("SECRET");
		if (soundBlocking) sb.append(' ').append("SOUNDBLOCKING");
		if (notDrawn) sb.append(' ').append("NOTDRAWN");
		if (mapped) sb.append(' ').append("MAPPED");
		if (repeatable) sb.append(' ').append("REPEATABLE");
		if (activatedByMonsters) sb.append(' ').append("PLAYERSANDMONSTERACTIVATE");
		if (blocksEverything) sb.append(' ').append("BLOCKEVERYTHING");
		
		sb.append(' ').append("Special ").append(special);
		sb.append(' ').append("Args ").append(Arrays.toString(arguments));
		sb.append(' ').append("Activation ").append(ACTIVATION_NAME[activationType]);
		return sb.toString();
	}
	
}
