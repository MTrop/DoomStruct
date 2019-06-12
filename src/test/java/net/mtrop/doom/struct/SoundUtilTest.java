/*******************************************************************************
 * Copyright (c) 2015-2019 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.struct;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.UnsupportedAudioFileException;

import net.mtrop.doom.sound.DMXSound;
import net.mtrop.doom.util.SoundUtils;

public final class SoundUtilTest
{
	public static void main(String[] args) throws IOException, UnsupportedAudioFileException
	{
		DMXSound sound = SoundUtils.createSound(new File(args[0]));
		sound.writeBytes(new FileOutputStream(new File("DSJUNK.dmx")));
		SoundUtils.writeSoundToFile(sound, Type.WAVE, new File("DSJUNK.wav"));
	}
}
