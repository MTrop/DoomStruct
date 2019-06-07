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
import java.util.Arrays;

import net.mtrop.doom.util.SerialReader;
import net.mtrop.doom.util.SerialWriter;
import net.mtrop.doom.util.SerializerUtils;
import net.mtrop.doom.util.Utils;

/**
 * Hexen/ZDoom 16-byte format implementation of Linedef.
 * @author Matthew Tropiano
 */
public class ZDoomLinedef extends HexenLinedef 
{
	/** Byte length of this object. */
	public static final int LENGTH = 16;

	/** Flag: (ZDoom) Line is activated by players and monsters. */
	protected boolean activatedByMonsters;
	/** Flag: (ZDoom) Line blocks players. */
	protected boolean blocksPlayers;
	/** Flag: (ZDoom) Line blocks everything. */
	protected boolean blocksEverything;

	/**
	 * Creates a new linedef.
	 */
	public ZDoomLinedef()
	{
		this.activatedByMonsters = false;
		this.blocksPlayers = false;
		this.blocksEverything = false;
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
	 * @param activatedByMonsters true to set, false to clear.
	 */
	public void setActivatedByMonsters(boolean activatedByMonsters)
	{
		this.activatedByMonsters = activatedByMonsters;
	}
	
	/**
	 * @return true if this line blocks players, false if not.
	 */
	public boolean isBlocksPlayers()
	{
		return blocksPlayers;
	}
	
	/**
	 * Sets if this line blocks players, false if not.
	 * @param blocksPlayers true to set, false to clear.
	 */
	public void setBlocksPlayers(boolean blocksPlayers)
	{
		this.blocksPlayers = blocksPlayers;
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
	 * @param blocksEverything true to set, false to clear.
	 */
	public void setBlocksEverything(boolean blocksEverything)
	{
		this.blocksEverything = blocksEverything;
	}
	
	@Override
	public void readBytes(InputStream in) throws IOException
	{
		SerialReader sr = new SerialReader(SerialReader.LITTLE_ENDIAN);
		
		vertexStartIndex = sr.readUnsignedShort(in);
		vertexEndIndex = sr.readUnsignedShort(in);
		
		// bitflags
		int flags = sr.readUnsignedShort(in);
		impassable = Utils.bitIsSet(flags, (1 << 0));
		monsterBlocking = Utils.bitIsSet(flags, (1 << 1));
		twoSided = Utils.bitIsSet(flags, (1 << 2));
		upperUnpegged = Utils.bitIsSet(flags, (1 << 3));
		lowerUnpegged = Utils.bitIsSet(flags, (1 << 4));
		secret = Utils.bitIsSet(flags, (1 << 5));
		soundBlocking = Utils.bitIsSet(flags, (1 << 6));
		notDrawn = Utils.bitIsSet(flags, (1 << 7));
		mapped = Utils.bitIsSet(flags, (1 << 8));
		repeatable = Utils.bitIsSet(flags, (1 << 9));
		activatedByMonsters = Utils.bitIsSet(flags, (1 << 13));
		blocksPlayers =  Utils.bitIsSet(flags, (1 << 14));
		blocksEverything = Utils.bitIsSet(flags, (1 << 15));
		
		activationType = (0x01C00 & flags) >> 10;
		
		special = sr.readUnsignedByte(in);
		arguments[0] = sr.readUnsignedByte(in);
		arguments[1] = sr.readUnsignedByte(in);
		arguments[2] = sr.readUnsignedByte(in);
		arguments[3] = sr.readUnsignedByte(in);
		arguments[4] = sr.readUnsignedByte(in);

		sidedefFrontIndex = sr.readShort(in);
		sidedefBackIndex = sr.readShort(in);
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException 
	{
		SerialWriter sw = new SerialWriter(SerialWriter.LITTLE_ENDIAN);
		sw.writeUnsignedShort(out, vertexStartIndex);
		sw.writeUnsignedShort(out, vertexEndIndex);
		
		int flags = SerializerUtils.booleansToInt(
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
			blocksPlayers,
			blocksEverything
		);
		
		flags |= ACTIVATION_FLAGS[activationType];
		
		sw.writeUnsignedShort(out, flags);
		
		sw.writeByte(out, (byte)special);
		sw.writeByte(out, (byte)arguments[0]);
		sw.writeByte(out, (byte)arguments[1]);
		sw.writeByte(out, (byte)arguments[2]);
		sw.writeByte(out, (byte)arguments[3]);
		sw.writeByte(out, (byte)arguments[4]);

		sw.writeShort(out, (short)sidedefFrontIndex);
		sw.writeShort(out, (short)sidedefBackIndex);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Linedef");
		sb.append(' ').append(vertexStartIndex).append(" to ").append(vertexEndIndex);
		sb.append(' ').append("Front Sidedef ").append(sidedefFrontIndex);
		sb.append(' ').append("Back Sidedef ").append(sidedefBackIndex);
		sb.append(' ').append("Special ").append(special);
		sb.append(' ').append("Args ").append(Arrays.toString(arguments));
		sb.append(' ').append("Activation ").append(ACTIVATION_NAME[activationType]);
		
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
		if (blocksPlayers) sb.append(' ').append("BLOCKPLAYERS");
		if (blocksEverything) sb.append(' ').append("BLOCKEVERYTHING");
		
		return sb.toString();
	}
	
}
