package net.mtrop.doom.struct;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.mtrop.doom.exception.DataExportException;
import net.mtrop.doom.map.BinaryObject;
import net.mtrop.doom.util.NameUtils;

import com.blackrook.commons.Common;
import com.blackrook.commons.list.List;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * This class represents the contents of a Boom Engine ANIMATED
 * lump. This lump contains extended information regarding animated
 * flats and textures.
 * <p>
 * NOTE: {@link Animated#readBytes(InputStream)} will read chunks of 23 bytes until it detects
 * the end of the ANIMATED entry list, NOT once it detects the end of the stream.
 * @author Matthew Tropiano
 */
public class Animated extends List<Animated.Entry> implements BinaryObject
{
	/**
	 * Enumeration of Animated Entry Texture types. 
	 */
	public static enum TextureType
	{
		FLAT,
		TEXTURE;
	}
	
	/** List of flats. */
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
	 * This reads from the stream until enough bytes for a {@link Animated} are read.
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
	 * Adds a flat entry to this lump.
	 * The names must include a number and be 8 characters or less.
	 * @param lastName	the last name in the sequence.
	 * @param firstName the first name in the sequence.
	 * @param ticks the amount of ticks between each frame.
	 * @throw {@link IllegalArgumentException} if lastName or firstName is not a valid entry name, or frame ticks is less than 1.
	 */
	public void addFlat(String lastName, String firstName, int ticks)
	{
		if (NameUtils.isValidEntryName(lastName))
			throw new IllegalArgumentException("Last texture name is invalid.");
		else if (NameUtils.isValidEntryName(firstName))
			throw new IllegalArgumentException("First texture name is invalid.");
		else if (ticks < 1 || ticks > Integer.MAX_VALUE)
			throw new IllegalArgumentException("Frame ticks must be between 1 and 2^31 - 1.");

		entryList.add(new Entry(false, lastName, firstName, ticks));
	}
	
	/**
	 * Adds a texture entry to this lump.
	 * The names must be 8 characters or less.
	 * @param lastName	the last name in the sequence.
	 * @param firstName the first name in the sequence.
	 * @param ticks the amount of ticks between each frame.
	 */
	public void addTexture(String lastName, String firstName, int ticks)
	{
		addTexture(lastName, firstName, ticks, false);
	}
	
	/**
	 * Adds a texture entry to this lump.
	 * The names must be 8 characters or less.
	 * @param lastName	the last name in the sequence.
	 * @param firstName the first name in the sequence.
	 * @param ticks the amount of ticks between each frame.
	 * @param decals if true, allows decals to be placed on this texture, false if not.
	 * @throw {@link IllegalArgumentException} if lastName or firstName is not a valid texture name, or frame ticks is less than 1.
	 */
	public void addTexture(String lastName, String firstName, int ticks, boolean decals)
	{
		if (NameUtils.isValidTextureName(lastName))
			throw new IllegalArgumentException("Last texture name is invalid.");
		else if (NameUtils.isValidTextureName(firstName))
			throw new IllegalArgumentException("First texture name is invalid.");
		else if (ticks < 1 || ticks > Integer.MAX_VALUE)
			throw new IllegalArgumentException("Frame ticks must be between 1 and 2^31 - 1.");
		
		entryList.add(new Entry(true, decals, lastName, firstName, ticks));
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
		Entry e = null;
		do {
			e = new Entry();
			e.readBytes(in);
			if (e.type != null)
				add(e);
		} while (e.type != null);
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		for (Entry e : this) 
			e.writeBytes(out);
		(new Entry()).writeBytes(out);
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
		Entry()
		{
			this(null, "\0\0\0\0\0\0\0\0", "\\0\\0\\0\\0\\0\\0\\0\\0", 1);
		}
		
		/**
		 * Creates a new Entry.
		 * @param texture	is this a texture entry (as opposed to a flat)?
		 * @param lastName	the last name in the sequence.
		 * @param firstName the first name in the sequence.
		 * @param ticks the amount of ticks between each frame.
		 */
		Entry(boolean texture, String lastName, String firstName, int ticks)
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
		Entry(boolean texture, boolean allowsDecals, String lastName, String firstName, int ticks)
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
		 * Returns the texture type of the entry (for FLAT or TEXTURE? null if terminal entry).
		 */
		public TextureType getType()
		{
			return type;
		}

		/**
		 * Returns if this texture allows decals on it, despite it being animated.
		 * True if so, false if not.
		 */
		public boolean getAllowsDecals()
		{
			return allowsDecals;
		}

		/**
		 * Returns the last texture/flat name in the animation sequence.
		 */
		public String getLastName()
		{
			return lastName;
		}

		/**
		 * Returns the first texture/flat name in the animation sequence.
		 */
		public String getFirstName()
		{
			return firstName;
		}

		/**
		 * Returns the amount of ticks between each frame.
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
		public void writeBytes(OutputStream out) throws IOException, DataExportException
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
			sw.writeASCIIString(lastName);
			sw.writeBoolean(false); // ensure null terminal
			sw.writeASCIIString(firstName);
			sw.writeBoolean(false); // ensure null terminal
			sw.writeInt(ticks);
		}
		
		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append("ANIMATED "); 
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
