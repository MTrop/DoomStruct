/*******************************************************************************
 * Copyright (c) 2015-2020 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.graphics;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import net.mtrop.doom.object.BinaryObject;
import net.mtrop.doom.object.GraphicObject;
import net.mtrop.doom.struct.io.SerialReader;
import net.mtrop.doom.struct.io.SerialWriter;
import net.mtrop.doom.util.RangeUtils;

/**
 * Doom graphic data stored as column-major indices (patches and most graphics with baked-in offsets). 
 * Useful for editing/displaying graphics.
 * @author Matthew Tropiano
 */
public class Picture implements BinaryObject, GraphicObject, IndexedGraphic
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
	 * Creates a new picture.
	 * @param width		the width of the picture in pixels.
	 * @param height	the height of the picture in pixels.
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
	 * Sets the dimensions of this picture.
	 * WARNING: This will clear all of the data in the picture.
	 * @param width	the width of the picture in pixels.
	 * @param height the height of the picture in pixels.
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
	 * Sets the pixel data at a location in the picture.
	 * Valid values are in the range of -1 to 255, with
	 * 0 to 255 being palette indexes and -1 being translucent
	 * pixel information. Values outside this range are CLAMPED into the
	 * range.
	 * @param x	picture x-coordinate.
	 * @param y	picture y-coordinate.
	 * @param value	the value to set.
	 * @throws IllegalArgumentException if the offset is outside the range -1 to 255.
	 * @throws ArrayIndexOutOfBoundsException if the provided coordinates is outside the graphic.
	 */
	public void setPixel(int x, int y, int value)
	{
		RangeUtils.checkRange("Pixel ("+x+", "+y+")", -1, 255, value);
		pixels[x][y] = (short)RangeUtils.clampValue(value, -1, 255);
	}
	
	/**
	 * Gets the pixel data at a location in the picture.
	 * @param x	picture x-coordinate.
	 * @param y	picture y-coordinate.
	 * @return a palette index value from 0 to 255 or PIXEL_TRANSLUCENT if the pixel is not filled in.
	 * @throws ArrayIndexOutOfBoundsException if the provided coordinates is outside the graphic.
	 */
	public int getPixel(int x, int y)
	{
		return pixels[x][y];
	}
	
	@Override
	public void readBytes(InputStream in) throws IOException
	{
		SerialReader sr = new SerialReader(SerialReader.LITTLE_ENDIAN);
		setDimensions(sr.readUnsignedShort(in), sr.readUnsignedShort(in));
		offsetX = sr.readShort(in);
		offsetY = sr.readShort(in);

		// load offset table.
		int[] columnOffsets = sr.readInts(in, getWidth());
		
		// data must be treated as a stream: find highest short offset so that the reading can stop.
		int offMax = -1;
		for (int i : columnOffsets)
		{
			offMax = i > offMax ? i : offMax;
		}
		
		// precache columns at each particular offset: picture may be compressed.
		HashMap<Integer, byte[]> columnData = new HashMap<Integer, byte[]>();

		for (int i = 0; i < columnOffsets.length; i++)
			columnData.put(columnOffsets[i],columnRead(sr, in));
			
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
		SerialWriter sw = new SerialWriter(SerialWriter.LITTLE_ENDIAN);
		sw.writeUnsignedShort(out, pixels.length);
		sw.writeUnsignedShort(out, pixels[0].length);
		sw.writeShort(out, (short)offsetX);
		sw.writeShort(out, (short)offsetY);
		
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
			sw.writeInt(out, n);
		
		sw.writeBytes(out, dataBytes.toByteArray());
	}

	// Reads a column of pixels.
	private byte[] columnRead(SerialReader sr, InputStream in) throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
	
		int offs = 0;
		int span = 0;
	
		offs = (sr.readByte(in) & 0x0ff);
		while (offs != 255)
		{
			span = (sr.readByte(in) & 0x0ff);
			sr.readByte(in);
			
			out.write(offs);
			out.write(span);
			out.write(sr.readBytes(in, span));
			sr.readByte(in);
			
			offs = (sr.readByte(in) & 0x0ff);
		}
		
		return out.toByteArray();
	}
	
	

}
