/*******************************************************************************
 * Copyright (c) 2015-2019 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.mtrop.doom.util.NameUtils;
import net.mtrop.doom.util.SerialReader;
import net.mtrop.doom.util.SerialWriter;

/**
 * Abstraction of a single entry from a WAD.
 * This entry contains NO DATA - this is a descriptor for the data in the originating WAD.
 * @author Matthew Tropiano
 */
public class WadEntry implements BinaryObject
{
	/** Byte length of this object. */
	public static final int LENGTH = 16;

	/** The name of the entry. */
	String name;
	/** The offset into the original WAD for the start of the data. */
	int offset;
	/** The size of the entry content in bytes. */
	int size;
	
	private WadEntry()
	{
		this(null, 0, 0);
	}
	
	private WadEntry(String name, int offset, int size)
	{
		this.name = name;
		this.offset = offset;
		this.size = size;
	}

	/**
	 * Creates a WadEntry.
	 * @param name the name of the entry.
	 * @param offset the offset into the WAD in bytes.
	 * @param size the size of the entry in bytes.
	 * @return the constructed WadEntry.
	 * @throws IllegalArgumentException if the name is invalid or the offset or size is negative.
	 */
	public static WadEntry create(String name, int offset, int size)
	{
		if (!NameUtils.isValidEntryName(name))
			throw new IllegalArgumentException("Entry name \""+name+"\" does not fit entry requirements.");
		if (offset < 0)
			throw new IllegalArgumentException("Entry offset is negative.");
		if (size < 0)
			throw new IllegalArgumentException("Entry size is negative.");
		
		return new WadEntry(name, offset, size); 
	}
	
	/**
	 * Creates a WadEntry.
	 * @param b the entry as serialized bytes.
	 * @return the constructed WadEntry.
	 * @throws IOException if an entry cannot be read.
	 * @throws IllegalArgumentException if the name is invalid or the offset or size is negative.
	 */
	public static WadEntry create(byte[] b) throws IOException
	{
		WadEntry out = new WadEntry();
		out.fromBytes(b);
		return out; 
	}
	
	/**
	 * @return the name of the entry.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the offset into the original WAD for the start of the data.
	 */
	public int getOffset()
	{
		return offset;
	}

	/**
	 * @return the size of the entry content in bytes.
	 */
	public int getSize()
	{
		return size;
	}
	
	/**
	 * Tests if this entry is a "marker" entry. Marker entries have 0 size.
	 * @return true if size = 0, false if not.
	 */
	public boolean isMarker()
	{
		return size == 0;
	}

	/**
	 * Returns this entry's name as how it is represented in a WAD.
	 * @return a byte array of length 8 containing the output data.
	 */
	public byte[] getNameBytes()
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		SerialWriter sw = new SerialWriter(SerialWriter.LITTLE_ENDIAN);

		try {
			sw.writeBytes(bos, name.getBytes("ASCII"));
			// null pad out to 8
			for (int n = name.length(); n < 8; n++)
				sw.writeByte(bos, (byte)0x00);
		} catch (IOException e) {
			// Should not happen.
		}
		
		return bos.toByteArray();
	}

	@Override
	public void readBytes(InputStream in) throws IOException
	{
		SerialReader sr = new SerialReader(SerialReader.LITTLE_ENDIAN);
		offset = sr.readInt(in);
		size = sr.readInt(in);
		name = NameUtils.nullTrim(sr.readString(in, 8, "ASCII"));
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		SerialWriter sw = new SerialWriter(SerialWriter.LITTLE_ENDIAN);
		sw.writeInt(out, offset);
		sw.writeInt(out, size);
		sw.writeBytes(out, name.getBytes("ASCII"));
		// null pad out to 8
		for (int n = name.length(); n < 8; n++)
			sw.writeByte(out, (byte)0x00);
	}

	@Override
	public String toString()
	{
		return String.format("WadEntry %-8s Offset: %d, Size: %d", name, offset, size);
	}
	
}
