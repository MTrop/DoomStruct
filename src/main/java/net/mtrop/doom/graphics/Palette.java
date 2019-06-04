/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.graphics;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;

import net.mtrop.doom.BinaryObject;

/**
 * The palette that makes up the Doom Engine's color palette.
 * All colors are all opaque. This contains an indexed set of 256 colors.
 * @author Matthew Tropiano
 */
public class Palette implements BinaryObject
{
	/** The number of total colors in a standard Doom palette. */
	public static final int NUM_COLORS = 256;
	/** Number of bytes per color in a Doom palette. */
	public static final int BYTES_PER_COLOR = 3;
	
	/** Comparators for hue. */
	private static final Comparator<byte[]> PALETTE_COMPARATOR = new Comparator<byte[]>()
	{
		// get this color's hue.
		private float getHue(byte[] color)
		{
			int r = color[0] & 0x0ff;
			int g = color[1] & 0x0ff;
			int b = color[2] & 0x0ff;
			
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
		
		private int getSaturation(byte[] color)
		{
			int r = color[0] & 0x0ff;
			int g = color[1] & 0x0ff;
			int b = color[2] & 0x0ff;
			
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
		
		private float getLuminance(byte[] color)
		{
			float r = (color[0] & 0x0ff) / 255f;
			float g = (color[1] & 0x0ff) / 255f;
			float b = (color[2] & 0x0ff) / 255f;
			return 0.2126f * r + 0.7152f * g + 0.0722f * b;
		}
		
		@Override
		public int compare(byte[] c1, byte[] c2)
		{
			float h1, h2, s1, s2, b1, b2;
			
			return (h1 = getHue(c1)) == (h2 = getHue(c2)) 
				? (s1 = getSaturation(c1)) == (s2 = getSaturation(c2))
				? (b1 = getLuminance(c1)) == (b2 = getLuminance(c2))
				? 0 
				: (b1 < b2 ? -1 : 1)
				: (s1 < s2 ? -1 : 1)
				: (h1 < h2 ? -1 : 1)
				;
		}
	}; 

	/** Comparators for index. */
	private Comparator<Integer> paletteIndexComparator = new Comparator<Integer>()
	{
		public int compare(Integer o1, Integer o2)
		{
			return PALETTE_COMPARATOR.compare(colorPalette[o1], colorPalette[o2]);
		}
	};
	
	/** The palette of colors. */
	protected byte[][] colorPalette;
	/** The color sort. */
	protected Integer[] colorSort;
	
	/**
	 * Creates a new palette of black, opaque colors.
	 */
	public Palette()
	{
		colorPalette = new byte[NUM_COLORS][3];
		colorSort = new Integer[NUM_COLORS];
		for (int i = 0; i < NUM_COLORS; i++)
		{
			colorSort[i] = i;
		}
	}
	
	/**
	 * Returns the Color of a specific index in the palette.
	 * @param index	the index number of the color.
	 * @throws ArrayIndexOutOfBoundsException if index is greater than or equal to NUM_COLORS or less than 0.
	 * @return the color as a java.awt.Color.
	 */
	public Color getColor(int index)
	{
		byte[] c = colorPalette[index];
		return new Color(c[0], c[1], c[2]);
	}
	
	/**
	 * Returns the Color of a specific index in the palette as a 32-bit ARGB integer.
	 * Alpha is always 255 (opaque).
	 * @param index	the index number of the color.
	 * @throws ArrayIndexOutOfBoundsException if index is greater than or equal to NUM_COLORS or less than 0.
	 * @return the color as an ARGB integer.
	 */
	public int getColorARGB(int index)
	{
		byte[] c = colorPalette[index];
		return 0xff000000 | ((c[0] & 0x0ff) << 16) | ((c[1] & 0x0ff) << 8) | (c[2] & 0x0ff);
	}
	
	/**
	 * Sets the color of a specific index in the Palette.
	 * @param index	the index number of the color.
	 * @param red the red component amount (0 to 255).
	 * @param green the green component amount (0 to 255).
	 * @param blue the blue component amount (0 to 255).
	 * @throws ArrayIndexOutOfBoundsException if index is greater than or equal to NUM_COLORS or less than 0.
	 */
	public void setColor(int index, int red, int green, int blue)
	{
		setColorNoSort(index, red, green, blue);
		sortIndices();
	}

	/**
	 * Returns the index of the color nearest to a color in the palette,
	 * or -1 if no color is appropriately matchable.
	 * @param red the red component amount (0 to 255).
	 * @param green the green component amount (0 to 255).
	 * @param blue the blue component amount (0 to 255).
	 * @return the closest index.
	 */
	public int getNearestColorIndex(int red, int green, int blue)
	{
		byte[] cbyte = new byte[3];
		int u = NUM_COLORS, l = 0;
		int i = (u+l)/2;
		int prev = u;
		
		while (true)
		{
			cbyte[0] = (byte)red;
			cbyte[1] = (byte)green;
			cbyte[2] = (byte)blue;
			
			if (Arrays.equals(colorPalette[colorSort[i]], cbyte))
				return i;
			
			int c = PALETTE_COMPARATOR.compare(colorPalette[colorSort[i]], cbyte); 
			
			if (c < 0)
				l = i;
			else if (c == 0)
				return i;
			else
				u = i;
			
			if (i == (u+l)/2)
				break;
			
			prev = i;
			i = (u+l)/2;
		}
		
		double d1 = getColorDistance(cbyte, colorPalette[colorSort[i]]);
		double d2 = getColorDistance(cbyte, colorPalette[colorSort[prev]]);
		
		if (d1 < d2)
			return colorSort[i];
		else
			return colorSort[prev];
	}
	
	private double getColorDistance(byte[] color1, byte[] color2)
	{
		int dr = color1[0] - color2[0];
		int dg = color1[1] - color2[1];
		int db = color1[2] - color2[2];
		return dr * dr + dg * dg + db * db;
	}
	
	/**
	 * Sets the color of a specific index in the Palette and doesn't trigger a re-sort.
	 * @param index	the index number of the color.
	 * @param red the red component amount (0 to 255).
	 * @param green the green component amount (0 to 255).
	 * @param blue the blue component amount (0 to 255).
	 * @throws ArrayIndexOutOfBoundsException if index is greater than or equal to NUM_COLORS or less than 0.
	 */
	protected void setColorNoSort(int index, int red, int green, int blue)
	{
		colorPalette[index][0] = (byte)red;
		colorPalette[index][1] = (byte)green;
		colorPalette[index][2] = (byte)blue;
	}

	/**
	 * Sort indexes into color.
	 */
	protected void sortIndices()
	{
		Arrays.sort(colorSort, paletteIndexComparator);
	}

	@Override
	public void readBytes(InputStream in) throws IOException
	{
		for (int i = 0; i < NUM_COLORS; i++)
		{
			int r = in.read();
			int g = in.read();
			int b = in.read();
			if (r == -1 || g == -1 || b == -1)
				throw new IOException("end of stream reached in color index "+i);
			setColorNoSort(i,
				r & 0x0ff,
				g & 0x0ff,
				b & 0x0ff
			);
		}
		sortIndices();
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		for (byte[] c : colorPalette)
			out.write(c);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Palette");
		for (int i = 0; i < NUM_COLORS; i++)
		{
			byte[] c = colorPalette[i];
			sb.append(' ').append(i).append(":").append(Arrays.toString(c));
			if (i < NUM_COLORS - 1)
				sb.append(", ");
		}
		return sb.toString();
	}
	
}
