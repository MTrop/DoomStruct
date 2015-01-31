package net.mtrop.doom.map.binary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.blackrook.commons.Common;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

import net.mtrop.doom.exception.DataExportException;
import net.mtrop.doom.map.BinaryMapObject;
import net.mtrop.doom.map.Linedef;
import net.mtrop.doom.map.MapObject;
import net.mtrop.doom.util.RangeUtils;

/**
 * Doom/Boom 14-byte format implementation of Linedef.
 * @author Matthew Tropiano
 */
public class DoomLinedef implements BinaryMapObject, Linedef 
{

	/** Vertex start. */
	private int vertexStartIndex;
	/** Vertex end. */
	private int vertexEndIndex;

	/** Front sidedef. */
	private int sidedefFrontIndex;
	/** Back sidedef. */
	private int sidedefBackIndex;

	/** Linedef special. */
	private int special;
	/** Linedef special tag. */
	private int tag;

	/** Flag: Creatures (players and monsters) cannot pass. */
	private boolean impassable;
	/** Flag: Monsters cannot pass. */
	private boolean monsterBlocking;
	/** Flag: Line has two sides: projectiles/hitscans can pass. */
	private boolean twoSided;
	/** Flag: Line's upper texture is drawn from top to bottom. */
	private boolean upperUnpegged;
	/** Flag: Line's lower texture is drawn from bottom to top. */
	private boolean lowerUnpegged;
	/** Flag: Line's drawn like one-sided impassable ones on automap. */
	private boolean secret;
	/** Flag: Line's immediately drawn on automap. */
	private boolean mapped;
	/** Flag: Line's NEVER drawn on automap. */
	private boolean notDrawn;
	/** Flag: Line blocks sound propagation. */
	private boolean soundBlocking;
	/** Flag: Line passes activation through to other lines. */
	private boolean passThru;

	/**
	 * Creates a new linedef with default values set.
	 * @see #reset()
	 */
	public DoomLinedef()
	{
		reset();
	}

	/**
	 * Reads and creates a new DoomLinedef from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for a {@link DoomLinedef} are read.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @return a new DoomLinedef with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static DoomLinedef read(InputStream in) throws IOException
	{
		DoomLinedef out = new DoomLinedef();
		out.readBytes(in);
		return out;
	}
	
	/**
	 * Resets this linedef's properties to defaults.
	 * <ul>
	 * <li>All references/indices are {@link MapObject#NULL_REFERENCE}.</li>
	 * <li>All flags are <code>false</code> except for IMPASSABLE.</li>
	 * <li>Special and Tag are set to 0.</li>
	 * </ul>
	 */
	public void reset()
	{
		vertexStartIndex = NULL_REFERENCE;
		vertexEndIndex = NULL_REFERENCE;
		sidedefFrontIndex = NULL_REFERENCE;
		sidedefBackIndex = NULL_REFERENCE;
		special = 0;
		tag = 0;
		impassable = true;
		monsterBlocking = false;
		twoSided = false;
		upperUnpegged = false;
		lowerUnpegged = false;
		secret = false;
		mapped = false;
		notDrawn = false;
		soundBlocking = false;
		passThru = false;
	}
	
	@Override
	public int getVertexStartIndex()
	{
		return vertexStartIndex;
	}

	public void setVertexStartIndex(int vertexStartIndex)
	{
		this.vertexStartIndex = vertexStartIndex;
	}

	@Override
	public int getVertexEndIndex()
	{
		return vertexEndIndex;
	}

	public void setVertexEndIndex(int vertexEndIndex)
	{
		this.vertexEndIndex = vertexEndIndex;
	}

	@Override
	public int getSidedefFrontIndex()
	{
		return sidedefFrontIndex;
	}

	public void setSidedefFrontIndex(int sidedefFrontIndex)
	{
		this.sidedefFrontIndex = sidedefFrontIndex;
	}

	@Override
	public int getSidedefBackIndex()
	{
		return sidedefBackIndex;
	}

	public void setSidedefBackIndex(int sidedefBackIndex)
	{
		this.sidedefBackIndex = sidedefBackIndex;
	}

	@Override
	public int getSpecial()
	{
		return special;
	}

	public void setSpecial(int special)
	{
		this.special = special;
	}

	/**
	 * @return this linedef's special tag.
	 */
	public int getTag()
	{
		return tag;
	}

	/**
	 * Sets this linedef's special tag.
	 */
	public void setTag(int tag)
	{
		this.tag = tag;
	}

	/**
	 * @return true if this blocks - at the very least - player and monster movement, false if not.
	 */
	public boolean isImpassable()
	{
		return impassable;
	}

	public void setImpassable(boolean impassable)
	{
		this.impassable = impassable;
	}

	/**
	 * @return true if this line blocks monsters, false if not.
	 */
	public boolean isMonsterBlocking()
	{
		return monsterBlocking;
	}

	public void setMonsterBlocking(boolean monsterBlocking)
	{
		this.monsterBlocking = monsterBlocking;
	}

	/**
	 * @return true if this line is two-sided, false if not.
	 */
	public boolean isTwoSided()
	{
		return twoSided;
	}

	public void setTwoSided(boolean twoSided)
	{
		this.twoSided = twoSided;
	}

	/**
	 * @return true if this line's upper texture is unpegged, false if not.
	 */
	public boolean isUpperUnpegged()
	{
		return upperUnpegged;
	}

	public void setUpperUnpegged(boolean upperUnpegged)
	{
		this.upperUnpegged = upperUnpegged;
	}

	/**
	 * @return true if this line's lower texture is unpegged, false if not.
	 */
	public boolean isLowerUnpegged()
	{
		return lowerUnpegged;
	}

	public void setLowerUnpegged(boolean lowerUnpegged)
	{
		this.lowerUnpegged = lowerUnpegged;
	}

	/**
	 * @return true if this line is shown as one-sided on the automap, false if not.
	 */
	public boolean isSecret()
	{
		return secret;
	}

	public void setSecret(boolean secret)
	{
		this.secret = secret;
	}

	/**
	 * @return true if this line is always drawn on the automap, false if not.
	 */
	public boolean isMapped()
	{
		return mapped;
	}

	public void setMapped(boolean mapped)
	{
		this.mapped = mapped;
	}

	/**
	 * @return true if this line is not drawn on the automap, false if so.
	 */
	public boolean isNotDrawn()
	{
		return notDrawn;
	}

	public void setNotDrawn(boolean notDrawn)
	{
		this.notDrawn = notDrawn;
	}

	/**
	 * @return true if this line blocks sound (must be doubled-up to block sound completely), false if not.
	 */
	public boolean isSoundBlocking()
	{
		return soundBlocking;
	}

	public void setSoundBlocking(boolean soundBlocking)
	{
		this.soundBlocking = soundBlocking;
	}

	/**
	 * @return true if this line's activated special does not block the activation search, false if so.
	 */
	public boolean isPassThru()
	{
		return passThru;
	}

	public void setPassThru(boolean passThru)
	{
		this.passThru = passThru;
	}

	@Override
	public void fromBytes(byte[] data) throws IOException
	{
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		readBytes(bin);
		Common.close(bin);
	}

	@Override
	public byte[] getBytes() throws DataExportException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try { writeBytes(bos); } catch (IOException e) { /* Shouldn't happen. */ }
		return bos.toByteArray();
	}

	@Override
	public void readBytes(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in, SuperReader.LITTLE_ENDIAN);
		
		vertexStartIndex = sr.readUnsignedShort();
		vertexEndIndex = sr.readUnsignedShort();
		
		// bitflags
		int flags = sr.readUnsignedShort();
		impassable = Common.bitIsSet(flags, (1 << 0));
		monsterBlocking = Common.bitIsSet(flags, (1 << 1));
		twoSided = Common.bitIsSet(flags, (1 << 2));
		upperUnpegged = Common.bitIsSet(flags, (1 << 3));
		lowerUnpegged = Common.bitIsSet(flags, (1 << 4));
		secret = Common.bitIsSet(flags, (1 << 5));
		soundBlocking = Common.bitIsSet(flags, (1 << 6));
		notDrawn = Common.bitIsSet(flags, (1 << 7));
		mapped = Common.bitIsSet(flags, (1 << 8));
		passThru = Common.bitIsSet(flags, (1 << 9));
		
		special = sr.readUnsignedShort();
		tag = sr.readUnsignedShort();
		sidedefFrontIndex = sr.readShort();
		sidedefBackIndex = sr.readShort();
	}

	@Override
	public void writeBytes(OutputStream out) throws DataExportException, IOException 
	{
		RangeUtils.checkShortUnsigned("Vertex Start Index", vertexStartIndex);
		RangeUtils.checkShortUnsigned("Vertex End Index", vertexEndIndex);
		RangeUtils.checkShortUnsigned("Special", special);
		RangeUtils.checkShortUnsigned("Tag", tag);
		RangeUtils.checkRange("Sidedef Front Index", -1, Short.MAX_VALUE, sidedefFrontIndex);
		RangeUtils.checkRange("Sidedef Back Index", -1, Short.MAX_VALUE, sidedefBackIndex);

		SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);
		sw.writeUnsignedShort(vertexStartIndex);
		sw.writeUnsignedShort(vertexEndIndex);
		
		sw.writeUnsignedShort(Common.booleansToInt(
			impassable,
			monsterBlocking,
			twoSided,
			upperUnpegged,
			lowerUnpegged,
			secret,
			soundBlocking,
			notDrawn,
			mapped,
			passThru
		));
		
		sw.writeUnsignedShort(special);
		sw.writeUnsignedShort(tag);
		sw.writeShort((short)sidedefFrontIndex);
		sw.writeShort((short)sidedefBackIndex);
	}
	
}
