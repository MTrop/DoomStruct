/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.struct;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import net.mtrop.doom.map.BinaryObject;

import com.blackrook.commons.Common;
import com.blackrook.io.SuperReader;

/**
 * The palette that makes up the Doom Engine's color palette.
 * All colors are stored as java.awt.Colors and are all opaque.
 * This contains an indexed set of 256 colors.
 * TODO: Finish this - add sorting and stuff. 
 * @author Matthew Tropiano
 */
public class Palette implements BinaryObject
{
	/** The number of total colors in a standard Doom palette. */
	public static final int NUM_COLORS = 256;
	/** Number of bytes per color in a Doom palette. */
	public static final int BYTES_PER_COLOR = 3;
	
	/** Comparators for hue. */
	private static final Comparator<Color> HUE_COMPARATOR = new Comparator<Color>()
	{
		// get this color's hue.
		private float getHue(Color c)
		{
			int rgb = c.getRGB();
			int r = (rgb & 0x00FFFFFF) >> 16;
			int g = (rgb & 0x0000FFFF) >> 8;
			int b = (rgb & 0x000000FF);
			
			if (r == g && g == b)
			{
				return 0.0f;
			}
			else if (b < g && b < r)
			{
				g -= b;
				r -= b;
				
				if (g == 0)
					return 0.0f;
				else if (r == 0)
					return 120.0f;
				else if (r < g)
					return 120.0f - ((float)r / g * 60.0f);
				else
					return 60.0f - ((float)g / r * 60.0f);
			}
			else if (r < g && r < b)
			{
				g -= r;
				b -= r;
				
				if (g == 0)
					return 240.0f;
				else if (b == 0)
					return 120.0f;
				else if (g < b)
					return 240.0f - ((float)g / b * 60.0f);
				else
					return 180.0f - ((float)b / g * 60.0f);
			}
			else
			{
				r -= g;
				b -= g;
				
				if (r == 0)
					return 240.0f;
				else if (b == 0)
					return 0.0f;
				else if (b < r)
					return 360.0f - ((float)b / r * 60.0f);
				else
					return 300.0f - ((float)r / b * 60.0f);
			}
		}
		
		@Override
		public int compare(Color o1, Color o2)
		{
			float h1 = getHue(o1);
			float h2 = getHue(o2);
			return h1 < h2 ? -1 : (h1 > h2 ? 1 : 0) ;
		}
	}; 
	
	/** Comparators for saturation. */
	private static final Comparator<Color> SATURATION_COMPARATOR = new Comparator<Color>()
	{
		
		private int getSaturation(Color c)
		{
			int rgb = c.getRGB();
			int r = (rgb & 0x00FFFFFF) >> 16;
			int g = (rgb & 0x0000FFFF) >> 8;
			int b = (rgb & 0x000000FF);
			
			if (r == g && g == b)
				return 0;
			else if (r == 0 || g == 0 || b == 0)
				return 255;
			else if (r == g)
				return Math.abs(b - g); 
			else if (g == b)
				return Math.abs(r - g); 
			else if (r == b)
				return Math.abs(g - r); 
			else if (r < g && g < b)
				return (g - r) + (b - g);
			else if (g < r && r < b)
				return (r - g) + (b - r);
			else // (r < b && b < g)
				return (b - r) + (g - b);
		}
		
		@Override
		public int compare(Color o1, Color o2)
		{
			return getSaturation(o1) - getSaturation(o2);
		}
		
	};

	/** Comparators for brightness. */
	private static final Comparator<Color> BRIGHTNESS_COMPARATOR = new Comparator<Color>()
	{
		
		private float getBrightness(Color c)
		{
			int rgb = c.getRGB();
			int r = (rgb & 0x00FFFFFF) >> 16;
			int g = (rgb & 0x0000FFFF) >> 8;
			int b = (rgb & 0x000000FF);
			
			return 0.2126f * r + 0.7152f * g + 0.0722f * b;
		}
		
		@Override
		public int compare(Color o1, Color o2)
		{
			float h1 = getBrightness(o1);
			float h2 = getBrightness(o2);
			return h1 < h2 ? -1 : (h1 > h2 ? 1 : 0);
		}
		
	};

	
	/** The palette of colors. */
	protected Color[] colorPalette;

	/** Sorted by hue. */
	private Color[] sortedByHue;
	/** Sorted by saturation. */
	private Color[] sortedBySaturated;
	/** Sorted by brightness. */
	private Color[] sortedByBrightness;
	
	/**
	 * Creates a new palette of black, opaque colors.
	 */
	public Palette()
	{
		colorPalette = new Color[NUM_COLORS];
		for (int i = 0; i < NUM_COLORS; i++)
			colorPalette[i] = new Color(0,0,0);
	}
	
	/**
	 * Returns the Color of a specific index in the palette.
	 * @param index	the index number of the color.
	 * @throws ArrayIndexOutOfBoundsException if index > NUM_COLORS or < 0.
	 * @return the color as a java.awt.Color.
	 */
	public Color getColor(int index)
	{
		return colorPalette[index];
	}
	
	/**
	 * Sets the color of a specific index in the Palette.
	 * @param index	the index number of the color.
	 * @param color the new Color.
	 * @throws ArrayIndexOutOfBoundsException if index > NUM_COLORS or < 0.
	 */
	public void setColor(int index, Color color)
	{
		colorPalette[index] = color;
	}
	
	/**
	 * Returns the index of the color nearest to a color in the palette.
	 * TODO: Make this perform better.
	 */
	public int getNearestColorIndex(Color color)
	{
		if (color.getAlpha() < 255)
			return -1;
		
		double dist = Double.MAX_VALUE;
		int out = -1;
		for (int i = 0; i < 256; i++)
		{
			Color c = getColor(i);
			double d = Math.sqrt(
					Math.pow(color.getRed() - c.getRed(), 2) +
					Math.pow(color.getGreen() - c.getGreen(), 2) +
					Math.pow(color.getBlue() - c.getBlue(), 2));
			
			if (d < dist)
			{
				dist = d;
				out = i;
			}
		}
		return out;
	}
	
	/**
	 * Returns an iterator for iterating through this palette's colors. 
	 */
	public Iterator<Color> iterator()
	{
		return Arrays.asList(colorPalette).iterator();
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
		for (int i = 0; i < NUM_COLORS; i++)
			colorPalette[i] = new Color(
				sr.readByte() & 0x0ff,
				sr.readByte() & 0x0ff,
				sr.readByte() & 0x0ff
			); 
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		for (Color c : colorPalette)
		{
			out.write(c.getRed() & 0x0ff);
			out.write(c.getGreen() & 0x0ff);
			out.write(c.getBlue() & 0x0ff);
		}
	}

	@Override
	public String toString()
	{
		return java.util.Arrays.toString(colorPalette);
	}
	
}
