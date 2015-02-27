/*******************************************************************************
 * Copyright (c) 2015 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map.bsp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.mtrop.doom.BinaryObject;
import net.mtrop.doom.util.RangeUtils;

import com.blackrook.commons.Common;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * 4-byte BSP Subsector information that lists all of the BSP segment indices for a sector.
 * These are essentially the mappings of Nodes to other nodes.
 * @author Matthew Tropiano
 */
public class BSPSubsector implements BinaryObject
{
	/** Byte length of this object. */
	public static final int LENGTH = 4;

	/** This Subsector's BSP Segment count. */
	protected int segCount;
	/** This Subsector's starting segment index. */
	protected int segStartIndex;

	/**
	 * Creates a new BSP Subsector.
	 */
	public BSPSubsector()
	{
		segCount = 0;
		segStartIndex = -1;
	}
	
	/**
	 * Reads and creates a new BSPSubsector from an array of bytes.
	 * This reads from the first 4 bytes of the array.
	 * @param bytes the byte array to read.
	 * @return a new BSPSubsector with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static BSPSubsector create(byte[] bytes) throws IOException
	{
		BSPSubsector out = new BSPSubsector();
		out.fromBytes(bytes);
		return out;
	}
	
	/**
	 * Reads and creates a new BSPSubsector from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for a {@link BSPSubsector} are read.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @return a new BSPSubsector with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static BSPSubsector read(InputStream in) throws IOException
	{
		BSPSubsector out = new BSPSubsector();
		out.readBytes(in);
		return out;
	}
	
	/**
	 * Reads and creates new BSPSubsectors from an array of bytes.
	 * This reads from the first 4 * <code>count</code> bytes of the array.
	 * @param bytes the byte array to read.
	 * @param count the amount of objects to read.
	 * @return an array of BSPSubsector objects with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static BSPSubsector[] create(byte[] bytes, int count) throws IOException
	{
		return read(new ByteArrayInputStream(bytes), count);
	}
	
	/**
	 * Reads and creates new BSPSubsectors from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for <code>count</code> {@link BSPSubsector}s are read.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @param count the amount of objects to read.
	 * @return an array of BSPSubsector objects with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static BSPSubsector[] read(InputStream in, int count) throws IOException
	{
		BSPSubsector[] out = new BSPSubsector[count];
		for (int i = 0; i < count; i++)
		{
			out[i] = new BSPSubsector();
			out[i].readBytes(in);
		}
		return out;
	}
	
	/**
	 * @return the amount of BSPSegments pointed to by this subsector.
	 */
	public int getSegCount()
	{
		return segCount;
	}

	/**
	 * Sets the amount of BSPSegments pointed to by this subsector.
	 * @throws IllegalArgumentException if the provided value is outside the range 0 to 65535.
	 */
	public void setSegCount(int segCount)
	{
		RangeUtils.checkShortUnsigned("Segment Count", segCount);
		this.segCount = segCount;
	}

	/**
	 * @return the starting offset index of this subsector's BSPSegments in the Segs lump.
	 */
	public int getSegStartIndex()
	{
		return segStartIndex;
	}

	/**
	 * Sets the starting offset index of this subsector's BSPSegments in the Segs lump.
	 * @throws IllegalArgumentException if the provided value is outside the range 0 to 65535.
	 */
	public void setSegStartIndex(int segStartIndex)
	{
		RangeUtils.checkShortUnsigned("Segment Start Index", segStartIndex);
		this.segStartIndex = segStartIndex;
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
		segCount = sr.readUnsignedShort();
		segStartIndex = sr.readUnsignedShort();
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);
		sw.writeUnsignedShort(segCount);
		sw.writeUnsignedShort(segStartIndex);
	}

}
