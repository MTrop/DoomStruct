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
import java.io.InputStream;
import java.io.OutputStream;

import com.blackrook.commons.Common;
import com.blackrook.commons.map.CaseInsensitiveMappedVector;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

import net.mtrop.doom.BinaryObject;
import net.mtrop.doom.util.NameUtils;

/**
 * A list of names of available patch entries for texture composition.
 * @author Matthew Tropiano
 */
public class PatchNames extends CaseInsensitiveMappedVector<String> implements BinaryObject
{

	/**
	 * Creates a new PatchNames with a default starting capacity.
	 */
	public PatchNames()
	{
		super();
	}

	/**
	 * Creates a new PatchNames with a specific starting capacity.
	 */
	public PatchNames(int capacity)
	{
		super(capacity);
	}

	/**
	 * Reads and creates a new PatchNames from an array of bytes.
	 * This reads a full patch name set from the array.
	 * @param bytes the byte array to read.
	 * @return a new PatchNames object.
	 * @throws IOException if the stream cannot be read.
	 */
	public static PatchNames create(byte[] bytes) throws IOException
	{
		PatchNames out = new PatchNames();
		out.fromBytes(bytes);
		return out;
	}
	
	/**
	 * Reads and creates a new DoomTextureList from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for a full patch name set are read.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @return a new PatchNames object.
	 * @throws IOException if the stream cannot be read.
	 */
	public static PatchNames read(InputStream in) throws IOException
	{
		PatchNames out = new PatchNames();
		out.readBytes(in);
		return out;
	}
	
	/**
	 * Gets the index of a patch name in this lump by its name.
	 * @param name the name of the patch.
	 * @return a valid index of found, or -1 if not.
	 */
	public int getIndex(String name)
	{
		return getIndexUsingKey(name);
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
		clear();
		SuperReader sr = new SuperReader(in, SuperReader.LITTLE_ENDIAN);
		int n = sr.readInt();
		while (n-- > 0)
			add(NameUtils.toValidEntryName(NameUtils.nullTrim(sr.readASCIIString(8))));
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);
		sw.writeInt(size());
		for (String s : this)
			sw.writeBytes(NameUtils.toASCIIBytes(s, 8));
	}

	@Override
	protected String getMappingKey(String object)
	{
		return object;
	}

}
