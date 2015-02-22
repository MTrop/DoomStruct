package net.mtrop.doom.map.binary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.blackrook.commons.Common;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

import net.mtrop.doom.BinaryObject;

/**
 * Doom/Boom 10-byte format implementation of Thing.
 * @author Matthew Tropiano
 */
public class DoomThing extends CommonThing implements BinaryObject
{
	/** Byte length of this object. */
	public static final int LENGTH = 10;
	
	/** Flag: Thing is not in Cooperative. */
	protected boolean notCooperative;
	/** Flag: Thing is not in Deathmatch. */
	protected boolean notDeathmatch;
	/** Flag: Thing is friendly. */
	protected boolean friendly;
	
	/**
	 * Creates a new thing.
	 */
	public DoomThing()
	{
	}

	/**
	 * Reads and creates a new DoomThing from an array of bytes.
	 * This reads from the first 10 bytes of the array.
	 * @param bytes the byte array to read.
	 * @return a new DoomThing with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static DoomThing create(byte[] bytes) throws IOException
	{
		DoomThing out = new DoomThing();
		out.fromBytes(bytes);
		return out;
	}
	
	/**
	 * Reads and creates a new DoomThing from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for a {@link DoomThing} are read.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @return a new DoomThing with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static DoomThing read(InputStream in) throws IOException
	{
		DoomThing out = new DoomThing();
		out.readBytes(in);
		return out;
	}
	
	/**
	 * Reads and creates new DoomThing from an array of bytes.
	 * This reads from the first 10 * <code>count</code> bytes of the array.
	 * @param bytes the byte array to read.
	 * @param count the amount of objects to read.
	 * @return an array of DoomThing objects with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static DoomThing[] create(byte[] bytes, int count) throws IOException
	{
		return read(new ByteArrayInputStream(bytes), count);
	}
	
	/**
	 * Reads and creates a new DoomThing from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for <code>count</code> {@link DoomThing}s are read.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @param count the amount of objects to read.
	 * @return an array of DoomThing objects with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static DoomThing[] read(InputStream in, int count) throws IOException
	{
		DoomThing[] out = new DoomThing[count];
		for (int i = 0; i < count; i++)
		{
			out[i] = new DoomThing();
			out[i].readBytes(in);
		}
		return out;
	}
	
	/**
	 * @return true if this does NOT appear on cooperative, false if not.
	 */
	public boolean isNotCooperative()
	{
		return notCooperative;
	}

	/**
	 * Sets if this does NOT appear on cooperative.
	 */
	public void setNotCooperative(boolean notCooperative)
	{
		this.notCooperative = notCooperative;
	}

	/**
	 * @return true if this does NOT appear on deathmatch, false if not.
	 */
	public boolean isNotDeathmatch()
	{
		return notDeathmatch;
	}

	/**
	 * Sets if this does NOT appear on deathmatch.
	 */
	public void setNotDeathmatch(boolean notDeathmatch)
	{
		this.notDeathmatch = notDeathmatch;
	}

	/**
	 * @return true if this is flagged as friendly, false if not.
	 */
	public boolean isFriendly()
	{
		return friendly;
	}

	/**
	 * Sets if this is flagged as friendly.
	 */
	public void setFriendly(boolean friendly)
	{
		this.friendly = friendly;
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
		SuperReader sr = new SuperReader(in, SuperReader.LITTLE_ENDIAN);
		x = sr.readShort();
		y = sr.readShort();
		angle = sr.readUnsignedShort();
		type = sr.readUnsignedShort();
		
		// bitflags
		int flags = sr.readUnsignedShort();
		easy = Common.bitIsSet(flags, (1 << 0));
		medium = Common.bitIsSet(flags, (1 << 1));
		hard = Common.bitIsSet(flags, (1 << 2));
		ambush = Common.bitIsSet(flags, (1 << 3));
		notSinglePlayer = Common.bitIsSet(flags, (1 << 4));
		notDeathmatch = Common.bitIsSet(flags, (1 << 5));
		notCooperative = Common.bitIsSet(flags, (1 << 6));
		friendly = Common.bitIsSet(flags, (1 << 7));
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);
		sw.writeShort((short)x);
		sw.writeShort((short)y);
		sw.writeShort((short)angle);
		sw.writeShort((short)type);
		
		sw.writeUnsignedShort(Common.booleansToInt(
			easy,
			medium,
			hard,
			ambush,
			notSinglePlayer,
			notDeathmatch,
			notCooperative,
			friendly
		));		
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Thing");
		sb.append(" (").append(x).append(", ").append(y).append(")");
		sb.append(" Type:").append(type);
		sb.append(" Angle:").append(angle);

		if (easy) sb.append(' ').append("EASY");
		if (medium) sb.append(' ').append("MEDIUM");
		if (hard) sb.append(' ').append("HARD");
		if (ambush) sb.append(' ').append("AMBUSH");
		if (notSinglePlayer) sb.append(' ').append("NOTSINGLEPLAYER");
		if (notDeathmatch) sb.append(' ').append("NOTDEATHMATCH");
		if (notCooperative) sb.append(' ').append("NOTCOOPERATIVE");
		if (friendly) sb.append(' ').append("FRIENDLY");
		
		return sb.toString();
	}

}
