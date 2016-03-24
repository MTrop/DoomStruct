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

import net.mtrop.doom.BinaryObject;

import com.blackrook.io.SuperReader;

/**
 * This is the lump that contains a collection of Strife-formatted textures.
 * All textures are stored in here, usually named TEXTURE1 or TEXTURE2 in the WAD.
 * @author Matthew Tropiano
 */
public class StrifeTextureList extends CommonTextureList<StrifeTexture> implements BinaryObject
{
	/**
	 * Creates a new TextureList with a default starting capacity.
	 */
	public StrifeTextureList()
	{
		super();
	}

	/**
	 * Creates a new TextureList with a specific starting capacity.
	 */
	public StrifeTextureList(int capacity)
	{
		super(capacity);
	}

	/**
	 * Reads and creates a new StrifeTextureList from an array of bytes.
	 * This reads a full texture set from the array.
	 * @param bytes the byte array to read.
	 * @return a new StrifeTextureList with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static StrifeTextureList create(byte[] bytes) throws IOException
	{
		StrifeTextureList out = new StrifeTextureList();
		out.fromBytes(bytes);
		return out;
	}
	
	/**
	 * Reads and creates a new DoomTextureList from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for a full texture set are read.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @return a new StrifeTextureList with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static StrifeTextureList read(InputStream in) throws IOException
	{
		StrifeTextureList out = new StrifeTextureList();
		out.readBytes(in);
		return out;
	}
	
	@Override
	public void readBytes(InputStream in) throws IOException
	{
		clear();
		SuperReader sr = new SuperReader(in, SuperReader.LITTLE_ENDIAN);
		int n = sr.readInt();
		
		in.skip(n*4);
		
		while(n-- > 0)
		{
			StrifeTexture t = new StrifeTexture();
			t.readBytes(in);
			add(t);
		}
	}

}
