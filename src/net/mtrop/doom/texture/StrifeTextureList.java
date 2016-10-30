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

import net.mtrop.doom.BinaryObject;
import net.mtrop.doom.util.NameUtils;

import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * This is the lump that contains a collection of Strife-formatted textures.
 * All textures are stored in here, usually named TEXTURE1 or TEXTURE2 in the WAD.
 * @author Matthew Tropiano
 */
public class StrifeTextureList extends CommonTextureList<StrifeTextureList.Texture> implements BinaryObject
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
	 * @param capacity the starting capacity.
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
	public Texture createTexture(String texture) 
	{
		Texture out = new Texture(texture);
		addCreatedTexture(out);
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
			Texture t = new Texture();
			t.readBytes(in);
			addCreatedTexture(t);
		}
	}

	/**
	 * This class represents a single texture entry in a TEXTURE1/TEXTURE2 lump.
	 * Strife Textures use Strife's texture representation. 
	 * @author Matthew Tropiano
	 */
	public static class Texture extends CommonTexture<Texture.Patch>
	{
		private Texture()
		{
			super();
		}
		
		/**
		 * Creates a new texture.
		 * @param name the new texture name.
		 * @throws IllegalArgumentException if the texture name is invalid.
		 */
		private Texture(String name)
		{
			super(name);
		}

		/**
		 * Reads and creates a new StrifeTexture object from an array of bytes.
		 * This reads until it reaches the end of the texture.
		 * @param bytes the byte array to read.
		 * @return a new DoomTexture object.
		 * @throws IOException if the stream cannot be read.
		 */
		public static Texture create(byte[] bytes) throws IOException
		{
			Texture out = new Texture();
			out.fromBytes(bytes);
			return out;
		}
		
		/**
		 * Reads and creates a new StrifeTexture from an {@link InputStream} implementation.
		 * This reads from the stream until enough bytes for a single {@link Texture} are read.
		 * The stream is NOT closed at the end.
		 * @param in the open {@link InputStream} to read from.
		 * @return a new DoomTexture object.
		 * @throws IOException if the stream cannot be read.
		 */
		public static Texture read(InputStream in) throws IOException
		{
			Texture out = new Texture();
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
			sw.writeUnsignedShort(patches.size());
			for (Patch p : patches)
				p.writeBytes(out);
		}

		/**
		 * Singular patch entry for a texture.
		 */
		public static class Patch extends CommonPatch
		{
			@Override
			public void readBytes(InputStream in) throws IOException
			{
				SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
				originX = sr.readShort();
				originY = sr.readShort();
				patchIndex = sr.readUnsignedShort();
			}

			@Override
			public void writeBytes(OutputStream out) throws IOException
			{
				SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);
				sw.writeShort((short)originX);
				sw.writeShort((short)originY);
				sw.writeUnsignedShort(patchIndex);
			}

		}
		
	}

}
