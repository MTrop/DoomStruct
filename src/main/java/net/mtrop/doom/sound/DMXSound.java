/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.sound;

import java.io.*;

import net.mtrop.doom.BinaryObject;
import net.mtrop.doom.struct.CustomWaveForm;
import net.mtrop.doom.struct.CustomWaveForm.InterpolationType;
import net.mtrop.doom.util.RangeUtils;
import net.mtrop.doom.util.SerialReader;
import net.mtrop.doom.util.SerialWriter;
import net.mtrop.doom.util.Utils;

/**
 * This class holds digital sound information.
 * The format that this reads is the DMX PCM Format, by Digital Expressions, Inc.,
 * written by Paul Radek. Doom uses this format for storing sound data.
 * @author Matthew Tropiano
 */
public class DMXSound implements BinaryObject
{
	/** 8 kHz Sampling rate. */
	public static final int SAMPLERATE_8KHZ = 8000;
	/** 11 kHz Sampling rate. */
	public static final int SAMPLERATE_11KHZ = 11025;
	/** 22 kHz Sampling rate. */
	public static final int SAMPLERATE_22KHZ = 22050;
	/** 44 kHz Sampling rate. */
	public static final int SAMPLERATE_44KHZ = 44100;
	
	/** Sampling rate in Samples per Second. */
	private int sampleRate;
	/** Sound samples put in a native waveform. */
	private CustomWaveForm waveForm;
	
	/**
	* Creates a new, blank DMXSound.
	*/	
	public DMXSound()
	{
		sampleRate = SAMPLERATE_11KHZ;
		waveForm = new CustomWaveForm(new double[]{0.0});
	}

	/**
	 * Creates a new DMXSound using a set of discrete samples
	 * at a particular sampling rate.
	 * @param sampleRate the sampling rate of this sound in samples per second.
	 * @param samples the discrete samples.
	 */
	public DMXSound(int sampleRate, double[] samples)
	{
		this.sampleRate = sampleRate;
		waveForm = new CustomWaveForm(samples);
	}
	
	/**
	 * @return the sampling rate of this sound clip in samples per second.
	 */
	public int getSampleRate()
	{
		return sampleRate;
	}

	/**
	 * Sets the sampling rate of this sound clip in samples per second.
	 * This does NOT change the underlying waveform!
	 * @param sampleRate the new sampling rate.
	 * @throws IllegalArgumentException if the sample rate is outside the range of 0 to 65535.
	 */
	public void setSampleRate(int sampleRate)
	{
		RangeUtils.checkShortUnsigned("Sample Rate", sampleRate);
		this.sampleRate = sampleRate;
	}

	/**
	 * Changes the sampling rate of this sound clip,
	 * and resamples the underlying data as well.
	 * @param newSamplingRate the new sampling rate to use and resample to.
	 */
	public void resample(int newSamplingRate)
	{
		double change = (double)newSamplingRate / (double)sampleRate;
		this.sampleRate = newSamplingRate;
		waveForm.resampleInline((int)(waveForm.getSampleCount() * change));
	}
	
	/**
	 * Changes the sampling rate of this sound clip, and resamples the underlying data as well.
	 * @param interpolation the interpolation type to use.
	 * @param newSamplingRate the new sampling rate to use and resample to.
	 */
	public void resample(InterpolationType interpolation, int newSamplingRate)
	{
		waveForm.setInterpolationType(interpolation);
		resample(newSamplingRate);
	}
	
	/**
	 * @return the waveform that holds all of the samples in this sound clip.
	 * @see CustomWaveForm
	 */
	public CustomWaveForm getWaveForm()
	{
		return waveForm;
	}

	@Override
	public void readBytes(InputStream in) throws IOException
	{
		SerialReader sr = new SerialReader(SerialReader.LITTLE_ENDIAN);
		int type = sr.readUnsignedShort(in);
		if (type != 3)
			throw new IOException("Not a sound clip.");
		
		sampleRate = sr.readUnsignedShort(in);
		int sampleCount = (int)sr.readUnsignedInt(in);
		
		waveForm = new CustomWaveForm(sampleCount - 32);
		waveForm.setAmplitude(1.0);
		
		sr.readBytes(in, 16); // padding
		
		byte[] b = sr.readBytes(in, sampleCount - 32);
		for (int i = 0; i < b.length; i++)
			waveForm.setSampleValue(i, (Utils.getInterpolationFactor((b[i] & 0x0ff), 0, 255) * 2.0) - 1.0);
		
		sr.readBytes(in, 16); // padding
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		final byte[] PADDING = new byte[]{
				0x7F, 0x7F, 0x7F, 0x7F,
				0x7F, 0x7F, 0x7F, 0x7F,
				0x7F, 0x7F, 0x7F, 0x7F,
				0x7F, 0x7F, 0x7F, 0x7F
			};
		SerialWriter sw = new SerialWriter(SerialWriter.LITTLE_ENDIAN);
		sw.writeUnsignedShort(out, 3); // format type
		sw.writeUnsignedShort(out, sampleRate);
		sw.writeUnsignedInteger(out, waveForm.getSampleCount() + 32);
		sw.writeBytes(out, PADDING);
		for (int i = 0; i < waveForm.getSampleCount(); i++)
			sw.writeUnsignedByte(out, (int)(((waveForm.getSampleValue(i) + 1.0) / 2.0) * 255.0));
		sw.writeBytes(out, PADDING);
	}

	@Override
	public String toString()
	{
		return String.format("DMXSound Sample Rate: %d Hz, %d Samples, 8-bit", sampleRate, waveForm.getSampleCount());
	}
	
}
