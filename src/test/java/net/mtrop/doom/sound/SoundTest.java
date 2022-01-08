/*******************************************************************************
 * Copyright (c) 2015-2022 Matt Tropiano
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.sound;

import java.io.IOException;

import net.mtrop.doom.WadFile;
import net.mtrop.doom.sound.DMXSound;
import net.mtrop.doom.sound.DMXSound.InterpolationType;


public final class SoundTest
{
	public static void main(String[] args) throws IOException
	{
		WadFile wad = new WadFile(args[0]);
		DMXSound sound = wad.getDataAs("DSRLAUNC", DMXSound.class)
				.resample(InterpolationType.CUBIC, DMXSound.SAMPLERATE_22KHZ);
		for (int i = 0 ; i < sound.getSampleCount(); i++)
			System.out.println(sound.getSample(i));
		wad.close();
	}
}
