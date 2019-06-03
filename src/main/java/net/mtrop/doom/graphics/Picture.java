/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.graphics;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.mtrop.doom.BinaryObject;
import net.mtrop.doom.GraphicObject;
import net.mtrop.doom.util.RangeUtils;

import com.blackrook.commons.Common;
import com.blackrook.commons.hash.HashMap;
import com.blackrook.commons.math.RMath;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * Doom graphic data stored as column-major indices (patches and most graphics with baked-in offsets). 
 * Useful for editing/displaying graphics.
 * @author Matthew Tropiano
 */
public class Picture implements BinaryObject, GraphicObject
{
	public static final short PIXEL_TRANSLUCENT = -1;
	
	/** The pixel data. */
	private short[][] pixels; 
	/** The offset from the center, horizontally, in pixels. */
	private int offsetX; 
	/** The offset from the center, vertically, in pixels. */
	private int offsetY; 

	/**
	 * Creates a new picture with dimensions (1, 1).
	 */
	public Picture()
	{
		this(1, 1);
	}
	
	/**
	 * Creates a new patch.
	 * @param width		the width of the patch in pixels.
	 * @param height	the height of the patch in pixels.
	 */
	public Picture(int width, int height)
	{
		if (width < 1 || height < 1)
			throw new IllegalArgumentException("Width or height cannot be less than 1.");
		if (width > 65535 || height > 65535)
			throw new IllegalArgumentException("Width or height cannot be greater than 65535.");
		offsetX = 0;
		offsetY = 0;
		setDimensions(width, height);
	}

	/**
	 * Reads and creates a new Picture object from an array of bytes.
	 * This reads until it reaches the end of the picture data.
	 * @param bytes the byte array to read.
	 * @return a new Picture from the data.
	 * @throws IOException if the stream cannot be read.
	 */
	public static Picture create(byte[] bytes) throws IOException
	{
		Picture out = new Picture();
		out.fromBytes(bytes);
		return out;
	}

	/**
	 * Reads and creates a new Picture from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for the full {@link Picture} are read.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @return a new Picture from the data.
	 * @throws IOException if the stream cannot be read.
	 */
	public static Picture read(InputStream in) throws IOException
	{
		Picture out = new Picture();
		out.readBytes(in);
		return out;
	}

	/**
	 * Sets the dimensions of this patch.
	 * WARNING: This will clear all of the data in the patch.
	 * @param width	the width of the patch in pixels.
	 * @param height the height of the patch in pixels.
	 */
	public void setDimensions(int width, int height)
	{
		pixels = new short[width][height];
		for (int i = 0; i < pixels.length; i++)
			for (int j = 0; j < pixels[i].length; j++)
				pixels[i][j] = PIXEL_TRANSLUCENT;
	}
	
	@Override
	public int getOffsetX()
	{
		return offsetX;
	}

	/**
	 * Sets the offset from the center, horizontally, in pixels.
	 * @param offsetX the new X offset.
	 * @throws IllegalArgumentException if the offset is outside the range -32768 to 32767.
	 */
	public void setOffsetX(int offsetX)
	{
		RangeUtils.checkShort("Offset X", offsetX);
		this.offsetX = offsetX;
	}

	@Override
	public int getOffsetY()
	{
		return offsetY;
	}

	/**
	 * Sets the offset from the center, vertically, in pixels.
	 * @param offsetY the new Y offset.
	 * @throws IllegalArgumentException if the offset is outside the range -32768 to 32767.
	 */
	public void setOffsetY(int offsetY)
	{
		RangeUtils.checkShort("Offset Y", offsetY);
		this.offsetY = offsetY;
	}

	/**
	 * Returns the width of this graphic in pixels.
	 */
	public int getWidth()
	{
		return pixels.length;
	}
	
	/**
	 * Returns the height of this graphic in pixels.
	 */
	public int getHeight()
	{
		return pixels[0].length;
	}
	
	/**
	 * Sets the pixel data at a location in the patch.
	 * Valid values are in the range of -1 to 255, with
	 * 0 to 255 being palette indexes and -1 being translucent
	 * pixel information. Values outside this range are CLAMPED into the
	 * range.
	 * @param x	patch x-coordinate.
	 * @param y	patch y-coordinate.
	 * @param value	the value to set.
	 * @throws IllegalArgumentException if the offset is outside the range -1 to 255.
	 */
	public void setPixel(int x, int y, int value)
	{
		RangeUtils.checkRange("Pixel ("+x+", "+y+")", -1, 255, value);
		pixels[x][y] = (short)RMath.clampValue(value, -1, 255);
	}
	
	/**
	 * Gets the pixel data at a location in the patch.
	 * @param x	patch x-coordinate.
	 * @param y	patch y-coordinate.
	 * @return a palette index value from 0 to 255 or PIXEL_TRANSLUCENT if the pixel is not filled in.
	 */
	public int getPixel(int x, int y)
	{
		return pixels[x][y];
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
		SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
		setDimensions(sr.readUnsignedShort(), sr.readUnsignedShort());
		offsetX = sr.readShort();
		offsetY = sr.readShort();

		// load offset table.
		int[] columnOffsets = sr.readInts(getWidth());
		
		// data must be treated as a stream: find highest short offset so that the reading can stop.
		int offMax = -1;
		for (int i : columnOffsets)
		{
			offMax = i > offMax ? i : offMax;
		}
		
		// precache columns at each particular offset: patch may be compressed.
		HashMap<Integer, byte[]> columnData = new HashMap<Integer, byte[]>();

		for (int i = 0; i < columnOffsets.length; i++)
			columnData.put(columnOffsets[i],columnRead(sr));
			
		for (int x = 0; x < columnOffsets.length; x++)
		{
			int y = 0;
			byte[] b = columnData.get(columnOffsets[x]);
			for (int i = 0; i < b.length; i++)
			{
				y = b[i++] & 0x0ff;
				int span = b[i++] & 0x0ff;
				for (int j = 0; j < span; j++)
					pixels[x][y+j] = (short)(b[i+j] & 0x0ff);
				i += span-1;
			}
		}
				
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);
		sw.writeUnsignedShort(pixels.length);
		sw.writeUnsignedShort(pixels[0].length);
		sw.writeShort((short)offsetX);
		sw.writeShort((short)offsetY);
		
		int[] columnOffsets = new int[getWidth()];

		final int STATE_TRANS = 0;
		final int STATE_COLOR = 1;
		
		int columnOffs = 8 + (4 * getWidth());
		ByteArrayOutputStream dataBytes = new ByteArrayOutputStream();
		
		for (int i = 0; i < columnOffsets.length; i++)
		{
			columnOffsets[i] = columnOffs;
			ByteArrayOutputStream columnBytes = new ByteArrayOutputStream();
			ByteArrayOutputStream pbytes = new ByteArrayOutputStream();
			short[] col = pixels[i];
			int STATE = STATE_TRANS;
			int span = 0;

			for (int offs = 0; offs < col.length; offs++)
			{
				switch (STATE)
				{
					case STATE_TRANS:
						if (col[offs] != PIXEL_TRANSLUCENT)
						{
							span = 0;
							columnBytes.write(offs & 0x0ff);
							pbytes = new ByteArrayOutputStream();
							STATE = STATE_COLOR;
							offs--;	// state change. keep index.
						}
						break;
						
					case STATE_COLOR:
						if (col[offs] == PIXEL_TRANSLUCENT)
						{
							columnBytes.write(span & 0x0ff);
							columnBytes.write(0);
							columnBytes.write(pbytes.toByteArray());
							columnBytes.write(0);
							pbytes.reset();
							STATE = STATE_TRANS;
							offs--;	// state change. keep index.
						}
						else
						{
							pbytes.write(col[offs] & 0x0ff);
							span++;
						}
						break;
				}
			}
			
			if (pbytes.size() > 0)
			{
				columnBytes.write(span & 0x0ff);
				columnBytes.write(0);
				columnBytes.write(pbytes.toByteArray());
				columnBytes.write(0);
			}
			columnBytes.write(-1);
			columnOffs += columnBytes.size();
			dataBytes.write(columnBytes.toByteArray());
			columnBytes.reset();
		}
		
		for (int n : columnOffsets)
			sw.writeInt(n);
		
		sw.writeBytes(dataBytes.toByteArray());
	}

	// Reads a column of pixels.
	private byte[] columnRead(SuperReader sr) throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
	
		int offs = 0;
		int span = 0;
	
		offs = (sr.readByte() & 0x0ff);
		while (offs != 255)
		{
			span = (sr.readByte() & 0x0ff);
			sr.readByte();
			
			out.write(offs);
			out.write(span);
			out.write(sr.readBytes(span));
			sr.readByte();
			
			offs = (sr.readByte() & 0x0ff);
		}
		
		return out.toByteArray();
	}
	
	

}
