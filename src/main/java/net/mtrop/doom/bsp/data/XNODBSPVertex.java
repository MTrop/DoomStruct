package net.mtrop.doom.bsp.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.mtrop.doom.object.BinaryObject;
import net.mtrop.doom.struct.io.SerialReader;
import net.mtrop.doom.struct.io.SerialWriter;
import net.mtrop.doom.struct.utils.FixedPoint;

/**
 * A single extended vertex for XNOD node format.
 * @author Matthew Tropiano
 */
public class XNODBSPVertex implements BinaryObject
{
	/** Byte length of this object. */
	public static final int LENGTH = 8;

	/** Fixed-point X-coordinate. */
	private int xFixed;
	/** Fixed-point Y-coordinate. */
	private int yFixed;
	/** Floating-point X-coordinate. */
	private float x;
	/** Floating-point Y-coordinate. */
	private float y;
	
	/**
	 * Creates a new vertex.
	 */
	public XNODBSPVertex()
	{
		xFixed = 0;
		yFixed = 0;
		x = 0f;
		y = 0f;
	}
	
	/**
	 * @return the X-coordinate as a 16.16 fixed-point value.
	 */
	public int getXFixed()
	{
		return xFixed;
	}

	/**
	 * Sets the X-coordinate as a 16.16 fixed-point value.
	 * @param xFixed the value.
	 */
	public void setXFixed(int xFixed)
	{
		this.xFixed = xFixed;
		x = FixedPoint.fixed1616ToFloat(xFixed);
	}

	/**
	 * @return the Y-coordinate as a 16.16 fixed-point value.
	 */
	public int getYFixed()
	{
		return yFixed;
	}

	/**
	 * Sets the Y-coordinate as a 16.16 fixed-point value.
	 * @param yFixed the value.
	 */
	public void setYFixed(int yFixed)
	{
		this.yFixed = yFixed;
		y = FixedPoint.fixed1616ToFloat(yFixed);
	}

	/**
	 * @return the X-coordinate as a floating-point value.
	 */
	public float getX()
	{
		return x;
	}

	/**
	 * Sets the X-coordinate as a floating-point value.
	 * @param x the value.
	 */
	public void setX(float x)
	{
		this.x = x;
		xFixed = FixedPoint.floatToFixed1616(x);
	}

	/**
	 * @return the Y-coordinate as a floating-point value.
	 */
	public float getY()
	{
		return y;
	}

	/**
	 * Sets the Y-coordinate as a floating-point value.
	 * @param y the value.
	 */
	public void setY(float y)
	{
		this.y = y;
		yFixed = FixedPoint.floatToFixed1616(y);
	}

	@Override
	public void readBytes(InputStream in) throws IOException
	{
		SerialReader sr = new SerialReader(SerialReader.LITTLE_ENDIAN);
		xFixed = sr.readInt(in);
		yFixed = sr.readInt(in);
		x = FixedPoint.fixed1616ToFloat(xFixed);
		y = FixedPoint.fixed1616ToFloat(yFixed);
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		SerialWriter sw = new SerialWriter(SerialWriter.LITTLE_ENDIAN);
		sw.writeInt(out, xFixed);
		sw.writeInt(out, yFixed);
	}

}
