/*******************************************************************************
 * Copyright (c) 2015-2019 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import net.mtrop.doom.graphics.Colormap;
import net.mtrop.doom.graphics.EndDoom;
import net.mtrop.doom.graphics.Flat;
import net.mtrop.doom.graphics.Palette;
import net.mtrop.doom.graphics.Picture;

/**
 * Graphics utility methods for image types.
 * @author Matthew Tropiano
 */
public final class GraphicUtils
{
	/** ANSI color table. */
	public static final Color[] ANSI_COLORS = {
		new Color(0,0,0),		//black
		new Color(0,0,171),		//blue
		new Color(0,171,0),		//green
		new Color(0,153,153),	//cyan
		new Color(171,0,0),		//red
		new Color(153,0,153), 	//magenta
		new Color(153,102,0),	//brown
		new Color(171,171,171),	//light gray
		new Color(84,84,84),	//dark gray
		new Color(102,102,255),	//light blue
		new Color(102,255,102),	//light green
		new Color(102,255,255),	//light cyan
		new Color(255,102,102),	//light red
		new Color(255,102,255),	//light magenta
		new Color(255,255,102),	//yellow
		new Color(255,255,255)	//white
	};

	private GraphicUtils()
	{
	}
	
	/**
	 * Creates a {@link Flat} from a {@link BufferedImage}.
	 * Colors are approximated using the provided {@link Palette}, and translated using the provided {@link Colormap}.
	 * Pixels that are not opaque are considered black. 
	 * @param image the image to convert.
	 * @param palette the palette to use for color approximation.
	 * @return the resultant Flat.
	 */
	public static Flat createFlat(BufferedImage image, Palette palette)
	{
		return createFlat(image, palette, null);
	}

	/**
	 * Creates a {@link Flat} from a {@link BufferedImage}.
	 * Colors are approximated using the provided {@link Palette}, and translated using the provided {@link Colormap}.
	 * Pixels that are not opaque are considered black. 
	 * @param image the image to convert.
	 * @param palette the palette to use for color approximation.
	 * @param colormap the colormap to use for palette translation. Can be <code>null</code> for no translation.
	 * @return the resultant Flat.
	 */
	public static Flat createFlat(BufferedImage image, Palette palette, Colormap colormap)
	{
		
		Flat out = new Flat(image.getWidth(), image.getHeight());
		for (int y = 0; y < out.getHeight(); y++)
			for (int x = 0; x < out.getWidth(); x++)
			{
				int argb = image.getRGB(x, y);
				if ((argb & 0xff000000) >> 24 != 0xff000000)
					argb = 0;
				int index = palette.getNearestColorIndex((argb & 0x00ff0000) >> 16, (argb & 0x0000ff00) >> 8, (argb & 0x000000ff));
				index = colormap != null ? colormap.getPaletteIndex(index) : index;
				out.setPixel(x, y, index);
			}
		
		return out;
	}
	
	/**
	 * Creates a {@link Picture} from a {@link BufferedImage}.
	 * Colors are approximated using the provided {@link Palette}, and translated using the provided {@link Colormap}.
	 * Pixels that are not opaque are considered blank. 
	 * @param image the image to convert.
	 * @param palette the palette to use for color approximation.
	 * @return the resultant Picture.
	 */
	public static Picture createPicture(BufferedImage image, Palette palette)
	{
		return createPicture(image, palette, null);
	}

	/**
	 * Creates a {@link Picture} from a {@link BufferedImage}.
	 * Colors are approximated using the provided {@link Palette}, and translated using the provided {@link Colormap}.
	 * Pixels that are not opaque are considered blank. 
	 * @param image the image to convert.
	 * @param palette the palette to use for color approximation.
	 * @param colormap the colormap to use for palette translation. Can be <code>null</code> for no translation.
	 * @return the resultant Picture.
	 */
	public static Picture createPicture(BufferedImage image, Palette palette, Colormap colormap)
	{
		
		Picture out = new Picture(image.getWidth(), image.getHeight());
		for (int y = 0; y < out.getHeight(); y++)
			for (int x = 0; x < out.getWidth(); x++)
			{
				int argb = image.getRGB(x, y);
				if ((argb & 0xff000000) >> 24 != 0xff000000)
					argb = 0;
				int index = palette.getNearestColorIndex((argb & 0x00ff0000) >> 16, (argb & 0x0000ff00) >> 8, (argb & 0x000000ff));
				index = colormap != null ? colormap.getPaletteIndex(index) : index;
				out.setPixel(x, y, index);
			}
		
		return out;
	}
	
	/**
	 * Creates a {@link BufferedImage} from a {@link Flat}.
	 * @param flat the Flat to convert.
	 * @param palette the palette to use as a color source.
	 * @return a full color image of the indexed-color Flat. 
	 */
	public static BufferedImage createImage(Flat flat, Palette palette)
	{
		return createImage(flat, palette, null);
	}

	/**
	 * Creates a {@link BufferedImage} from a {@link Flat}.
	 * @param flat the Flat to convert.
	 * @param palette the palette to use as a color source.
	 * @param colormap the colormap for palette translation, if any. Can be null for no translation.
	 * @return a full color image of the indexed-color Flat. 
	 */
	public static BufferedImage createImage(Flat flat, Palette palette, Colormap colormap)
	{
		BufferedImage out = new BufferedImage(flat.getWidth(), flat.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < out.getHeight(); y++)
			for (int x = 0; x < out.getWidth(); x++)
			{
				int index = colormap != null ? colormap.getPaletteIndex(flat.getPixel(x, y)) : flat.getPixel(x, y);
				int argb = palette.getColorARGB(index);
				out.setRGB(x, y, argb);
			}
		
		return out;
	}
	
	/**
	 * Creates a {@link BufferedImage} from a {@link Picture}.
	 * @param picture the Picture to convert.
	 * @param palette the palette to use as a color source.
	 * @return a full color image of the indexed-color Flat. 
	 */
	public static BufferedImage createImage(Picture picture, Palette palette)
	{
		return createImage(picture, palette, null);
	}

	/**
	 * Creates a {@link BufferedImage} from a {@link Flat}.
	 * @param picture the Picture to convert.
	 * @param palette the palette to use as a color source.
	 * @param colormap the colormap for palette translation, if any. Can be null for no translation.
	 * @return a full color image of the indexed-color Flat. 
	 */
	public static BufferedImage createImage(Picture picture, Palette palette, Colormap colormap)
	{
		BufferedImage out = new BufferedImage(picture.getWidth(), picture.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < out.getHeight(); y++)
			for (int x = 0; x < out.getWidth(); x++)
			{
				int index = picture.getPixel(x, y);
				if (index < 0)
					out.setRGB(x, y, 0);
				else
				{
					index = colormap != null ? colormap.getPaletteIndex(picture.getPixel(x, y)) : picture.getPixel(x, y);
					int argb = palette.getColorARGB(index);
					out.setRGB(x, y, argb);
				}
			}
		
		return out;
	}

	/**
	 * Returns the EndDoom data rendered to a BufferedImage.
	 * @param endoom the EndDoom lump to render.
	 * @param blinking if true, this will render the "blinking" characters.
	 * @return a BufferedImage that represents the graphic image in RGB color (including transparency).
	 * @throws NullPointerException if endoom is null.
	 */
	public static BufferedImage createImageForEndDoom(EndDoom endoom, boolean blinking)
	{
		BufferedImage out = new BufferedImage(640, 300, BufferedImage.TYPE_INT_ARGB);
		Font font = new Font("Lucida Console", Font.PLAIN, 13);
		char[] ch = new char[1];
		Graphics2D g = (Graphics2D)out.getGraphics();
		g.setFont(font);
		g.setColor(ANSI_COLORS[0]);
		g.fillRect(0, 0, 640, 300);
		
		for (int r = 0; r < 25; r++)
			for (int c = 0; c < 80; c++)
			{
				g.setColor(ANSI_COLORS[endoom.getBackgroundColor(r, c)]);
				g.fillRect(c*8, r*12, 8, 12);
			}
		
		for (int r = 24; r >= 0; r--)
			for (int c = 79; c >= 0; c--)
			{
				if (blinking || (!blinking && endoom.getBlinking(r, c)))
				{
					g.setColor(ANSI_COLORS[endoom.getForegroundColor(r, c)]);
					ch[0] = endoom.getCharAt(r, c);
					g.drawChars(ch, 0, 1, c*8, r*12+10);
				}
			}
		return out;
	}
		
}
