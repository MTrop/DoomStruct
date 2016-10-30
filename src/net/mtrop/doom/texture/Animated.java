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
import java.util.Iterator;

import net.mtrop.doom.BinaryObject;
import net.mtrop.doom.util.NameUtils;

import com.blackrook.commons.Common;
import com.blackrook.commons.Sizable;
import com.blackrook.commons.list.List;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * This class represents the contents of a Boom Engine ANIMATED
 * lump. This lump contains extended information regarding animated
 * flats and textures.
 * @author Matthew Tropiano
 */
public class Animated implements BinaryObject, Iterable<Animated.Entry>, Sizable
{
	/**
	 * Enumeration of Animated Entry Texture types. 
	 */
	public static enum TextureType
	{
		FLAT,
		TEXTURE;
	}
	
	/** List of entries. */
	protected List<Entry> entryList;
	
	/**
	 * Creates a new ANIMATED lump.
	 */
	public Animated()
	{
		entryList = new List<Entry>(20);
	}
	
	/**
	 * Reads and creates a new Animated object from an array of bytes.
	 * This reads until it reaches the end of the entry list.
	 * @param bytes the byte array to read.
	 * @return a new Animated object.
	 * @throws IOException if the stream cannot be read.
	 */
	public static Animated create(byte[] bytes) throws IOException
	{
		Animated out = new Animated();
		out.fromBytes(bytes);
		return out;
	}
	
	/**
	 * Reads and creates a new Animated from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for a full {@link Animated} lump are read.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @return a new Animated with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static Animated read(InputStream in) throws IOException
	{
		Animated out = new Animated();
		out.readBytes(in);
		return out;
	}

	/**
	 * Adds an entry to this Animated lump.
	 * @param entry the entry to add.
	 * @see #flat(String, String, int)
	 * @see #texture(String, String, int)
	 * @see #texture(String, String, int, boolean)
	 */
	public void addEntry(Entry entry)
	{
		entryList.add(entry);
	}
	
	/**
	 * Returns an Animated entry at a specific index.
	 * @param i the index of the entry to return.
	 * @return the corresponding entry, or <code>null</code> if no corresponding entry for that index.
	 * @throws IndexOutOfBoundsException if the index is out of range (less than 0 or greater than or equal to getFlatCount()).
	 */
	public Entry getEntry(int i)
	{
		return entryList.getByIndex(i);
	}

	/**
	 * Removes an Animated entry at a specific index.
	 * @param i the index of the entry to remove.
	 * @return the corresponding removed entry, or <code>null</code> if no corresponding entry for that index.
	 * @throws IndexOutOfBoundsException if the index is out of range (less than 0 or greater than or equal to getSwitchCount()).
	 */
	public Entry removeEntry(int i)
	{
		return entryList.removeIndex(i);
	}

	/**
	 * @return the amount of switch entries in this lump.
	 */
	public int getEntryCount()
	{
		return entryList.size();
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
		entryList.clear();
		Entry e = null;
		do {
			e = new Entry();
			e.readBytes(in);
			if (e.type != null)
				entryList.add(e);
		} while (e.type != null);
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		for (Entry e : entryList) 
			e.writeBytes(out);
		(new Entry()).writeBytes(out);
	}

	@Override
	public Iterator<Entry> iterator()
	{
		return entryList.iterator();
	}
	
	@Override
	public int size() 
	{
		return entryList.size();
	}

	@Override
	public boolean isEmpty() 
	{
		return entryList.isEmpty();
	}

	/**
	 * Creates a flat entry.
	 * @param lastName	the last name in the sequence.
	 * @param firstName the first name in the sequence.
	 * @param ticks the amount of ticks between each frame.
	 * @return a new entry detailing an animated texture.
	 * @throws IllegalArgumentException if <code>lastName</code> or <code>firstName</code> is not a valid texture name, or frame ticks is less than 1.
	 */
	public static Entry flat(String lastName, String firstName, int ticks)
	{
		if (!NameUtils.isValidTextureName(lastName))
			throw new IllegalArgumentException("Last texture name is invalid.");
		else if (!NameUtils.isValidTextureName(firstName))
			throw new IllegalArgumentException("First texture name is invalid.");
		else if (ticks < 1 || ticks > Integer.MAX_VALUE)
			throw new IllegalArgumentException("Frame ticks must be between 1 and 2^31 - 1.");
		
		return new Entry(false, lastName, firstName, ticks);
	}

	/**
	 * Creates a texture entry.
	 * @param lastName	the last name in the sequence.
	 * @param firstName the first name in the sequence.
	 * @param ticks the amount of ticks between each frame.
	 * @return a new entry detailing an animated texture.
	 * @throws IllegalArgumentException if <code>lastName</code> or <code>firstName</code> is not a valid texture name, or frame ticks is less than 1.
	 */
	public static Entry texture(String lastName, String firstName, int ticks)
	{
		return new Entry(true, false, lastName, firstName, ticks);
	}
	
	/**
	 * Creates a texture entry.
	 * @param lastName	the last name in the sequence.
	 * @param firstName the first name in the sequence.
	 * @param ticks the amount of ticks between each frame.
	 * @param decals if true, allows decals to be placed on this texture, false if not.
	 * @return a new entry detailing an animated texture.
	 * @throws IllegalArgumentException if <code>lastName</code> or <code>firstName</code> is not a valid texture name, or frame ticks is less than 1.
	 */
	public static Entry texture(String lastName, String firstName, int ticks, boolean decals)
	{
		if (!NameUtils.isValidTextureName(lastName))
			throw new IllegalArgumentException("Last texture name is invalid.");
		else if (!NameUtils.isValidTextureName(firstName))
			throw new IllegalArgumentException("First texture name is invalid.");
		else if (ticks < 1 || ticks > Integer.MAX_VALUE)
			throw new IllegalArgumentException("Frame ticks must be between 1 and 2^31 - 1.");
		
		return new Entry(true, decals, lastName, firstName, ticks);
	}
	
	/** Flat entry for ANIMATED. */
	public static class Entry implements BinaryObject
	{
		/** Is this a texture entry? If not, it's a flat. */
		protected TextureType type;
		/** The last texture name. */
		protected String lastName;
		/** The first texture name. */
		protected String firstName;
		/** Allows decals. */
		protected boolean allowsDecals;
		/** The amount of ticks between each frame. */
		protected int ticks;
		
		/**
		 * Creates a new Entry (terminal type).
		 */
		private Entry()
		{
			this(null, "\0\0\0\0\0\0\0\0", "\0\0\0\0\0\0\0\0", 1);
		}
		
		/**
		 * Creates a new Entry.
		 * @param texture	is this a texture entry (as opposed to a flat)?
		 * @param lastName	the last name in the sequence.
		 * @param firstName the first name in the sequence.
		 * @param ticks the amount of ticks between each frame.
		 */
		private Entry(boolean texture, String lastName, String firstName, int ticks)
		{
			this(texture ? TextureType.TEXTURE : TextureType.FLAT, false, lastName, firstName, ticks);
		}

		/**
		 * Creates a new Entry.
		 * @param texture	is this a texture entry (as opposed to a flat)?
		 * @param allowsDecals if true, this texture allows decals.
		 * @param lastName	the last name in the sequence.
		 * @param firstName the first name in the sequence.
		 * @param ticks the amount of ticks between each frame.
		 */
		private Entry(boolean texture, boolean allowsDecals, String lastName, String firstName, int ticks)
		{
			this(texture ? TextureType.TEXTURE : TextureType.FLAT, lastName, firstName, ticks);
		}

		/**
		 * Creates a new Entry.
		 * @param type		what is the type of this animated entry (TEXTURE/FLAT)?
		 * @param lastName	the last name in the sequence.
		 * @param firstName the first name in the sequence.
		 * @param ticks the amount of ticks between each frame.
		 */
		Entry(TextureType type, String lastName, String firstName, int ticks)
		{
			this(type, false, lastName, firstName, ticks);
		}

		/**
		 * Creates a new Entry.
		 * @param type what is the type of this animated entry (TEXTURE/FLAT)?
		 * @param allowsDecals if true, this texture allows decals.
		 * @param lastName	the last name in the sequence.
		 * @param firstName the first name in the sequence.
		 * @param ticks the amount of ticks between each frame.
		 */
		Entry(TextureType type, boolean allowsDecals, String lastName, String firstName, int ticks)
		{
			this.type = type;
			this.allowsDecals = allowsDecals;
			this.lastName = lastName;
			this.firstName = firstName;
			this.ticks = ticks;
		}

		/**
		 * Is this a texture entry?
		 * @return true if it is, false if not (it's a flat, then).
		 */
		public boolean isTexture()
		{
			return type == TextureType.TEXTURE;
		}

		/**
		 * @return the texture type of the entry (for FLAT or TEXTURE? null if terminal entry).
		 */
		public TextureType getType()
		{
			return type;
		}

		/**
		 * Returns if this texture allows decals on it, despite it being animated.
		 * @return true if so, false if not.
		 */
		public boolean getAllowsDecals()
		{
			return allowsDecals;
		}

		/**
		 * @return the last texture/flat name in the animation sequence.
		 */
		public String getLastName()
		{
			return lastName;
		}

		/**
		 * @return the first texture/flat name in the animation sequence.
		 */
		public String getFirstName()
		{
			return firstName;
		}

		/**
		 * @return the amount of ticks between each frame.
		 */
		public int getTicks()
		{
			return ticks;
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
			SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
			byte b = sr.readByte();
			if (b != -1)
			{
				if ((b & 0x01) == 0)
					type = TextureType.FLAT;
				else if ((b & 0x01) != 0)
					type = TextureType.TEXTURE;
				
				if ((b & 0x02) != 0)
					allowsDecals = true;
			}
			else
			{
				type = null;
				return;
			}
			lastName = NameUtils.toValidTextureName(NameUtils.nullTrim(sr.readASCIIString(9)));
			firstName = NameUtils.toValidTextureName(NameUtils.nullTrim(sr.readASCIIString(9)));
			ticks = sr.readInt();
		}

		@Override
		public void writeBytes(OutputStream out) throws IOException
		{
			SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);
			if (type != null) 
			{
				byte b = (byte)type.ordinal();
				b |= allowsDecals ? 0x02 : 0x00;
				sw.writeByte(b);
			}
			else
				sw.writeByte((byte)-1);
			
			sw.writeBytes(NameUtils.toASCIIBytes(lastName, 8));
			sw.writeBoolean(false); // ensure null terminal
			sw.writeBytes(NameUtils.toASCIIBytes(firstName, 8));
			sw.writeBoolean(false); // ensure null terminal
			sw.writeInt(ticks);
		}
		
		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append("Animated "); 
			sb.append(type != null ? TextureType.values()[type.ordinal()] : "[TERMINAL]");
			sb.append(' ');
			sb.append(lastName);
			sb.append(' ');
			sb.append(firstName);
			sb.append(' ');
			sb.append(ticks);
			sb.append(' ');
			sb.append(allowsDecals ? "DECALS" : "");
			return sb.toString();
		}
		
	}
	
}
