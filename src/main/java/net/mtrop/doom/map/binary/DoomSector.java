/*******************************************************************************
 * Copyright (c) 2015-2019 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map.binary;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.mtrop.doom.BinaryObject;
import net.mtrop.doom.map.MapObjectConstants;
import net.mtrop.doom.util.NameUtils;
import net.mtrop.doom.util.RangeUtils;
import net.mtrop.doom.util.SerialReader;
import net.mtrop.doom.util.SerialWriter;

/**
 * Doom/Boom 26-byte format implementation of Sector.
 * @author Matthew Tropiano
 */
public class DoomSector implements BinaryObject
{
	/** Byte length of this object. */
	public static final int LENGTH = 26;

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
	 * Creates a new sector.
	 */
	public DoomSector()
	{
		floorTexture = MapObjectConstants.TEXTURE_BLANK;
		ceilingTexture = MapObjectConstants.TEXTURE_BLANK;
	}
	
	/**
	 * Sets this sector's floor height. 
	 * @param floorHeight the new height.
	 * @throws IllegalArgumentException if floorHeight is outside of the range -32768 to 32767.
	 */
	public void setFloorHeight(int floorHeight)
	{
		RangeUtils.checkShort("Floor Height", floorHeight);
		this.floorHeight = floorHeight;
	}
	
	/**
	 * @return the sector's floor height.
	 */
	public int getFloorHeight()
	{
		return floorHeight;
	}

	/**
	 * Sets the sector's ceiling height. 
	 * @param ceilingHeight the new height.
	 * @throws IllegalArgumentException if floorHeight is outside of the range -32768 to 32767.
	 */
	public void setCeilingHeight(int ceilingHeight)
	{
		RangeUtils.checkShort("Ceiling Height", ceilingHeight);
		this.ceilingHeight = ceilingHeight;
	}
	
	/**
	 * @return the sector's ceiling height.
	 */
	public int getCeilingHeight()
	{
		return ceilingHeight;
	}

	/**
	 * Sets the sector's floor texture.
	 * @param floorTexture the new texture.
	 * @throws IllegalArgumentException if the texture name is invalid. 
	 */
	public void setFloorTexture(String floorTexture)
	{
		if (!NameUtils.isValidTextureName(floorTexture))
			throw new IllegalArgumentException("Texture name is invalid.");
		this.floorTexture = floorTexture;
	}
	
	/**
	 * @return the sector's floor texture.
	 */
	public String getFloorTexture()
	{
		return floorTexture;
	}

	/**
	 * Sets the sector's ceiling texture. 
	 * @param ceilingTexture the new texture.
	 * @throws IllegalArgumentException if the texture name is invalid. 
	 */
	public void setCeilingTexture(String ceilingTexture)
	{
		if (!NameUtils.isValidTextureName(ceilingTexture))
			throw new IllegalArgumentException("Texture name is invalid.");
		this.ceilingTexture = ceilingTexture;
	}
	
	/**
	 * @return the sector's ceiling texture. 
	 */
	public String getCeilingTexture()
	{
		return ceilingTexture;
	}

	/**
	 * Sets the sector's light level. 
	 * @param lightLevel the new light level.
	 * @throws IllegalArgumentException if lightLevel is outside the range 0 to 255.
	 */
	public void setLightLevel(int lightLevel)
	{
		RangeUtils.checkShort("Light Level", lightLevel);
		this.lightLevel = lightLevel;
	}
	
	/**
	 * @return the sector's light level.
	 */
	public int getLightLevel()
	{
		return lightLevel;
	}

	/**
	 * Sets the sector's special. 
	 * @param special the new special number.
	 * @throws IllegalArgumentException if special is outside the range 0 to 65535.
	 */
	public void setSpecial(int special)
	{
		RangeUtils.checkShort("Special", special);
		this.special = special;
	}
	
	/**
	 * @return the sector's special. 
	 */
	public int getSpecial()
	{
		return special;
	}

	/**
	 * Sets the sector's tag. 
	 * @param tag the new tag.
	 * @throws IllegalArgumentException if tag is outside the range 0 to 65535.
	 */
	public void setTag(int tag)
	{
		RangeUtils.checkShort("Tag", tag);
		this.tag = tag;
	}
	
	/**
	 * @return the sector's tag.
	 */
	public int getTag()
	{
		return tag;
	}

	@Override
	public void readBytes(InputStream in) throws IOException
	{
		SerialReader sr = new SerialReader(SerialReader.LITTLE_ENDIAN);
		floorHeight = sr.readShort(in);
		ceilingHeight = sr.readShort(in);
		floorTexture = NameUtils.nullTrim(sr.readString(in, 8, "ASCII"));
		ceilingTexture = NameUtils.nullTrim(sr.readString(in, 8, "ASCII"));
		lightLevel = sr.readShort(in);
		special = sr.readShort(in);
		tag = sr.readShort(in);
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		SerialWriter sw = new SerialWriter(SerialWriter.LITTLE_ENDIAN);
		sw.writeShort(out, (short)floorHeight);
		sw.writeShort(out, (short)ceilingHeight);
		sw.writeBytes(out, NameUtils.toASCIIBytes(floorTexture, 8));
		sw.writeBytes(out, NameUtils.toASCIIBytes(ceilingTexture, 8));
		sw.writeShort(out, (short)lightLevel);
		sw.writeShort(out, (short)special);
		sw.writeShort(out, (short)tag);
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
