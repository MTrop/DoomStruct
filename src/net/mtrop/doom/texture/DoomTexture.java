/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.texture;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.mtrop.doom.util.NameUtils;

import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * This class represents a single texture entry in a TEXTURE1/TEXTURE2/TEXTURES lump.
 * Doom Textures have the same binary representation in Heretic and Hexen. 
 * @author Matthew Tropiano
 */
public class DoomTexture extends CommonTexture<DoomTexture.Patch>
{
	
	/**
	 * Reads and creates a new DoomTexture object from an array of bytes.
	 * This reads until it reaches the end of the texture.
	 * @param bytes the byte array to read.
	 * @return a new DoomTexture object.
	 * @throws IOException if the stream cannot be read.
	 */
	public static DoomTexture create(byte[] bytes) throws IOException
	{
		DoomTexture out = new DoomTexture();
		out.fromBytes(bytes);
		return out;
	}
	
	/**
	 * Reads and creates a new DoomTexture from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for a single {@link DoomTexture} are read.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @return a new DoomTexture object.
	 * @throws IOException if the stream cannot be read.
	 */
	public static DoomTexture read(InputStream in) throws IOException
	{
		DoomTexture out = new DoomTexture();
		out.readBytes(in);
		return out;
	}
	
	@Override
	public Patch createPatch() 
	{
		Patch out = new Patch();
		patches.add(out);
		return out;
	}

	@Override
	public void readBytes(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
		name = NameUtils.toValidTextureName(NameUtils.nullTrim(sr.readASCIIString(8)));
		sr.readShort();
		sr.readShort();
		width = sr.readUnsignedShort();
		height = sr.readUnsignedShort();
		sr.readShort();
		sr.readShort();
		
		patches.clear();
		
		int n = sr.readUnsignedShort();
		while (n-- > 0)
		{
			Patch p = new Patch();
			p.readBytes(in);
			patches.add(p);
		}
	}
	
	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);
		sw.writeBytes(NameUtils.toASCIIBytes(name, 8));
		sw.writeUnsignedShort(0);
		sw.writeUnsignedShort(0);
		sw.writeUnsignedShort(width);
		sw.writeUnsignedShort(height);
		sw.writeUnsignedShort(0);
		sw.writeUnsignedShort(0);
		sw.writeUnsignedShort(patches.size());
		for (Patch p : patches)
			p.writeBytes(out);
	}
	
	/**
	 * Singular patch entry for a texture.
	 */
	public static class Patch extends net.mtrop.doom.texture.CommonPatch
	{
		@Override
		public void readBytes(InputStream in) throws IOException
		{
			SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
			originX = sr.readShort();
			originY = sr.readShort();
			patchIndex = sr.readUnsignedShort();
			sr.readShort();
			sr.readShort();
		}
		
		@Override
		public void writeBytes(OutputStream out) throws IOException
		{
			SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);
			sw.writeShort((short)originX);
			sw.writeShort((short)originY);
			sw.writeUnsignedShort(patchIndex);
			sw.writeUnsignedShort(1);
			sw.writeUnsignedShort(0);
		}

	}
	
}
