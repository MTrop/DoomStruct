/*******************************************************************************
 * Copyright (c) 2015-2019 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.struct;

import java.util.Random;

import net.mtrop.doom.util.MathUtils;
import net.mtrop.doom.util.RangeUtils;

/**
 * An abstract representation of a WaveForm.
 * Internally, it is a series of discrete values clamped within a 
 * specific amplitude range. It can be used for interpolating via
 * getSample(), or a specific discrete sample can be queried with
 * getSampleValue().
 * <p> Since this wave has no direct application, this can be 
 * used for any kind of sampling and wave generation purposes.
 * <p> The method getSample() wraps the <code>time</code> value.
 * <p> NOTE: This class is able to perform bulk operations to its 
 * underlying data, so it is not threadsafe by any means.
 * @author Matthew Tropiano
 */
public class CustomWaveForm
{
	public static enum InterpolationType
	{
		/** Use no interpolation. */
		NONE,
		/** Use linear interpolation. */
		LINEAR,
		/** Use cosine interpolation. */
		COSINE,
		/** Use cubic interpolation. */
		CUBIC;
	}
	
	/** Array of samples (values). */
	protected double[] samples;
	/** Interpolation method to use between discrete samples. */
	protected InterpolationType interpolationType;
	
	/** Wave amplitude. */
	protected double amplitude;
	/** Incremental value of the time period between each sample. */
	protected double sampleIncrement;

	/**
	 * Creates a new abstract waveform object.
	 * @param sampleValues the list of samples. 
	 */
	public CustomWaveForm(double[] sampleValues)
	{
		this(0.0, sampleValues, InterpolationType.LINEAR);
	}
	
	/**
	 * Creates a new abstract waveform object with zero sample data.
	 * @param sampleCount the number of discrete samples. 
	 */
	public CustomWaveForm(int sampleCount)
	{
		this(0.0, sampleCount, InterpolationType.LINEAR);
	}
	
	/**
	 * Creates a new abstract waveform object.
	 * @param sampleValues the list of samples. 
	 * @param interpolationType the type of interpolation to use for sampling values between the discrete samples.
	 */
	public CustomWaveForm(double[] sampleValues, InterpolationType interpolationType)
	{
		this(0.0, sampleValues, interpolationType);
	}
	
	/**
	 * Creates a new abstract waveform object with zero sample data.
	 * @param sampleCount the number of discrete samples. 
	 * @param interpolationType the type of interpolation to use for sampling values between the discrete samples.
	 */
	public CustomWaveForm(int sampleCount, InterpolationType interpolationType)
	{
		this(0.0, sampleCount, interpolationType);
	}
	
	/**
	 * Creates a new abstract waveform object.
	 * @param amplitude the desired wave amplitude. 
	 * Note that this is only used when a wave is adjusted by amplitude or when getNormalizedSample() is called.
	 * It will be adjusted in this constructor should the source data contain a sample larger in magnitude
	 * than the supplied amplitude value.
	 * @param sampleValues the list of samples. 
	 * @param interpolationType the type of interpolation to use for sampling values between the discrete samples.
	 */
	public CustomWaveForm(double amplitude, double[] sampleValues, InterpolationType interpolationType)
	{
		this.amplitude = amplitude;
		samples = new double[sampleValues.length];
		sampleIncrement = 1.0 / sampleValues.length;
		this.interpolationType = interpolationType;
		for (int i = 0; i < samples.length; i++)
		{
			samples[i] = sampleValues[i];
			amplitude = Math.max(amplitude, Math.abs(sampleValues[i]));
		}
	}
	
	/**
	 * Creates a new abstract waveform object with zero sample data.
	 * @param amplitude the desired wave amplitude. 
	 * Note that this is only used when a wave is adjusted by amplitude or when getNormalizedSample() is called.
	 * It will be adjusted in this constructor should the source data contain a sample larger in magnitude
	 * than the supplied amplitude value.
	 * @param sampleCount the number of discrete samples. 
	 * @param interpolationType the type of interpolation to use for sampling values between the discrete samples.
	 */
	public CustomWaveForm(double amplitude, int sampleCount, InterpolationType interpolationType)
	{
		this.amplitude = amplitude;
		samples = new double[sampleCount];
		this.interpolationType = interpolationType;
		sampleIncrement = 1.0 / sampleCount;
	}
	
	/**
	 * Creates a new randomized abstract waveform object.
	 * @param amplitude the desired wave amplitude. This is used to select the random range.
	 * @param random the random seeder to use for generating the random data.
	 * @param sampleCount the number of discrete samples. 
	 * @param interpolationType the type of interpolation to use for sampling values between the discrete samples.
	 */
	public CustomWaveForm(Random random, double amplitude, int sampleCount, InterpolationType interpolationType)
	{
		this.amplitude = amplitude;
		samples = new double[sampleCount];
		sampleIncrement = 1.0 / samples.length;
		this.interpolationType = interpolationType;
		for (int i = 0; i < samples.length; i++)
			samples[i] = MathUtils.randDoubleN(random) * amplitude;
	}
	
	/**
	 * Changes the contents of this waveform to its derivative values, 
	 * which are the differences between all of the samples in the wave.
	 */
	public void deriveInline()
	{
		double[] newSamples = new double[samples.length];
		for (int i = 0; i < samples.length; i++)
		{
			if (i == 0)
				newSamples[i] = samples[i] - samples[samples.length-1];
			else
				newSamples[i] = samples[i] - samples[i-1];
		}
		samples = newSamples;
	}
	
	/**
	 * Resamples this waveform to a new set of discrete samples,
	 * using the current interpolation type.
	 * @param newSampleCount the new count of discrete samples.
	 */
	public void resampleInline(int newSampleCount)
	{
		double[] newSamples = new double[newSampleCount];
		double sinc = 1.0 / newSampleCount;
		for (int i = 0; i < newSampleCount; i++)
			newSamples[i] = getSample(i*sinc);
		samples = newSamples;
		sampleIncrement = sinc;
	}

	/**
	 * Scales all of the values in this waveform, along with its current
	 * amplitude, changing all of the sample values within, but overall
	 * keeping the normalized samples the same (since the amplitude is
	 * changed by the same factor).
	 * @param value the value to scale each sample to.
	 */
	public void scaleAmplitudeInline(double value)
	{
		amplitude *= value;
		for (int i = 0; i < samples.length; i++)
			samples[i] *= value;
	}
	
	/**
	 * Adds a a value to all samples in this waveform, potentially replacing
	 * all of the existing sample values in the wave. The resultant sample 
	 * values will be clamped into this wave's amplitude. This does NOT alter 
	 * the current amplitude.
	 * @param value the sample value to add to each sample.
	 */
	public void modulateInline(double value)
	{
		double amp = getAmplitude();
		for (int i = 0; i < samples.length; i++)
			samples[i] = RangeUtils.clampValue(samples[i]*value, -amp, amp);
	}
	
	/**
	 * Adds a a value to all samples in this waveform, potentially replacing
	 * all of the existing sample values in the wave. The resultant sample 
	 * values will be clamped into this wave's amplitude.
	 * @param value the sample value to add to each sample.
	 */
	public void addInline(double value)
	{
		double amp = getAmplitude();
		for (int i = 0; i < samples.length; i++)
			samples[i] = RangeUtils.clampValue(samples[i]+value, -amp, amp);
	}
	
	/**
	 * Returns the deriviative of this waveform, which is the difference 
	 * between all samples in the wave. A wave is returned reflecting this
	 * change, leaving the original untouched.
	 * @return a new waveform.
	 */
	public CustomWaveForm derive()
	{
		CustomWaveForm out = copy();
		out.deriveInline();
		return out;
	}
	
	/**
	 * Resamples this waveform to a new set of discrete samples,
	 * returning a brand new instance with a new set of discrete
	 * sample values, using the current interpolation type.
	 * @param newSampleCount the new count of discrete samples.
	 * @return a new waveform.
	 */
	public CustomWaveForm resample(int newSampleCount)
	{
		CustomWaveForm out = copy();
		out.resampleInline(newSampleCount);
		return out;
	}

	/**
	 * Scales all of the values in this waveform, along with its current
	 * amplitude, changing all of the sample values within, but overall
	 * keeping the normalized samples the same (since the amplitude is
	 * changed by the same factor). A wave is returned reflecting this
	 * change, leaving the original untouched.
	 * @param value the value to scale each sample to.
	 * @return a new waveform.
	 */
	public CustomWaveForm scaleAmplitude(double value)
	{
		CustomWaveForm out = copy();
		out.scaleAmplitudeInline(value);
		return out;
	}
	
	/**
	 * Multiplies a waveform to this waveform and returns the contents as a new 
	 * wave. The number of samples and interpolation type of the 
	 * resultant wave is dependent on the number of samples and the 
	 * interpolation type in this wave. The resultant sample values are clamped
	 * into this wave's amplitude.
	 * @param value the sample value to add to each sample.
	 * @return a new CustomWaveForm that is the source wave plus this one.
	 */
	public CustomWaveForm modulate(double value)
	{
		CustomWaveForm out = copy();
		out.modulateInline(value);
		return out;
	}
	
	/**
	 * Adds a a value to all samples in this waveform and returns the contents 
	 * as a new wave. The number of samples and interpolation type of the 
	 * resultant wave is dependent on the number of samples and the 
	 * interpolation type in this wave. The resultant sample values are clamped
	 * into this wave's amplitude.
	 * @param value the sample value to add to each sample.
	 * @return a new CustomWaveFrom that is the source wave plus this one.
	 */
	public CustomWaveForm add(double value)
	{
		CustomWaveForm out = copy();
		out.addInline(value);
		return out;
	}
	
	/**
	 * Produces a deep copy of this wave, sample values and all.
	 * @return a new, copied waveform.
	 */
	public CustomWaveForm copy()
	{
		return new CustomWaveForm(samples, interpolationType);
	}
	
	/**
	 * Sets the normalized amplitude of this waveform.
	 * @param amplitude the amplitude value.
	 */
	public void setAmplitude(double amplitude)
	{
		this.amplitude = amplitude;
	}
	
	public double getAmplitude()
	{
		return amplitude;
	}

	public double getSample(double time)
	{
		time = RangeUtils.wrapValue(time, 0.0, 1.0);
		double spos = time / sampleIncrement;
		double v1 = samples[(int)Math.floor(spos)];
		switch (interpolationType)
		{
			default:
			case NONE:
				return v1;
			case LINEAR:
			{
				double v2 = samples[RangeUtils.wrapValue((int)Math.ceil(spos), 0, samples.length)];
				double interp = (time % sampleIncrement) / sampleIncrement;
				return MathUtils.linearInterpolate(interp, v1, v2);
			}
			case COSINE:
			{
				double v2 = samples[RangeUtils.wrapValue((int)Math.ceil(spos), 0, samples.length)];
				double interp = (time % sampleIncrement) / sampleIncrement;
				return MathUtils.cosineInterpolate(interp, v1, v2);
			}
			case CUBIC:
			{
				double v2 = samples[RangeUtils.wrapValue((int)Math.ceil(spos), 0, samples.length)];
				double v0 = samples[RangeUtils.wrapValue((int)Math.floor(spos - 1.0), 0, samples.length)];
				double v3 = samples[RangeUtils.wrapValue((int)Math.ceil(spos + 1.0), 0, samples.length)];
				double interp = (time % sampleIncrement) / sampleIncrement;
				return MathUtils.cubicInterpolate(interp, v0, v1, v2, v3);
			}
		}
	}
	
	/**
	 * @return the amount of discrete samples in the wave.
	 */
	public int getSampleCount()
	{
		return samples.length;
	}
	
	/**
	 * Returns a value on the wave at a particular part of the wave's period,
	 * like getSample(), however this method will return a value normalized 
	 * and clamped to the interval [-1,1] according to the wave's amplitude.
	 * @param time a value from 0 to 1, describing a position along the period (0 = beginning, 1 = end).
	 * Depending on the WaveForm, this may wrap the input value around the interval [0,1] or clamp it.
	 * @return a value within the wave's amplitude.
	 */
	public double getNormalizedSample(double time)
	{
		return getSample(time) / getAmplitude();
	}
	
	/**
	 * Gets the actual sample value at a particular sample point. 
	 * @param sampleIndex the index of the sample to get.
	 * @return the sample value.
	 */
	public double getSampleValue(int sampleIndex)
	{
		return samples[sampleIndex];
	}
	
	/**
	 * Sets the actual sample value at a particular sample point.
	 * @param sampleIndex the index of the sample to set.
	 * @param value the value to set at the chosen index.
	 */
	public void setSampleValue(int sampleIndex, double value)
	{
		samples[sampleIndex] = value;
	}

	/**
	 * @return the interpolation type for this wave. 
	 */
	public InterpolationType getInterpolationType()
	{
		return interpolationType;
	}

	/**
	 * Sets the interpolation type for this wave. 
	 * @param interpolationType the interpolation type to set.
	 */
	public void setInterpolationType(InterpolationType interpolationType)
	{
		this.interpolationType = interpolationType;
	}
	
}
