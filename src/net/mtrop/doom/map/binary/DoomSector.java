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
import net.mtrop.doom.map.Sector;
import net.mtrop.doom.util.NameUtils;
import net.mtrop.doom.util.RangeUtils;

/**
 * Doom/Boom 26-byte format implementation of Sector.
 * @author Matthew Tropiano
 */
public class DoomSector implements Sector, BinaryMapObject
{
	/** Sector Floor height. */
	private int floorHeight;
	/** Sector Ceiling height. */
	private int ceilingHeight;
	/** Sector Floor texture. */
	private String floorTexture;
	/** Sector Ceiling texture. */
	private String ceilingTexture;
	/** Sector light level. */
	private int lightLevel;
	/** Sector special. */
	private int special;
	/** Sector tag. */
	private int tag;
	
	/**
	 * Creates a new sector with default values set.
	 * @see #reset()
	 */
	public DoomSector()
	{
		reset();
	}
	
	/**
	 * Reads and creates a new DoomSector from an array of bytes.
	 * This reads from the first 30 bytes of the stream.
	 * The stream is NOT closed at the end.
	 * @param bytes the byte array to read.
	 * @return a new DoomSector with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static DoomSector create(byte[] bytes) throws IOException
	{
		DoomSector out = new DoomSector();
		out.fromBytes(bytes);
		return out;
	}
	
	/**
	 * Reads and creates a new DoomSector from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for a {@link DoomSector} are read.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @return a new DoomSector with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static DoomSector read(InputStream in) throws IOException
	{
		DoomSector out = new DoomSector();
		out.readBytes(in);
		return out;
	}
	
	/**
	 * Resets this sector's properties to defaults.
	 * <ul>
	 * <li>Both heights are set to 0.</li>
	 * <li>All textures are set to {@link Sector#TEXTURE_BLANK}.</li>
	 * <li>The light level is set to 0.</li>
	 * <li>The special and tag are set to 0.</li>
	 * </ul>
	 */
	public void reset()
	{
		floorHeight = 0;
		ceilingHeight = 0;
		floorTexture = TEXTURE_BLANK;
		ceilingTexture = TEXTURE_BLANK;
		lightLevel = 0;
		special = 0;
		tag = 0;
	}
	
	/**
	 * Sets this sector's floor height. 
	 */
	public void setFloorHeight(int floorHeight)
	{
		this.floorHeight = floorHeight;
	}
	
	@Override
	public int getFloorHeight()
	{
		return floorHeight;
	}

	/**
	 * Sets this sector's ceiling height. 
	 */
	public void setCeilingHeight(int ceilingHeight)
	{
		this.ceilingHeight = ceilingHeight;
	}
	
	@Override
	public int getCeilingHeight()
	{
		return ceilingHeight;
	}

	/**
	 * Sets this sector's floor texture. 
	 */
	public void setFloorTexture(String floorTexture)
	{
		if (!NameUtils.isValidTextureName(floorTexture))
			throw new DataConversionException("Texture name is invalid.");
		this.floorTexture = floorTexture;
	}
	
	@Override
	public String getFloorTexture()
	{
		return floorTexture;
	}

	/**
	 * Sets this sector's ceiling texture. 
	 */
	public void setCeilingTexture(String ceilingTexture)
	{
		if (!NameUtils.isValidTextureName(ceilingTexture))
			throw new DataConversionException("Texture name is invalid.");
		this.ceilingTexture = ceilingTexture;
	}
	
	@Override
	public String getCeilingTexture()
	{
		return ceilingTexture;
	}

	/**
	 * Sets this sector's light level. 
	 */
	public void setLightLevel(int lightLevel)
	{
		this.lightLevel = lightLevel;
	}
	
	@Override
	public int getLightLevel()
	{
		return lightLevel;
	}

	/**
	 * Sets this sector's special. 
	 */
	public void setSpecial(int special)
	{
		this.special = special;
	}
	
	@Override
	public int getSpecial()
	{
		return special;
	}

	/**
	 * Sets this sector's tag. 
	 */
	public void setTag(int tag)
	{
		this.tag = tag;
	}
	
	@Override
	public int getTag()
	{
		return tag;
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
		floorHeight = sr.readShort();
		ceilingHeight = sr.readShort();
		floorTexture = NameUtils.nullTrim(sr.readASCIIString(8)).toUpperCase();
		ceilingTexture = NameUtils.nullTrim(sr.readASCIIString(8)).toUpperCase();
		lightLevel = sr.readShort();
		special = sr.readShort();
		tag = sr.readShort();
	}

	@Override
	public void writeBytes(OutputStream out) throws DataExportException, IOException
	{
		RangeUtils.checkShort("Floor Height", floorHeight);
		RangeUtils.checkShort("Ceiling Height", ceilingHeight);
		RangeUtils.checkShort("Light Level", lightLevel);
		RangeUtils.checkShort("Special", special);
		RangeUtils.checkShort("Tag", tag);

		SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);
		sw.writeShort((short)floorHeight);
		sw.writeShort((short)ceilingHeight);
		sw.writeBytes(NameUtils.toASCIIBytes(floorTexture, 8));
		sw.writeBytes(NameUtils.toASCIIBytes(ceilingTexture, 8));
		sw.writeShort((short)lightLevel);
		sw.writeShort((short)special);
		sw.writeShort((short)tag);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Sector");
		sb.append(' ').append("Ceiling ").append(ceilingHeight).append(" Floor ").append(floorHeight);
		sb.append(' ').append(String.format("%-8s %-8s", ceilingTexture, floorTexture));
		sb.append(' ').append("Light ").append(lightLevel);
		sb.append(' ').append("Special ").append(special);
		sb.append(' ').append("Tag ").append(tag);
		return sb.toString();
	}

}
