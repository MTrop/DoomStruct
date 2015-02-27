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
 * 12-byte BSP Segment information for a BSP tree in Doom.
 * @author Matthew Tropiano
 */
public class BSPSegment implements BinaryObject
{
	/** Byte length of this object. */
	public static final int LENGTH = 12;

	/** Direction along linedef (same). */
	public final static int DIRECTION_SAME_AS_LINEDEF = 0;
	/** Direction along linedef (opposite). */
	public final static int DIRECTION_OPPOSITE_LINEDEF = 1;

	/** Binary angle. */
	public final static int ANGLE_EAST = 0;
	/** Binary angle. */
	public final static int ANGLE_NORTH = 16384;
	/** Binary angle. */
	public final static int ANGLE_SOUTH = -16384;
	/** Binary angle. */
	public final static int ANGLE_WEST = -32768;

	/** This Seg's start vertex index reference. */
	protected int vertexStartIndex;
	/** This Seg's end vertex index reference. */
	protected int vertexEndIndex;
	/** This Seg's angle. */
	protected int angle;
	/** This Seg's linedef index. */
	protected int linedefIndex;
	/** This Seg's direction. */
	protected int direction;
	/** This Seg's offset along linedef. */
	protected int offset;

	/**
	 * Creates a new BSP Segment.
	 */
	public BSPSegment()
	{
		vertexStartIndex = -1;
		vertexEndIndex = -1;
		angle = 0;
		linedefIndex = -1;
		direction = DIRECTION_SAME_AS_LINEDEF;
		offset = 0;
	}

	/**
	 * Reads and creates a new BSPSegment from an array of bytes.
	 * This reads from the first 12 bytes of the array.
	 * @param bytes the byte array to read.
	 * @return a new BSPSegment with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static BSPSegment create(byte[] bytes) throws IOException
	{
		BSPSegment out = new BSPSegment();
		out.fromBytes(bytes);
		return out;
	}
	
	/**
	 * Reads and creates a new BSPSegment from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for a {@link BSPSegment} are read.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @return a new BSPSegment with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static BSPSegment read(InputStream in) throws IOException
	{
		BSPSegment out = new BSPSegment();
		out.readBytes(in);
		return out;
	}
	
	/**
	 * Reads and creates new BSPSegments from an array of bytes.
	 * This reads from the first 12 * <code>count</code> bytes of the array.
	 * @param bytes the byte array to read.
	 * @param count the amount of objects to read.
	 * @return an array of BSPSegment objects with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static BSPSegment[] create(byte[] bytes, int count) throws IOException
	{
		return read(new ByteArrayInputStream(bytes), count);
	}
	
	/**
	 * Reads and creates new BSPSegments from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for <code>count</code> {@link BSPSegment}s are read.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @param count the amount of objects to read.
	 * @return an array of BSPSegment objects with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static BSPSegment[] read(InputStream in, int count) throws IOException
	{
		BSPSegment[] out = new BSPSegment[count];
		for (int i = 0; i < count; i++)
		{
			out[i] = new BSPSegment();
			out[i].readBytes(in);
		}
		return out;
	}
	
	/** 
	 * Sets this Seg's start vertex index reference. 
	 * @throws IllegalArgumentException if the provided value is outside the range 0 to 65535.
	 */
	public void setVertexStartIndex(int val)
	{
		RangeUtils.checkShort("Vertex Start Index", vertexStartIndex);
		vertexStartIndex = val;
	}

	/** 
	 * Sets this Seg's end vertex index reference. 
	 * @throws IllegalArgumentException if the provided value is outside the range 0 to 65535.
	 */
	public void setVertexEndIndex(int val)
	{
		RangeUtils.checkShort("Vertex End Index", vertexEndIndex);
		vertexEndIndex = val;
	}

	/** 
	 * Sets this Seg's binary angle. 
	 * @throws IllegalArgumentException if the provided value is outside the range 0 to 65535.
	 */
	public void setAngle(int val)
	{
		RangeUtils.checkShort("Angle", angle);
		angle = val;
	}

	/** 
	 * Sets this Seg's linedef index. 
	 * @throws IllegalArgumentException if the provided value is outside the range 0 to 65535.
	 */
	public void setLinedefIndex(int val)
	{
		RangeUtils.checkShort("Linedef Index", linedefIndex);
		linedefIndex = val;
	}

	/** 
	 * Sets this Seg's direction. 
	 * @throws IllegalArgumentException if the provided value is neither {@link BSPSegment#DIRECTION_OPPOSITE_LINEDEF} to {@link BSPSegment#DIRECTION_SAME_AS_LINEDEF}.
	 */
	public void setDirection(int val)
	{
		RangeUtils.checkRange("Direction", DIRECTION_SAME_AS_LINEDEF, DIRECTION_OPPOSITE_LINEDEF, direction);
		direction = val;
	}

	/** 
	 * Sets this Seg's linedef offset (distance along line until start of seg). 
	 * @throws IllegalArgumentException if the provided value is outside the range 0 to 65535.
	 */
	public void setOffset(int val)
	{
		RangeUtils.checkShort("Offset", offset);
		offset = val;
	}

	/** 
	 * @return this Seg's start vertex index reference. 
	 */
	public int getVertexStartIndex()
	{
		return vertexStartIndex;
	}

	/** 
	 * @return this Seg's end vertex index reference. 
	 */
	public int getVertexEndIndex()
	{
		return vertexEndIndex;
	}

	/** 
	 * @return this Seg's angle in degrees. 
	 */
	public int getAngle()
	{
		return angle;
	}

	/** 
	 * @return this Seg's linedef index. 
	 */
	public int getLinedefIndex()
	{
		return linedefIndex;
	}

	/** 
	 * @return this Seg's direction. 
	 */
	public int getDirection()
	{
		return direction;
	}

	/** 
	 * @return this Seg's linedef offset. 
	 */
	public int getOffset()
	{
		return offset;
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
		angle = sr.readUnsignedShort();
		linedefIndex = sr.readUnsignedShort();
		direction = sr.readUnsignedShort();
		offset = sr.readUnsignedShort();
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);
		sw.writeUnsignedShort(vertexStartIndex);
		sw.writeUnsignedShort(vertexEndIndex);
		sw.writeUnsignedShort(angle);
		sw.writeUnsignedShort(linedefIndex);
		sw.writeUnsignedShort(direction);
		sw.writeUnsignedShort(offset);
	}

}
