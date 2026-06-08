package net.mtrop.doom.bsp.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.mtrop.doom.object.BinaryObject;
import net.mtrop.doom.struct.io.SerialReader;
import net.mtrop.doom.struct.io.SerialWriter;
import net.mtrop.doom.util.RangeUtils;

/**
 * A single DeepBSP v4 Segment.
 * @author Matthew Tropiano
 * @since [NOW]
 */
public class DeepBSPSegment implements BinaryObject 
{
	/** Byte length of this object. */
	public static final int LENGTH = 16;

	/** Direction along linedef (same). */
	public final static boolean DIRECTION_SAME_AS_LINEDEF = false;
	/** Direction along linedef (opposite). */
	public final static boolean DIRECTION_OPPOSITE_LINEDEF = true;

	/** Binary angle. */
	public final static int ANGLE_EAST = 0;
	/** Binary angle. */
	public final static int ANGLE_NORTH = 16384;
	/** Binary angle. */
	public final static int ANGLE_WEST = 32768;
	/** Binary angle. */
	public final static int ANGLE_SOUTH = 49152;

	/** This Seg's start vertex index reference. */
	protected int vertexStartIndex;
	/** This Seg's end vertex index reference. */
	protected int vertexEndIndex;
	
	/** This Seg's angle. */
	protected int angle;
	/** This Seg's linedef index. */
	protected int linedefIndex;
	/** If this Seg's direction is flipped. */
	protected boolean flipDirection;
	/** This Seg's offset along linedef. */
	protected int offset;

	/**
	 * Creates a new DeepBSP Segment.
	 */
	public DeepBSPSegment()
	{
		this.vertexStartIndex = -1;
		this.vertexEndIndex = -1;
		this.angle = 0;
		this.linedefIndex = -1;
		this.flipDirection = false;
		this.offset = 0;
	}

	/** 
	 * @return this Seg's start vertex index reference. 
	 */
	public int getVertexStartIndex()
	{
		return vertexStartIndex;
	}

	/** 
	 * Sets this Seg's start vertex index reference. 
	 * @param vertexStartIndex the index.
	 */
	public void setVertexStartIndex(int vertexStartIndex)
	{
		this.vertexStartIndex = vertexStartIndex;
	}

	/** 
	 * @return this Seg's ending vertex index reference. 
	 */
	public int getVertexEndIndex()
	{
		return vertexEndIndex;
	}

	public void setVertexEndIndex(int vertexEndIndex)
	{
		this.vertexEndIndex = vertexEndIndex;
	}

	public int getAngle() 
	{
		return angle;
	}

	/** 
	 * Sets this Seg's short angle reference. 
	 * @param angle the binary angle (unsigned).
	 * @throws IllegalArgumentException if the angle is out of range (0 to 65535).
	 */
	public void setAngle(int angle)
	{
		RangeUtils.checkShortUnsigned("Angle", angle);
		this.angle = angle;
	}

	/** 
	 * @return this Seg's linedef index reference. 
	 */
	public int getLinedefIndex()
	{
		return linedefIndex;
	}

	/** 
	 * Sets this Seg's linedef index reference. 
	 * @param linedefIndex the linedef index (unsigned).
	 * @throws IllegalArgumentException if the index is out of range (0 to 65535).
	 */
	public void setLinedefIndex(int linedefIndex)
	{
		RangeUtils.checkShortUnsigned("Linedef index", linedefIndex);
		this.linedefIndex = linedefIndex;
	}

	/**
	 * @return true if this seg's direction is flipped.
	 */
	public boolean isDirectionFlipped() 
	{
		return flipDirection;
	}

	/**
	 * Check if this seg's direction is flipped.
	 * @param flipDirection true if so, false if not.
	 */
	public void setDirectionFlipped(boolean flipDirection)
	{
		this.flipDirection = flipDirection;
	}

	/** 
	 * @return Gets this Seg's offset.
	 */
	public int getOffset()
	{
		return offset;
	}

	/** 
	 * Sets this Seg's offset. 
	 * @param offset the offset along the seg.
	 * @throws IllegalArgumentException if the offset is out of range (0 to 65535).
	 */
	public void setOffset(int offset)
	{
		RangeUtils.checkShortUnsigned("Offset", offset);
		this.offset = offset;
	}

	@Override
	public void readBytes(InputStream in) throws IOException
	{
		SerialReader sr = new SerialReader(SerialReader.LITTLE_ENDIAN);
		vertexStartIndex = sr.readInt(in);
		vertexEndIndex = sr.readInt(in);
		angle = sr.readUnsignedShort(in);
		linedefIndex = sr.readUnsignedShort(in);
		flipDirection = sr.readUnsignedShort(in) != 0;
		offset = sr.readUnsignedShort(in);
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		SerialWriter sw = new SerialWriter(SerialWriter.LITTLE_ENDIAN);
		sw.writeInt(out, vertexStartIndex);
		sw.writeInt(out, vertexEndIndex);
		sw.writeUnsignedShort(out, angle);
		sw.writeUnsignedShort(out, linedefIndex);
		sw.writeUnsignedShort(out, flipDirection ? -1 : 0);
		sw.writeUnsignedShort(out, offset);
	}
	
}
