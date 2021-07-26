/*******************************************************************************
 * Copyright (c) 2015-2020 Matt Tropiano
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.graphics;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.TreeMap;

import net.mtrop.doom.object.BinaryObject;
import net.mtrop.doom.object.GraphicObject;
import net.mtrop.doom.struct.io.SerialReader;
import net.mtrop.doom.struct.io.SerialWriter;
import net.mtrop.doom.util.MathUtils;
import net.mtrop.doom.util.RangeUtils;

/**
 * Doom graphic data stored as column-major indices (patches and most graphics with baked-in offsets). 
 * Useful for editing/displaying graphics.
 * @author Matthew Tropiano
 */
public class Picture implements BinaryObject, GraphicObject, IndexedGraphic
{
	public static final byte PIXEL_TRANSLUCENT = -1;
	
	/** The pixel data. */
	private byte[][] pixels; 
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
	 * @throws IllegalArgumentException if width is &lt; 1 or &gt; 256, or height is &lt; 1 or &gt; 65535.
	 */
	public Picture(int width, int height)
	{
		this.offsetX = 0;
		this.offsetY = 0;
		setDimensions(width, height);
	}

	/**
	 * Sets the dimensions of this picture.
	 * WARNING: This will clear all of the data in the picture.
	 * @param width	the width of the picture in pixels.
	 * @param height the height of the picture in pixels.
	 * @throws IllegalArgumentException if width is &lt; 1 or &gt; 256, or height is &lt; 1 or &gt; 65535.
	 */
	public void setDimensions(int width, int height)
	{
		if (width < 1 || height < 1)
			throw new IllegalArgumentException("Width or height cannot be less than 1.");
		if (width > 65535)
			throw new IllegalArgumentException("Width cannot be greater than 65535.");
		if (height > 65535)
			throw new IllegalArgumentException("Height cannot be greater than 65535.");
		
		pixels = new byte[width][height];
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
	 * Valid values are in the range of -1 to 255, with 0 to 254 being palette indexes and -1 / 255 being translucent pixels.
	 * Note that palette value 255 does not get used as a color! 
	 * @param x	picture x-coordinate.
	 * @param y	picture y-coordinate.
	 * @param value	the value to set.
	 * @throws IllegalArgumentException if the value is outside the range -1 to 255.
	 * @throws ArrayIndexOutOfBoundsException if the provided coordinates is outside the graphic.
	 */
	public void setPixel(int x, int y, int value)
	{
		RangeUtils.checkRange("Pixel ("+x+", "+y+")", -1, 255, value);
		pixels[x][y] = (byte)MathUtils.clampValue(value, -1, 255);
	}
	
	/**
	 * Gets the pixel data at a location in the picture.
	 * @param x	picture x-coordinate.
	 * @param y	picture y-coordinate.
	 * @return a palette index value from 0 to 254 or {@link #PIXEL_TRANSLUCENT} if the pixel is not filled in.
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
			offMax = i > offMax ? i : offMax;
		
		// precache columns at each particular offset: picture may be compressed.
		Map<Integer, byte[]> columnData = new TreeMap<Integer, byte[]>();

		for (int i = 0; i < columnOffsets.length; i++)
			columnData.put(columnOffsets[i], columnRead(sr, in));
			
		for (int x = 0; x < columnOffsets.length; x++)
		{
			int y = 0;
			byte[] b = columnData.get(columnOffsets[x]);
			for (int i = 0; i < b.length; i++)
			{
				y = b[i++] & 0x0ff;
				int span = b[i++] & 0x0ff;
				for (int j = 0; j < span; j++)
					pixels[x][y+j] = (byte)(b[i+j] & 0x0ff);
				i += span-1;
			}
		}
				
	}

	// Reads a column of pixels.
	private static byte[] columnRead(SerialReader sr, InputStream in) throws IOException
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

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		SerialWriter sw = new SerialWriter(SerialWriter.LITTLE_ENDIAN);
		sw.writeUnsignedShort(out, pixels.length);
		sw.writeUnsignedShort(out, pixels[0].length);
		sw.writeShort(out, (short)offsetX);
		sw.writeShort(out, (short)offsetY);
		
		int[] columnOffsets = new int[getWidth()];

		int columnOffs = 8 + (4 * columnOffsets.length);
		ByteArrayOutputStream dataBytes = new ByteArrayOutputStream();
		ByteArrayOutputStream columnBytes = new ByteArrayOutputStream();
		
		for (int i = 0; i < columnOffsets.length; i++)
		{
			columnOffsets[i] = columnOffs;
			writeColumn(pixels[i], columnBytes);
			columnBytes.writeTo(dataBytes);
			columnOffs += columnBytes.size();
			columnBytes.reset();
		}
		
		for (int n : columnOffsets)
			sw.writeInt(out, n);
		
		sw.writeBytes(out, dataBytes.toByteArray());
	}
	
	// Writes a column of pixels.
	// FIXME: Transparency is broken.
	private static void writeColumn(byte[] columnPixels, ByteArrayOutputStream buffer) throws IOException
	{
		int topDelta = 0;
		ByteArrayOutputStream postBytes = new ByteArrayOutputStream();

		final int STATE_TRANSPARENT = 0;
		final int STATE_OPAQUE = 1;
		int state = STATE_TRANSPARENT;
		
		for (int i = 0; i < columnPixels.length; i++)
		{
			byte b = columnPixels[i];
			switch (state)
			{
				case STATE_TRANSPARENT:
				{
					if (topDelta >= 254)
					{
						// should be empty. Write empty post to set up "tall patch" workaround.
						writePost(254, postBytes, buffer);
						topDelta = 0;
					}
					else if (b != PIXEL_TRANSLUCENT)
					{
						postBytes.write(b);
						state = STATE_OPAQUE;
					} 
					else
					{
						topDelta++;
					}
				}
				break;
				
				case STATE_OPAQUE:
				{
					if (b == PIXEL_TRANSLUCENT || postBytes.size() == 254)
					{
						writePost(topDelta, postBytes, buffer);
						topDelta = (postBytes.size() + topDelta) % 255;
						postBytes.reset();
						
						if (topDelta == 254)
						{
							writePost(254, postBytes, buffer);
							topDelta = 0;
						}

						if (b == PIXEL_TRANSLUCENT)
						{
							topDelta++;
							state = STATE_TRANSPARENT;
						}
						else
						{
							postBytes.write(b);
						}
					}
					else
					{
						postBytes.write(b);
					}
				}
				break;
			}
		}
		
		// flush remaining
		if (state == STATE_OPAQUE)
		{
			writePost(topDelta, postBytes, buffer);
		}

		buffer.write(0xff); // terminal topDelta
	}
	
	// Writes a post of pixels to an output buffer.
	private static void writePost(int topDelta, ByteArrayOutputStream postBytes, OutputStream out) throws IOException
	{
		out.write(topDelta);
		out.write(postBytes.size());
		out.write(0);
		postBytes.writeTo(out);
		out.write(0);
	}
	
}
