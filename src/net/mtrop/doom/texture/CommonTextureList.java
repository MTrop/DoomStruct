/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.texture;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.mtrop.doom.BinaryObject;
import net.mtrop.doom.util.NameUtils;

import com.blackrook.commons.Common;
import com.blackrook.commons.Sizable;
import com.blackrook.commons.map.AbstractMappedVector;
import com.blackrook.io.SuperWriter;

/**
 * This is the lump that contains a collection of textures.
 * All textures are stored in here, usually named TEXTURE1 or TEXTURE2 in a WAD.
 * Most creation methods in this object are factory-style, due to the diversity of implemented texture formats.
 * @author Matthew Tropiano
 */
public abstract class CommonTextureList<T extends CommonTexture<?>> extends AbstractMappedVector<T, String> implements BinaryObject, Sizable
{
	/**
	 * Creates a new TextureList with a default starting capacity.
	 */
	public CommonTextureList()
	{
		super();
	}

	/**
	 * Creates a new TextureList with a specific starting capacity.
	 */
	public CommonTextureList(int capacity)
	{
		super(capacity);
	}

	/**
	 * Gets the index of a texture in this lump by its name.
	 * @param name the name of the texture.
	 * @return a valid index if found, or -1 if not.
	 */
	public int getTextureIndex(String name)
	{
		return getIndexUsingKey(name);
	}
	
	/**
	 * Gets a texture in this lump by its name.
	 * @param name the name of the texture.
	 * @return a valid index if found, or -1 if not.
	 */
	public T getByName(String name)
	{
		return getUsingKey(name);
	}
	
	/**
	 * Creates a new texture in this list with no patches, at the end of the list.
	 * @param name the name of the texture.
	 * @return a new, empty texture object added to this list. 
	 * @throws IllegalArgumentException if the provided name is not a valid name for a texture.
	 * @see NameUtils#isValidTextureName(String)
	 */
	public abstract T createTexture(String name);
	
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
	public void writeBytes(OutputStream out) throws IOException
	{
		SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);
		sw.writeInt(size());
		
		byte[][] data = new byte[size()][];
	
		int n = 0;
		for (T t : this)
			data[n++] = t.toBytes();
		
		int offset = (size()+1) * 4;
		
		for (byte[] b : data)
		{
			sw.writeInt(offset);
			offset += b.length;
		}
	
		for (byte[] b : data)
			sw.writeBytes(b);
	}

	@Override
	protected String getMappingKey(T object)
	{
		return object.getName();
	}

}
