/*******************************************************************************
 * Copyright (c) 2015 Matt Tropiano
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
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * <p>
 * Doom graphic data that has no header data for its dimensions/offsets.
 * </p>
 * <p>
 * Normally, flats are the floor/ceiling textures in the Doom engine that are
 * a set size (64x64) and thus have no need for header information, but fullscreen
 * pictures like Doom's TITLEPIC are also a straight mapping of pixels with assumed
 * dimensions (in this case, 320x200). This class can read both, and its dimensions can
 * be arbitrarily set by the programmer regardless of the amount of data inside.
 * </p>
 * NOTE: The {@link Flat#readBytes(InputStream)} method will only read as many bytes as possible to fill the
 * current dimensions of the flat, as this information is not found in the byte data.
 * @author Matthew Tropiano
 */
public class Flat implements BinaryObject, GraphicObject
{
	public static final short PIXEL_TRANSLUCENT = 0;
	
	/** This flat's width. */
	private int width;
	/** This flat's height. */
	private int height;
	/** The pixel data. */
	private byte[] pixels;
	
	/**
	 * Creates a new flat with dimensions (1, 1).
	 */
	public Flat()
	{
		this(1, 1);
	}
	
	/**
	 * Creates a new flat.
	 * @param width	the width of the flat in pixels. Must be greater than 1.
	 * @param height the height of the flat in pixels. Must be greater than 1.
	 */
	public Flat(int width, int height)
	{
		if (width < 1 || height < 1)
			throw new IllegalArgumentException("Width or height cannot be less than 1.");
		setDimensions(width, height);
	}

	/**
	 * Reads and creates a new Flat object from an array of bytes.
	 * This reads until it reaches the end of the entry list.
	 * @param width	the width of the flat in pixels. Must be greater than 1.
	 * @param height the height of the flat in pixels. Must be greater than 1.
	 * @param bytes the byte array to read.
	 * @return a new Switches object.
	 * @throws IOException if the stream cannot be read.
	 */
	public static Flat create(int width, int height, byte[] bytes) throws IOException
	{
		Flat out = new Flat(width, height);
		out.fromBytes(bytes);
		return out;
	}
	
	/**
	 * Reads and creates a new Flat from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for the full {@link Flat} are read.
	 * The stream is NOT closed at the end.
	 * @param width	the width of the flat in pixels. Must be greater than 1.
	 * @param height the height of the flat in pixels. Must be greater than 1.
	 * @param in the open {@link InputStream} to read from.
	 * @return a new Flat with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static Flat read(int width, int height, InputStream in) throws IOException
	{
		Flat out = new Flat(width, height);
		out.readBytes(in);
		return out;
	}
	
	@Override
	public int getOffsetX()
	{
		return 0;
	}

	@Override
	public int getOffsetY()
	{
		return 0;
	}

	/**
	 * Returns the width of this graphic in pixels.
	 */
	public int getWidth()
	{
		return width;
	}
	
	/**
	 * Returns the height of this graphic in pixels.
	 */
	public int getHeight()
	{
		return height;
	}
	
	/**
	 * Clears the pixel data.
	 */
	public void clear()
	{
		setDimensions(width, height);
	}

	/**
	 * Sets the dimensions of this flat.
	 * WARNING: This will clear all of the data in the patch.
	 * @param width	the width of the flat in pixels.
	 * @param height the height of the flat in pixels.
	 */
	public void setDimensions(int width, int height)
	{
		this.width = width;
		this.height = height;
		pixels = new byte[width*height];
	}

	/**
	 * Sets the pixel data at a location in the flat.
	 * Valid values are in the range of 0 to 255, with 0 to 255 being palette indexes.
	 * @param x	patch x-coordinate.
	 * @param y	patch y-coordinate.
	 * @param value	the value to set.
	 * @throws IllegalArgumentException if the value of the pixel is outside the range 0 to 255.
	 */
	public void setPixel(int x, int y, int value)
	{
		RangeUtils.checkByte("Pixel ("+x+", "+y+")", value);
		pixels[y*width + x] = (byte)value;
	}
	
	/**
	 * Gets the pixel data at a location in the flat.
	 * Returns a palette index value from 0 to 255 or PIXEL_TRANSLUCENT if the pixel is not filled in.
	 * @param x	patch x-coordinate.
	 * @param y	patch y-coordinate.
	 */
	public int getPixel(int x, int y)
	{
		return pixels[y*width + x] & 0x0ff;
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
		for (int i = 0; i < width*height; i++)
			pixels[i] = sr.readByte();
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);
		for (byte pixel : pixels)
			sw.writeByte(pixel);
	}

}
