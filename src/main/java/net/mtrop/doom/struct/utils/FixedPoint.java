/*******************************************************************************
 * Copyright (c) 2023 Black Rook Software
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package net.mtrop.doom.struct.utils;

/**
 * Fixed-point arithmetic functions.
 * These are inline functions that do not create new objects for each execution.
 * @author Matthew Tropiano
 */
public class FixedPoint 
{
	private FixedPoint() {} // Don't instantiate.
	
	/**
	 * Converts a floating-point number to a 16.16 fixed point number.
	 * Due to the nature of the conversion, this will result in a loss of precision -
	 * the maximum that the float-point value can be is 65535, roughly the size of a short.
	 * @param value the input value.
	 * @return the fixed-point value of the floating-point value.
	 */
	public static int floatToFixed1616(float value)
	{
		return (int)(value * 65536f);
	}
	
	/**
	 * Converts a fixed-point 16.16 number to a floating-point number.
	 * @param fixed1616 the fixed-point value.
	 * @return the floating-point number.
	 */
	public static float fixed1616ToFloat(int fixed1616)
	{
		float whole = (float)(fixed1616 >> 16);
		float mantissa = (fixed1616 & 0x0ffff) / 65536f;
		return mantissa + whole;
	}
	
	/**
	 * Adds a 16.16 fixed-point number to another 16.16 fixed point number.
	 * @param fixed1616a the first number.
	 * @param fixed1616b the second number.
	 * @return the result in 16.16 fixed-point.
	 */
	public static int add1616(int fixed1616a, int fixed1616b)
	{
		return fixed1616a + fixed1616b;
	}
	
	/**
	 * Subtracts a 16.16 fixed-point number from another 16.16 fixed point number.
	 * @param fixed1616a the first number.
	 * @param fixed1616b the second number.
	 * @return the result in 16.16 fixed-point.
	 */
	public static int sub1616(int fixed1616a, int fixed1616b)
	{
		return fixed1616a - fixed1616b;
	}
	
	/**
	 * Multiplies a 16.16 fixed-point number by another 16.16 fixed point number.
	 * @param fixed1616a the first number.
	 * @param fixed1616b the second number.
	 * @return the result in 16.16 fixed-point.
	 */
	public static int mul1616(int fixed1616a, int fixed1616b)
	{
		long mul = (long)fixed1616a * (long)fixed1616b;
		return (int)((mul >> 16) & 0x0ffffffffL);
	}
	
}
