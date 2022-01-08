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
import net.mtrop.doom.sound.MUS;
import net.mtrop.doom.LoggingFactory;
import net.mtrop.doom.LoggingFactory.Logger;


public final class MUSTest
{
	public static void main(String[] args) throws IOException
	{
		Logger logger = LoggingFactory.createConsoleLoggerFor(MUSTest.class);
		WadFile wad = new WadFile(args[0]);
		MUS mus = wad.getDataAs("D_RUNNIN", MUS.class);
		for (MUS.Event event : mus)
			logger.info(event);
		wad.close();
		
		/*
		long n = 1000000000/140;
		long millis = n / 1000000;
		int nanos = (int)(n % 1000000);
		
		AtomicLong t = new AtomicLong(0);
		MUS.SequencerListener listener = new MUS.SequencerListener()
		{
			@Override
			public void onSystemEvent(int channel, int type)
			{
			}
			
			@Override
			public void onScoreEnd(int channel)
			{
			}
			
			@Override
			public void onPitchEvent(int channel, int pitch)
			{
			}
			
			@Override
			public void onNoteReleaseEvent(int channel, int note)
			{
			}
			
			@Override
			public void onNotePlayEvent(int channel, int note, int volume)
			{
				System.out.println(t + " P "+channel + ", " + note + " v"+volume);
			}
			
			@Override
			public void onNotePlayEvent(int channel, int note)
			{
				System.out.println(t + " P "+channel + ", " + note);
			}
			
			@Override
			public void onControllerChangeEvent(int channel, int controllerNumber, int controllerValue)
			{
			}
		};
		
		MUS.Sequencer seq = mus.getSequencer(listener);
		while (seq.step())
			try
			{
				Thread.sleep(millis, nanos);
				t.incrementAndGet();
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		*/
	}
}
