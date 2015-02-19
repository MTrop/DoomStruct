package net.mtrop.doom.util;

import java.awt.image.BufferedImage;

import net.mtrop.doom.graphics.Flat;
import net.mtrop.doom.graphics.Picture;
import net.mtrop.doom.struct.Colormap;
import net.mtrop.doom.struct.Palette;

/**
 * Graphics utility methods for image types.
 * @author Matthew Tropiano
 */
public final class GraphicUtils
{
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
	
}
