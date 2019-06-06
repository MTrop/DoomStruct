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

import net.mtrop.doom.BinaryObject;
import net.mtrop.doom.util.RangeUtils;
import net.mtrop.doom.util.SerialReader;
import net.mtrop.doom.util.SerialWriter;
import net.mtrop.doom.util.SerializerUtils;
import net.mtrop.doom.util.Utils;

/**
 * Doom/Boom 14-byte format implementation of Linedef.
 * @author Matthew Tropiano
 */
public class DoomLinedef extends CommonLinedef implements BinaryObject
{
	/** Byte length of this object. */
	public static final int LENGTH = 14;

	/** Flag: (Boom) Line passes activation through to other lines. */
	protected boolean passThru;
	/** Linedef special tag. */
	protected int tag;

	/**
	 * Creates a new linedef.
	 */
	public DoomLinedef()
	{
	}

	/**
	 * Sets this linedef's special tag.
	 * @param tag the new tag.
	 */
	public void setTag(int tag)
	{
		RangeUtils.checkShortUnsigned("Tag", tag);
		this.tag = tag;
	}

	/**
	 * @return this linedef's special tag.
	 */
	public int getTag()
	{
		return tag;
	}

	/**
	 * Sets if this line's activated special does not block the activation search.
	 * @param passThru true to set, false to clear.
	 */
	public void setPassThru(boolean passThru)
	{
		this.passThru = passThru;
	}

	/**
	 * @return true if this line's activated special does not block the activation search, false if so.
	 */
	public boolean isPassThru()
	{
		return passThru;
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
		passThru = Utils.bitIsSet(flags, (1 << 9));
		
		special = sr.readUnsignedShort(in);
		tag = sr.readUnsignedShort(in);
		sidedefFrontIndex = sr.readShort(in);
		sidedefBackIndex = sr.readShort(in);
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException 
	{
		SerialWriter sw = new SerialWriter(SerialWriter.LITTLE_ENDIAN);
		sw.writeUnsignedShort(out, vertexStartIndex);
		sw.writeUnsignedShort(out, vertexEndIndex);
		
		sw.writeUnsignedShort(out, SerializerUtils.booleansToInt(
			impassable,
			monsterBlocking,
			twoSided,
			upperUnpegged,
			lowerUnpegged,
			secret,
			soundBlocking,
			notDrawn,
			mapped,
			passThru
		));
		
		sw.writeUnsignedShort(out, special);
		sw.writeUnsignedShort(out, tag);
		sw.writeShort(out, (short)sidedefFrontIndex);
		sw.writeShort(out, (short)sidedefBackIndex);
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
		if (passThru) sb.append(' ').append("PASSTHRU");
		
		sb.append(' ').append("Special ").append(special);
		sb.append(' ').append("Tag ").append(tag);
		return sb.toString();
	}
	
}
