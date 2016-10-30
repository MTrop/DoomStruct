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

import com.blackrook.commons.AbstractVector;
import com.blackrook.commons.Common;
import com.blackrook.commons.Sizable;
import com.blackrook.commons.list.List;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * This class represents the contents of a Boom Engine SWITCHES
 * lump. This lump contains extended information regarding textures
 * used for in-game switches.
 * @author Matthew Tropiano
 */
public class Switches implements BinaryObject, Iterable<Switches.Entry>, Sizable
{
	/** Enumeration of game types. */
	public static enum Game
	{
		/** No entry should contain this - internal use only. */
		TERMINAL_SPECIAL,
		SHAREWARE_DOOM,
		DOOM,
		ALL;
	}
	
	/** List of entries. */
	protected AbstractVector<Entry> entryList;
	
	/**
	 * Creates a new SWITCHES lump.
	 */
	public Switches()
	{
		entryList = new List<Entry>(20);
	}
	
	/**
	 * Reads and creates a new Switches object from an array of bytes.
	 * This reads until it reaches the end of the entry list.
	 * @param bytes the byte array to read.
	 * @return a new Switches object.
	 * @throws IOException if the stream cannot be read.
	 */
	public static Switches create(byte[] bytes) throws IOException
	{
		Switches out = new Switches();
		out.fromBytes(bytes);
		return out;
	}
	
	/**
	 * Reads and creates a new Switches from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for a full {@link Switches} lump are read.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @return a new Switches with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static Switches read(InputStream in) throws IOException
	{
		Switches out = new Switches();
		out.readBytes(in);
		return out;
	}
	
	/**
	 * Returns a switch entry at a specific index.
	 * @param i the index of the entry to return.
	 * @return the corresponding entry, or <code>null</code> if no corresponding entry for that index.
	 * @throws IndexOutOfBoundsException  if the index is out of range (less than 0 or greater than or equal to getFlatCount()).
	 */
	public Entry getEntry(int i)
	{
		return entryList.getByIndex(i);
	}
	
	/**
	 * Removes a switch entry at a specific index.
	 * @param i the index of the entry to remove.
	 * @return the corresponding removed entry, or <code>null</code> if no corresponding entry for that index.
	 * @throws IndexOutOfBoundsException  if the index is out of range (less than 0 or greater than or equal to getSwitchCount()).
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
	
	/**
	 * Adds a switch entry to this lump.
	 * The names must be 8 characters or less.
	 * @param offName the "off" name for the switch.
	 * @param onName the "on" name for the switch.
	 * @param game the game type that this switch works with.
	 */
	public void addEntry(String offName, String onName, Game game)
	{
		entryList.add(new Entry(offName, onName, game));
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
			if (e.game != Game.TERMINAL_SPECIAL)
				entryList.add(e);
		} while (e.game != Game.TERMINAL_SPECIAL);
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		for (Entry e : entryList) 
			e.writeBytes(out);
		(new Entry()).writeBytes(out); // write blank terminal.
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

	/** Entry for Switches. */
	public static class Entry implements BinaryObject
	{
		/** The "off" texture name. */
		protected String offName;
		/** The "on" texture name. */
		protected String onName;
		/** The game that this is used for. */
		protected Game game;
		
		/**
		 * Creates a new Entry.
		 */
		Entry()
		{
			offName = "";
			onName = "";
			game = Game.TERMINAL_SPECIAL;
		}

		/**
		 * Creates a new Entry.
		 * @param offName the name of the switch "off" texture.
		 * @param onName the name of the switch "on" texture.
		 * @param game the game type that this switch is used for.
		 */
		Entry(String offName, String onName, Game game)
		{
			if (!NameUtils.isValidTextureName(offName))
				throw new IllegalArgumentException("\"Off\" Texture Name is invalid.");
			if (!NameUtils.isValidTextureName(offName))
				throw new IllegalArgumentException("\"On\" Texture Name is invalid.");
			if (game == null)
				throw new IllegalArgumentException("Game cannot be null.");

			this.offName = offName;
			this.onName = onName;
			this.game = game;
		}

		/**
		 * @return the switch "off" position texture.  
		 */
		public String getOffName()
		{
			return offName;
		}

		/**
		 * @return the switch "on" position texture.  
		 */
		public String getOnName()
		{
			return onName;
		}

		/**
		 * @return the active game type of the switch.  
		 */
		public Game getGame()
		{
			return game;
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
			offName = NameUtils.toValidTextureName(NameUtils.nullTrim(sr.readASCIIString(9)));
			onName = NameUtils.toValidTextureName(NameUtils.nullTrim(sr.readASCIIString(9)));
			game = Game.values()[sr.readShort()];
		}

		@Override
		public void writeBytes(OutputStream out) throws IOException
		{
			SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);
			sw.writeBytes(NameUtils.toASCIIBytes(offName, 8));
			sw.writeBoolean(false);  // ensure null terminal
			sw.writeBytes(NameUtils.toASCIIBytes(onName, 8));
			sw.writeBoolean(false);  // ensure null terminal
			sw.writeShort((short)game.ordinal());
		}
		
		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append("Switch "); 
			sb.append(offName);
			sb.append(' ');
			sb.append(onName);
			sb.append(' ');
			sb.append(game.name());
			return sb.toString();
		}
		
	}
	
}
