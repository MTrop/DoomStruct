/*******************************************************************************
 * Copyright (c) 2019 Black Rook Software
 * 
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package net.mtrop.doom.struct;

import java.util.Random;

import net.mtrop.doom.util.RangeUtils;

/**
 * Math utils.
 * @author Matthew Tropiano
 */
public final class MathUtils
{
	/**
	 * Checks if bits are set in a value.
	 * @param value		the value.
	 * @param test		the testing bits.
	 * @return			true if all of the bits set in test are set in value, false otherwise.
	 */
	public static boolean bitIsSet(long value, long test)
	{
		return (value & test) == test;
	}

	/**
	 * Returns a random boolean.
	 * @param rand the random number generator.
	 * @return true or false.
	 */
	public static boolean randBoolean(Random rand)
	{
	    return rand.nextBoolean();
	}

	/**
	 * @param rand the random number generator.
	 * @return a random double value from [0 to 1) (inclusive/exclusive).
	 */
	public static double randDouble(Random rand)
	{
	    return rand.nextDouble();
	}

	/**
	 * Returns a random double value from -1 to 1 (inclusive).
	 * @param rand the random number generator.
	 * @return the next double.
	 */
	public static double randDoubleN(Random rand)
	{
	    return randDouble(rand) * (randBoolean(rand)? -1.0 : 1.0);
	}

	/**
	 * Gets a scalar factor that equals how "far along" a value is along an interval.
	 * @param value the value to test.
	 * @param lo the lower value of the interval.
	 * @param hi the higher value of the interval.
	 * @return a value between 0 and 1 describing this distance 
	 * 		(0 = beginning or less, 1 = end or greater), or 0 if lo and hi are equal.
	 */
	public static double getInterpolationFactor(double value, double lo, double hi)
	{
		if (lo == hi)
			return 0.0;
		return RangeUtils.clampValue((value - lo) / (hi - lo), 0, 1);
	}

	/**
	 * Gives a value that is the result of a linear interpolation between two values.
	 * @param factor the interpolation factor.
	 * @param x the first value.
	 * @param y the second value.
	 * @return the interpolated value.
	 */
	public static double linearInterpolate(double factor, double x, double y)
	{
		return factor * (y - x) + x;
	}

	/**
	 * Gives a value that is the result of a cosine interpolation between two values.
	 * @param factor the interpolation factor.
	 * @param x the first value.
	 * @param y the second value.
	 * @return the interpolated value.
	 */
	public static double cosineInterpolate(double factor, double x, double y)
	{
		double ft = factor * Math.PI;
		double f = (1 - Math.cos(ft)) * .5;
		return f * (y - x) + x;
	}

	/**
	 * Gives a value that is the result of a cublic interpolation between two values.
	 * Requires two outside values to predict a curve more accurately.
	 * @param factor the interpolation factor between x and y.
	 * @param w the value before the first.
	 * @param x the first value.
	 * @param y the second value.
	 * @param z the value after the second.
	 * @return the interpolated value.
	 */
	public static double cubicInterpolate(double factor, double w, double x, double y, double z)
	{
		double p = (z - y) - (w - x);
		double q = (w - x) - p;
		double r = y - w;
		double s = x;
		return (p*factor*factor*factor) + (q*factor*factor) + (r*factor) + s;
	}

}
