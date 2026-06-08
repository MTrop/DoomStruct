package net.mtrop.doom.bsp.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.mtrop.doom.object.BinaryObject;
import net.mtrop.doom.struct.io.SerialReader;
import net.mtrop.doom.struct.io.SerialWriter;
import net.mtrop.doom.util.RangeUtils;

/**
 * The DeepBSP v4 Subsector type.
 * @author Matthew Tropiano
 * @since [NOW]
 */
public class DeepBSPSubsector implements BinaryObject
{
	/** Byte length of this object. */
	public static final int LENGTH = 6;

	/** This Subsector's BSP Segment count. */
	protected int segCount;
	/** This Subsector's starting segment index. */
	protected int segStartIndex;

	/**
	 * Creates a new DeepBSP Subsector.
	 */
	public DeepBSPSubsector()
	{
		segCount = 0;
		segStartIndex = 0;
	}
	
	/**
	 * @return the amount of segments in this subsector.
	 */
	public int getSegCount()
	{
		return segCount;
	}

	/**
	 * Sets the amount of segments in this subsector.
	 * @param segCount the amount.
	 * @throws IllegalArgumentException if the count is above 65535.
	 */
	public void setSegCount(int segCount)
	{
		RangeUtils.checkShortUnsigned("Seg count", segCount);
		this.segCount = segCount;
	}

	/**
	 * @return the seg start index.
	 */
	public int getSegStartIndex() 
	{
		return segStartIndex;
	}

	/**
	 * Sets the segment start index.
	 * @param segStartIndex the segment start index.
	 */
	public void setSegStartIndex(int segStartIndex) 
	{
		this.segStartIndex = segStartIndex;
	}

	@Override
	public void readBytes(InputStream in) throws IOException
	{
		SerialReader sr = new SerialReader(SerialReader.LITTLE_ENDIAN);
		segCount = sr.readUnsignedShort(in);
		segStartIndex = sr.readInt(in);
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		SerialWriter sw = new SerialWriter(SerialWriter.LITTLE_ENDIAN);
		sw.writeUnsignedShort(out, segCount);
		sw.writeInt(out, segStartIndex);
	}

}
