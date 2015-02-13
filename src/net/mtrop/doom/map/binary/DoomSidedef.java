package net.mtrop.doom.map.binary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.blackrook.commons.Common;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

import net.mtrop.doom.exception.DataConversionException;
import net.mtrop.doom.exception.DataExportException;
import net.mtrop.doom.map.BinaryMapObject;
import net.mtrop.doom.map.Sidedef;
import net.mtrop.doom.util.NameUtils;
import net.mtrop.doom.util.RangeUtils;

/**
 * Doom/Boom 30-byte format implementation of Sidedef.
 * @author Matthew Tropiano
 */
public class DoomSidedef implements BinaryMapObject, Sidedef 
{
	
	/** Sidedef X Offset. */
	private int offsetX;
	/** Sidedef Y Offset. */
	private int offsetY;
	/** Sidedef top texture. */
	private String textureTop;
	/** Sidedef bottom texture. */
	private String textureBottom;
	/** Sidedef middle texture. */
	private String textureMiddle;
	/** Sidedef's sector reference. */
	private int sectorIndex;
	
	/**
	 * Creates a new sidedef with default values set.
	 * @see #reset()
	 */
	public DoomSidedef()
	{
		reset();
	}
	
	/**
	 * Reads and creates a new DoomSidedef from an array of bytes.
	 * This reads from the first 30 bytes of the stream.
	 * The stream is NOT closed at the end.
	 * @param bytes the byte array to read.
	 * @return a new DoomSidedef with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static DoomSidedef create(byte[] bytes) throws IOException
	{
		DoomSidedef out = new DoomSidedef();
		out.fromBytes(bytes);
		return out;
	}
	
	/**
	 * Reads and creates a new DoomSidedef from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for a {@link DoomSidedef} are read.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @return a new DoomSidedef with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static DoomSidedef read(InputStream in) throws IOException
	{
		DoomSidedef out = new DoomSidedef();
		out.readBytes(in);
		return out;
	}
	
	/**
	 * Resets this sidedef's properties to defaults.
	 * <ul>
	 * <li>All offsets are set to 0.</li>
	 * <li>All textures are set to {@link Sidedef#TEXTURE_BLANK}.</li>
	 * <li>The sector index is 0.</li>
	 * </ul>
	 */
	public void reset()
	{
		offsetX = 0;
		offsetY = 0;
		textureTop = TEXTURE_BLANK;
		textureBottom = TEXTURE_BLANK;
		textureMiddle = TEXTURE_BLANK;
		sectorIndex = NULL_REFERENCE;
	}
	
	/**
	 * Sets the sidedef's texture X offset.
	 */
	public void setOffsetX(int offsetX)
	{
		this.offsetX = offsetX;
	}
	
	@Override
	public int getOffsetX()
	{
		return offsetX;
	}

	/**
	 * Sets the sidedef's texture Y offset.
	 */
	public void setOffsetY(int offsetY)
	{
		this.offsetY = offsetY;
	}
	
	@Override
	public int getOffsetY()
	{
		return offsetY;
	}

	/**
	 * Sets the top texture name.
	 */
	public void setTextureTop(String textureTop)
	{
		if (!NameUtils.isValidTextureName(textureTop))
			throw new DataConversionException("Texture name is invalid.");
		this.textureTop = textureTop;
	}
	
	@Override
	public String getTextureTop()
	{
		return textureTop;
	}

	/**
	 * Sets the bottom texture name.
	 */
	public void setTextureBottom(String textureBottom)
	{
		if (!NameUtils.isValidTextureName(textureBottom))
			throw new DataConversionException("Texture name is invalid.");
		this.textureBottom = textureBottom;
	}
	
	@Override
	public String getTextureBottom()
	{
		return textureBottom;
	}

	/**
	 * Sets the middle texture name.
	 */
	public void setTextureMiddle(String textureMiddle)
	{
		if (!NameUtils.isValidTextureName(textureMiddle))
			throw new DataConversionException("Texture name is invalid.");
		this.textureMiddle = textureMiddle;
	}
	
	@Override
	public String getTextureMiddle()
	{
		return textureMiddle;
	}

	/**
	 * Sets the sector reference index for this sidedef.
	 */
	public void setSectorIndex(int sectorIndex)
	{
		this.sectorIndex = sectorIndex;
	}
	
	@Override
	public int getSectorIndex()
	{
		return sectorIndex;
	}

	@Override
	public byte[] toBytes() throws DataExportException
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
		offsetX = sr.readShort();
		offsetY = sr.readShort();
		textureTop = NameUtils.nullTrim(sr.readASCIIString(8)).toUpperCase();
		textureBottom = NameUtils.nullTrim(sr.readASCIIString(8)).toUpperCase();
		textureMiddle = NameUtils.nullTrim(sr.readASCIIString(8)).toUpperCase();
		sectorIndex = sr.readShort();
	}

	@Override
	public void writeBytes(OutputStream out) throws DataExportException, IOException
	{
		RangeUtils.checkShort("X-offset", offsetX);
		RangeUtils.checkShort("Y-offset", offsetY);
		RangeUtils.checkShort("Sector Index", sectorIndex);
		
		SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);
		sw.writeShort((short)offsetX);
		sw.writeShort((short)offsetY);
		sw.writeBytes(NameUtils.toASCIIBytes(textureTop, 8));
		sw.writeBytes(NameUtils.toASCIIBytes(textureBottom, 8));
		sw.writeBytes(NameUtils.toASCIIBytes(textureMiddle, 8));
		sw.writeShort((short)sectorIndex);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Sidedef");
		sb.append(' ').append("Offset (").append(offsetX).append(", ").append(offsetY).append(")");
		sb.append(' ').append(String.format("%-8s %-8s %-8s", textureTop, textureBottom, textureMiddle));
		sb.append(' ').append("Sector ").append(sectorIndex);
		return sb.toString();
	}
	
}
