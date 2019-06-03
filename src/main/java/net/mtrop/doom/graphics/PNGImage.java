/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.graphics;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import net.mtrop.doom.BinaryObject;
import net.mtrop.doom.GraphicObject;

import com.blackrook.commons.Common;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;
import com.blackrook.io.container.PNGContainerReader;
import com.blackrook.io.container.PNGContainerWriter;

/**
 * Represents PNG-formatted data.
 * The export functions write this data back as PNG.
 * @author Matthew Tropiano
 */
public class PNGImage extends BufferedImage implements BinaryObject, GraphicObject
{
	private static final String PNG_OFFSET_CHUNK = "grAb";
	
	/** The offset from the center, horizontally, in pixels. */
	private int offsetX; 
	/** The offset from the center, vertically, in pixels. */
	private int offsetY; 

	/**
	 * Creates a new image with dimensions (1, 1).
	 */
	public PNGImage()
	{
		this(1, 1);
	}

	/**
	 * Creates a new PNG data image.
	 * @param width	the width of the patch in pixels.
	 * @param height the height of the patch in pixels.
	 */
	public PNGImage(int width, int height)
	{
		super(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * Creates a new PNG data image from another image.
	 * @param image	the source image.
	 */
	public PNGImage(BufferedImage image)
	{
		this(image.getWidth(), image.getHeight());
		setImage(image);
	}

	/**
	 * Reads and creates a new PNGImage object from an array of bytes.
	 * This reads until it reaches the end of the picture data.
	 * @param bytes the byte array to read.
	 * @return a new PNGImage from the data.
	 * @throws IOException if the stream cannot be read.
	 */
	public static PNGImage create(byte[] bytes) throws IOException
	{
		PNGImage out = new PNGImage();
		out.fromBytes(bytes);
		return out;
	}

	/**
	 * Reads and creates a new PNGImage from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for the full {@link PNGImage} are read.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @return a new PNGImage from the data.
	 * @throws IOException if the stream cannot be read.
	 */
	public static PNGImage read(InputStream in) throws IOException
	{
		PNGImage out = new PNGImage();
		out.readBytes(in);
		return out;
	}

	@Override
	public int getOffsetX()
	{
		return offsetX;
	}

	/**
	 * Sets the offset from the center, horizontally, in pixels.
	 * @param offsetX the new X offset.
	 */
	public void setOffsetX(int offsetX)
	{
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
	 */
	public void setOffsetY(int offsetY)
	{
		this.offsetY = offsetY;
	}

	/**
	 * Sets the pixel data for this graphic using an Image.
	 * If the source image is scaled to this image's dimensions.
	 * @param image the image to copy from. 
	 */
	public void setImage(BufferedImage image)
	{
		Graphics2D g2d = (Graphics2D)this.getGraphics();
		g2d.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		g2d.dispose();
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
		byte[] b = Common.getBinaryContents(in);
		PNGContainerReader pr = new PNGContainerReader(new ByteArrayInputStream(b));
		PNGContainerReader.Chunk cin = null;
		while ((cin = pr.nextChunk()) != null)
		{
			if (cin.getName().equals(PNG_OFFSET_CHUNK))
			{
				SuperReader sr = new SuperReader(new ByteArrayInputStream(cin.getData()), SuperReader.BIG_ENDIAN);
				setOffsetX(sr.readInt());
				setOffsetY(sr.readInt());
				break;
			}
		}

		setImage(ImageIO.read(new ByteArrayInputStream(b)));
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		ByteArrayOutputStream ibos = new ByteArrayOutputStream();
		ImageIO.write(this, "PNG", ibos);
		ByteArrayInputStream ibis = new ByteArrayInputStream(ibos.toByteArray());
		PNGContainerReader pr = new PNGContainerReader(ibis);
		PNGContainerReader.Chunk cin = null;

		PNGContainerWriter pw = new PNGContainerWriter(out);

		cin = pr.nextChunk(); // IHDR
		pw.writeChunk(cin.getName(), cin.getData());
		
		ByteArrayOutputStream obos = new ByteArrayOutputStream();
		obos.write(SuperWriter.intToBytes(getOffsetX(), SuperWriter.BIG_ENDIAN));
		obos.write(SuperWriter.intToBytes(getOffsetY(), SuperWriter.BIG_ENDIAN));
		pw.writeChunk(PNG_OFFSET_CHUNK, obos.toByteArray());
		
		while ((cin = pr.nextChunk()) != null)
			pw.writeChunk(cin.getName(), cin.getData());
	}
	
	

}
