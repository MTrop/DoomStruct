/*******************************************************************************
 * Copyright (c) 2015-2019 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.sound;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.mtrop.doom.BinaryObject;
import net.mtrop.doom.io.SerialReader;
import net.mtrop.doom.io.SerialWriter;
import net.mtrop.doom.struct.vector.SortedList;
import net.mtrop.doom.util.MathUtils;

/**
 * Abstraction of MUS formatted music sequence data.
 * @author Matthew Tropiano
 */
public class DMXMUS implements BinaryObject, Iterable<DMXMUS.Event>
{
	public static final byte[] MUS_ID = {0x4d, 0x55, 0x53, 0x1a}; 
	
	public static final String[] NOTE_NAMES = 
	{
		"C0", "C#0", "D0", "Eb0", "E0", "F0", "F#0", "G0", "Ab0", "A0", "Bb0", "B0",
		"C1", "C#1", "D1", "Eb1", "E1", "F1", "F#1", "G1", "Ab1", "A1", "Bb1", "B1",
		"C2", "C#2", "D2", "Eb2", "E2", "F2", "F#2", "G2", "Ab2", "A2", "Bb2", "B2",
		"C3", "C#3", "D3", "Eb3", "E3", "F3", "F#3", "G3", "Ab3", "A3", "Bb3", "B3",
		"C4", "C#4", "D4", "Eb4", "E4", "F4", "F#4", "G4", "Ab4", "A4", "Bb4", "B4",
		"C5", "C#5", "D5", "Eb5", "E5", "F5", "F#5", "G5", "Ab5", "A5", "Bb5", "B5",
		"C6", "C#6", "D6", "Eb6", "E6", "F6", "F#6", "G6", "Ab6", "A6", "Bb6", "B6",
		"C7", "C#7", "D7", "Eb7", "E7", "F7", "F#7", "G7", "Ab7", "A7", "Bb7", "B7",
		"C8", "C#8", "D8", "Eb8", "E8", "F8", "F#8", "G8", "Ab8", "A8", "Bb8", "B8",
		"C9", "C#9", "D9", "Eb9", "E9", "F9", "F#9", "G9", "Ab9", "A9", "Bb9", "B9",
		"C10", "C#10", "D10", "Eb10", "E10", "F10", "F#10", "G10"
	};
	
	public static final String[] SYSTEM_EVENT_NAME = 
	{
		"Sound Off",
		"Notes Off",
		"Monophonic",
		"Polyphonic",
		"Reset All Controllers",
	};
	
	public static final String[] CONTROLLER_NAME = 
	{
		"Set Instrument",
		"Bank Select",
		"Vibrato Depth",
		"Set Volume",
		"Set Panning",
		"Expression Pot",
		"Reverb Depth",
		"Chorus Depth",
		"Sustain Pedal",
		"Soft Pedal"
	};

	public static final String[] INSTRUMENT_NAME = 
	{
		"Acoustic Grand Piano",
		"Bright Acoustic Piano",
		"Electric Grand Piano",
		"Honky-tonk Piano",
		"Rhodes Piano",
		"Chorused Piano",
		"Harpsichord",
		"Clavinet",
		
		"Celesta",
		"Glockenspiel",
		"Music Box",
		"Vibraphone",
		"Marimba",
		"Xylophone",
		"Tubular Bell",
		"Dulcimer",
		
		"Hammond Organ",   
		"Percussive Organ",
		"Rock Organ",  
		"Church Organ",
		"Reed Organ",  
		"Accordion",   
		"Harmonica",   
		"Tango Accordion", 
	
		"Acoustic Guitar (nylon)",
		"Acoustic Guitar (steel)",
		"Electric Guitar (jazz)",
		"Electric Guitar (clean)",
		"Electric Guitar (muted)",
		"Overdriven Guitar",
		"Distortion Guitar",
		"Guitar Harmonics",
	
		"Acoustic Bass",   
		"Electric Bass (finger)",  
		"Electric Bass (pick)",
		"Fretless Bass",   
		"Slap Bass 1", 
		"Slap Bass 2", 
		"Synth Bass 1",
		"Synth Bass 2",
	
		"Violin",
		"Viola",
		"Cello",
		"Contrabass",
		"Tremolo Strings",
		"Pizzicato Strings",
		"Orchestral Harp",
		"Timpani",
	
		"String Ensemble 1",   
		"String Ensemble 2",   
		"Synth Strings 1", 
		"Synth Strings 2", 
		"Choir Aahs",  
		"Voice Oohs",  
		"Synth Voice", 
		"Orchestra Hit",   
	
		"Trumpet",
		"Trombone",
		"Tuba",
		"Muted Trumpet",
		"French Horn",
		"Brass Section",
		"Synth Brass 1",
		"Synth Bass 2",
	
		"Soprano Sax",
		"Alto Sax",
		"Tenor Sax",  
		"Baritone Sax",
		"Oboe",
		"English Horn",
		"Bassoon", 
		"Clarinet",
	
		"Piccolo",
		"Flute",
		"Recorder",
		"Pan Flute",
		"Bottle Blow",
		"Shakuhachi",
		"Whistle",
		"Ocarina",
	
		"Lead 1 (square)", 
		"Lead 2 (sawtooth)",   
		"Lead 3 (calliope)",   
		"Lead 4 (chiffer)",
		"Lead 5 (charang)",
		"Lead 6 (voice)",  
		"Lead 7 (5th sawtooth)",   
		"Lead 8 (bass & lead)",
	
		"Pad 1 (new age)",
		"Pad 2 (warm)",
		"Pad 3 (polysynth)",
		"Pad 4 (choir)",
		"Pad 5 (bowed glass)",
		"Pad 6 (metal)",
		"Pad 7 (halo)",
		"Pad 8 (sweep)",
	
		"FX 1 (rain)",
		"FX 2 (soundtrack)",  
		"FX 3 (crystal)", 
		"FX 4 (atmosphere)",  
		"FX 5 (brightness)",  
		"FX 6 (goblin)",  
		"FX 7 (echo drops)",  
		"FX 8 (star-theme)",  
	
		"Sitar",
		"Banjo",
		"Shamisen",
		"Koto",
		"Kalimba",
		"Bag Pipe",
		"Fiddle",
		"Shanai",
	
		"Tinkle Bell",
		"Agogo",  
		"Steel Drums",
		"Woodblock",  
		"Taiko Drum", 
		"Melodic Tom",
		"Synth Drum", 
		"Reverse Cymbal", 
		
		"Guitar Fret Noise",
		"Breath Noise",
		"Seashore",
		"Bird Tweet",
		"Telephone Ring",
		"Helicopter",
		"Applause",
		"Gun Shot"
	};
	
	public static final String[] DRUM_INSTRUMENT_NAME = {
		"Acoustic Bass Drum",
		"Ride Cymbal 2",
		"Bass Drum",
		"High Bongo",
		"Slide Stick", 
		"Low Bango",
		"Acoustic Snare",  
		"Mute High Conga",
		"Hand Clap",
		"Open High Conga",
		"Electric Snare",
		"Low Conga",
		"Low Floor Tom",
		"High Timbale",
		"Closed High-Hat", 
		"Low Timbale",
		"High Floor Tom",  
		"High Agogo",
		"Pedal High Hat",  
		"Low Agogo",
		"Low Tom",
		"Cabasa",
		"Open High Hat",
		"Maracas",
		"Low-Mid Tom", 
		"Short Whistle",
		"High-Mid Tom",
		"Long Whistle",
		"Crash Cymbal 1",  
		"Short Guiro",
		"High Tom",
		"Long Guiro",
		"Ride Cymbal 1",
		"Claves",
		"Chinses Cymbal",  
		"High Wood Block",
		"Ride Bell",
		"Low Wood Block",
		"Tambourine",
		"Mute Cuica",
		"Splash Cymbal",
		"Open Cuica",
		"Cowbell",
		"Mute Triangle",
		"Crash Cymbal 2", 
		"Open Triangle",
		"Vibraslap"
	};

	public static final int CHANNEL_DRUM = 15;
	
	/** List of events. */
	private List<Event> eventList;
	/** Amount of primary channels. */
	private int primaryChannelCount;
	/** Amount of secondary channels. */
	private int secondaryChannelCount;
	/** Instruments. */
	private int[] instrumentPatches;

	/**
	 * Creates a blank DMXMUS lump with no events.
	 */
	public DMXMUS()
	{
		this.eventList = new ArrayList<Event>();
	}
	
	/**
	 * @return the amount of primary channels (from channel 0).
	 */
	public int getPrimaryChannelCount()
	{
		return primaryChannelCount;
	}

	/**
	 * @return the amount of secondary channels (from channel 10).
	 */
	public int getSecondaryChannelCount()
	{
		return secondaryChannelCount;
	}
	
	/**
	 * @return the amount of instrument patches.
	 */
	public int getInstrumentPatchCount()
	{
		return instrumentPatches.length;
	}

	/**
	 * Gets an instrument patch to preload.
	 * @param index the instrument patch list index.
	 * @return the instrument at the corresponding index.
	 * @throws ArrayIndexOutOfBoundsException of index is &lt; 0 or &gt; {@link #getInstrumentPatchCount()}.
	 */
	public int getInstrumentPatch(int index)
	{
		return instrumentPatches[index];
	}
	
	@Override
	public Iterator<Event> iterator()
	{
		return eventList.iterator();
	}

	@Override
	public void readBytes(InputStream in) throws IOException
	{
		SerialReader sr = new SerialReader(SerialReader.LITTLE_ENDIAN);
		
		byte[] head = sr.readBytes(in, 4);
		if (!java.util.Arrays.equals(head, MUS_ID))
			throw new IOException("Not an MUS data chunk.");
		
		int scoreLen = sr.readUnsignedShort(in);
		int scoreOffset = sr.readUnsignedShort(in);
		
		this.primaryChannelCount = sr.readUnsignedShort(in);
		this.secondaryChannelCount = sr.readUnsignedShort(in);
		int instrumentCount = sr.readUnsignedShort(in);
		
		// read reserved short.
		sr.readUnsignedShort(in);
		
		this.instrumentPatches = new int[instrumentCount];
		for (int i = 0; i < instrumentPatches.length; i++)
			instrumentPatches[i] = sr.readUnsignedShort(in);
		
		in.skip(scoreOffset - (4 + (2*(6+instrumentCount))));
		
		eventList = new ArrayList<Event>(scoreLen/2);
		
		boolean foundEnd = false;
		
		while (!foundEnd)
		{
			byte eventDesc = sr.readByte(in);
			boolean last = MathUtils.bitIsSet(eventDesc, 0x80);
			byte channel = (byte)(eventDesc & 0x0f);

			switch ((eventDesc & 0x70) >>> 4)
			{
				case Event.TYPE_RELEASE:
				{
					byte b = sr.readByte(in);
					int tics = 0;
					if (last)
						tics = sr.readVariableLengthInt(in);
					eventList.add(new NoteReleaseEvent(channel, b, tics));
				}
				break;
					
				case Event.TYPE_PLAY:
				{
					byte b = sr.readByte(in);
					byte note = (byte)(b & 0x7f);
					byte volume = NotePlayEvent.VOLUME_NO_CHANGE;
					if (MathUtils.bitIsSet(b, 0x80))
						volume = sr.readByte(in);
					int tics = 0;
					if (last)
						tics = sr.readVariableLengthInt(in);
					eventList.add(new NotePlayEvent(channel, note, volume, tics));
				}
				break;

				case Event.TYPE_PITCH:
				{
					short b = sr.readUnsignedByte(in);
					int tics = 0;
					if (last)
						tics = sr.readVariableLengthInt(in);
					eventList.add(new PitchEvent(channel, b, tics));
				}
				break;
					
				case Event.TYPE_SYSTEM:
				{
					byte b = sr.readByte(in);
					int tics = 0;
					if (last)
						tics = sr.readVariableLengthInt(in);
					eventList.add(new SystemEvent(channel, b, tics));
				}
				break;
				
				case Event.TYPE_CHANGE_CONTROLLER:
				{
					byte b = sr.readByte(in);
					byte b2 = sr.readByte(in);
					int tics = 0;
					if (last)
						tics = sr.readVariableLengthInt(in);
					eventList.add(new ControllerChangeEvent(channel, b, b2, tics));
				}
				break;

				case Event.TYPE_SCORE_END:
				{
					int tics = 0;
					if (last)
						tics = sr.readVariableLengthInt(in);
					eventList.add(new ScoreEndEvent(channel, tics));
					foundEnd = true;
				}
				break;
			}
		}
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		SerialWriter sw = new SerialWriter(SerialWriter.LITTLE_ENDIAN);
		
		ByteArrayOutputStream ebos = new ByteArrayOutputStream();
		SerialWriter esw = new SerialWriter(SerialWriter.LITTLE_ENDIAN);
		
		SortedList<Integer> channels = new SortedList<Integer>(4);
		SortedList<Integer> instruments = new SortedList<Integer>(4);
		
		for (Event event : eventList)
		{
			int channel = event.channel;
			if (!channels.contains(channel))
				channels.add(channel);

			switch (event.getType())
			{
				case Event.TYPE_PLAY:
				{
					NotePlayEvent c = (NotePlayEvent)event;
					if (c.getChannel() == CHANNEL_DRUM)	// drum channel
					{
						byte n = c.getNote();
						if (n >= 35 && n <= 81)
						{
							int inst = 100+n;
							if (!instruments.contains(inst))
								instruments.add(inst);
						}
					}
				}
				break;

				case Event.TYPE_RELEASE:
				{
					NoteReleaseEvent c = (NoteReleaseEvent)event;
					if (c.getChannel() == CHANNEL_DRUM)	// drum channel
					{
						byte n = c.getNote();
						if (n >= 35 && n <= 81)
						{
							int inst = 100+n;
							if (!instruments.contains(inst))
								instruments.add(inst);
						}
					}
				}
				break;
					
				case Event.TYPE_CHANGE_CONTROLLER:
				{
					ControllerChangeEvent c = (ControllerChangeEvent)event;
					if (c.getChannel() != CHANNEL_DRUM && c.getController() == ControllerChangeEvent.CONTROLLER_INSTRUMENT)
					{
						int inst = c.getValue();
						if (!instruments.contains(inst))
							instruments.add(inst);
					}
				}
				break;
			}
			
			esw.writeBytes(ebos, event.toBytes());
		}
		
		byte[] data = ebos.toByteArray();
		
		sw.writeBytes(out, MUS_ID);
		sw.writeUnsignedShort(out, data.length);
		sw.writeUnsignedShort(out, 4 + (2*(6+instruments.size())));
		sw.writeUnsignedShort(out, channels.size()-1);
		sw.writeUnsignedShort(out, 0);
		sw.writeUnsignedShort(out, instruments.size());
		sw.writeUnsignedShort(out, 0);
		for (Integer i : instruments)
			sw.writeUnsignedShort(out, i);
		sw.writeBytes(out, data);
	}
	
	/**
	 * Individual events.
	 */
	public static abstract class Event
	{
		/** Release note event. */
		public static final byte TYPE_RELEASE = 0;
		/** Play note event. */
		public static final byte TYPE_PLAY = 1;
		/** Pitch slide event. */
		public static final byte TYPE_PITCH = 2;
		/** System event event. */
		public static final byte TYPE_SYSTEM = 3;
		/** Controller change event. */
		public static final byte TYPE_CHANGE_CONTROLLER = 4;
		/** Score end event. */
		public static final byte TYPE_SCORE_END = 6;
		
		/** Event type. */
		protected byte type;
		/** Event channel. */
		protected byte channel;
		/** Time to rest in tics. */
		protected int restTics;
		
		/**
		 * Creates a new MUS event.
		 * @param type		Event type. Must be valid EVENT_TYPE.
		 * @param channel	Event channel.
		 * @param restTics	The amount of tics before the next event gets processed.
		 * @throws IllegalArgumentException if <code>type</code> is 5 or not between 0 and 6, or <code>channel</code> is not between 0 and 15.
		 */
		protected Event(byte type, byte channel, int restTics)
		{
			if (type < 0 || type > 6 || type == 5)
				throw new IllegalArgumentException("Type must be from 0 to 6, inclusively, but not 5.");

			this.type = type;
			setChannel(channel);
			setRestTics(Math.max(0, restTics));
		}

		/**
		 * @return this Event's type.
		 */
		public byte getType()
		{
			return type;
		}

		/**
		 * @return this Event's channel.
		 */
		public byte getChannel()
		{
			return channel;
		}

		/**
		 * Sets this Event's channel.
		 * @param channel the channel number.
		 * @throws IllegalArgumentException if <code>channel</code> is not between 0 and 15.
		 */
		public void setChannel(byte channel)
		{
			if (channel < 0 || channel > 15)
				throw new IllegalArgumentException("Channel must be from 0 to 15, inclusively.");
			this.channel = channel;
		}

		/**
		 * Checks if this is the last event in a group, before a rest needs to be taken?
		 * @return true if so, false if not.
		 */
		public boolean isLast()
		{
			return restTics != 0;
		}

		/**
		 * Gets the amount of tics in the rest period.
		 * Only valid if {@link #isLast()} is true.
		 * @return the amount of rest tics.
		 */
		public int getRestTics()
		{
			return restTics;
		}

		/**
		 * Sets the amount of tics in the rest period.
		 * Only valid if {@link #isLast()} is true.
		 * @param restTics the new amount of rest tics.
		 */
		public void setRestTics(int restTics)
		{
			this.restTics = restTics;
		}
		
		/**
		 * @return this event to a serialized byte form.
		 */
		public abstract byte[] toBytes();
		
	}
	
	/**
	 * An event that deals with notes.
	 */
	private static abstract class NoteEvent extends Event
	{
		/** The event's note. */
		protected byte note;
		
		/**
		 * Creates a new MUS note event.
		 * @param type		Event type. Must be valid EVENT_TYPE.
		 * @param channel	Event channel.
		 * @param note		The note on this event.
		 * @param restTics	The amount of tics before the next event gets processed.
		 * @throws IllegalArgumentException if <code>type</code> is 5 or not between 0 and 6, 
		 * or <code>channel</code> is not between 0 and 15, 
		 * or <code>note</code> is not between 0 and 127.
		 */
		protected NoteEvent(byte type, byte channel, byte note, int restTics)
		{
			super(type, channel, restTics);
			setNote(note);
		}
		
		/**
		 * @return this event's note.
		 */
		public byte getNote()
		{
			return note;
		}

		/**
		 * Sets this event's note.
		 * @param note the new note.
		 * @throws IllegalArgumentException if <code>note</code> is not between 0 and 127.
		 */
		public void setNote(byte note)
		{
			if (note < 0)
				throw new IllegalArgumentException("Note must be between 0 and 127.");
			this.note = note;
		}

	}
	
	/**
	 * Note release event.
	 */
	public static class NoteReleaseEvent extends NoteEvent
	{
		/**
		 * Creates a "release note" event.
		 * @param channel	Event channel.
		 * @param note		The note, from 0 to 127. 60 is Middle C. Each integer either way is one semitone.
		 * @throws IllegalArgumentException if <code>channel</code> is not between 0 and 15, 
		 * or <code>note</code> is not between 0 and 127.
		 */
		private NoteReleaseEvent(byte channel, byte note)
		{
			this(channel, note, 0);
		}
		
		/**
		 * Creates a "release note" event.
		 * @param channel	Event channel.
		 * @param note		The note, from 0 to 127. 60 is Middle C. Each integer either way is one semitone.
		 * @param restTics	The amount of tics before the next event gets processed.
		 * @throws IllegalArgumentException if <code>channel</code> is not between 0 and 15, 
		 * or <code>note</code> is not between 0 and 127.
		 */
		private NoteReleaseEvent(byte channel, byte note, int restTics)
		{
			super(TYPE_RELEASE, channel, note, restTics);
		}
		
		@Override
		public byte[] toBytes()
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				SerialWriter sw = new SerialWriter(SerialWriter.LITTLE_ENDIAN);
				sw.writeByte(bos, (byte)((isLast() ? 0x80 : 0x00) | (type << 4) | channel));
				sw.writeByte(bos, note);
				if (isLast())
					sw.writeVariableLengthInt(bos, restTics);
				return bos.toByteArray();
			} catch (IOException ioe) {
				return null;
			}
		}
		
		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append("MUSEvent ");

			sb.append("Note Release");
			sb.append(" ");

			sb.append("Channel: ");
			sb.append(channel);
			sb.append(" Rest: ");
			sb.append(restTics);

			sb.append(" Note: ");
			sb.append(NOTE_NAMES[note]);

			return sb.toString();
		}

	}
	
	/**
	 * Note play event.
	 */
	public static class NotePlayEvent extends NoteEvent
	{
		public static final byte VOLUME_NO_CHANGE = -1;
		
		/** The note that will be played. */
		protected byte note;
		/** The volume that the note will be played. */
		protected byte volume;
		
		/**
		 * Creates a "play note" event.
		 * @param channel	Event channel.
		 * @param note		The note, from 0 to 127. 60 is Middle C. Each integer either way is one semitone.
		 * @param volume	The channel volume change from 0 to 127, or VOLUME_NO_CHANGE for same as last note.
		 * @throws IllegalArgumentException if <code>channel</code> is not between 0 and 15, 
		 * or <code>note</code> is not between 0 and 127,
		 * or <code>volume</code> is not between 0 to 127, or VOLUME_NO_CHANGE.
		 */
		private NotePlayEvent(byte channel, byte note, byte volume)
		{
			this(channel, note, volume, 0);
		}
		
		/**
		 * Creates a "play note" event.
		 * @param channel	Event channel.
		 * @param note		The note, from 0 to 127. 60 is Middle C. Each integer either way is one semitone.
		 * @param volume	The channel volume change from 0 to 127, or VOLUME_NO_CHANGE for same as last note.
		 * @param restTics	The amount of tics before the next event gets processed.
		 * @throws IllegalArgumentException if <code>channel</code> is not between 0 and 15, 
		 * or <code>note</code> is not between 0 and 127,
		 * or <code>volume</code> is not between 0 to 127, or VOLUME_NO_CHANGE.
		 */
		private NotePlayEvent(byte channel, byte note, byte volume, int restTics)
		{
			super(TYPE_PLAY, channel, note, restTics);
			setVolume(volume);
		}
		
		/**
		 * @return this event's volume.
		 */
		public byte getVolume()
		{
			return volume;
		}
	
		/**
		 * Sets this event's volume, or no change.
		 * @param volume the new volume value.
		 * @throws IllegalArgumentException if <code>volume</code> is not between 0 and 127 nor VOLUME_NO_CHANGE.
		 */
		public void setVolume(byte volume)
		{
			if (volume != VOLUME_NO_CHANGE && volume < 0)
				throw new IllegalArgumentException("Volume must be between 0 and 127 or VOLUME_NO_CHANGE (-1).");
			this.volume = volume;
		}
	
		@Override
		public byte[] toBytes()
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				SerialWriter sw = new SerialWriter(SerialWriter.LITTLE_ENDIAN);
				sw.writeByte(bos, (byte)((isLast() ? 0x80 : 0x00) | (type << 4) | channel));
				sw.writeByte(bos, (byte)(note | (volume != VOLUME_NO_CHANGE ? 0x80 : 0x00)));
				if (volume != VOLUME_NO_CHANGE)
					sw.writeByte(bos, (byte)(volume & 0x7f));
				if (isLast())
					sw.writeVariableLengthInt(bos, restTics);
				return bos.toByteArray();
			} catch (IOException ioe) {
				return null;
			}
		}
	
		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append("MUSEvent ");

			sb.append("Note Play");
			sb.append(" ");

			sb.append("Channel: ");
			sb.append(channel);
			sb.append(" Rest: ");
			sb.append(restTics);

			sb.append(" Note: ");
			sb.append(NOTE_NAMES[note]);
			if (volume != VOLUME_NO_CHANGE)
			{
				sb.append(" Volume: ");
				sb.append(volume);
			}

			return sb.toString();
		}

	}

	/**
	 * Pitch wheel event.
	 */
	public static class PitchEvent extends Event
	{
		/** The pitch wheel adjustment. */
		protected int pitch;
		
		/**
		 * Creates a "pitch wheel" event.
		 * @param channel	Event channel.
		 * @param pitch		The pitch, from 0 to 255. 128 is no adjustment. 
		 * 					0 is one full semitone down. 255 is one full semitone up.
		 * @throws IllegalArgumentException if <code>channel</code> is not between 0 and 15, 
		 * or <code>note</code> is not between 0 and 127,
		 * or <code>pitch</code> is not between 0 to 255.
		 */
		private PitchEvent(byte channel, int pitch)
		{
			this(channel, pitch, 0);
		}
		
		/**
		 * Creates a "pitch wheel" event.
		 * @param channel	Event channel.
		 * @param pitch		The pitch, from 0 to 255. 128 is no adjustment. 
		 * 					0 is one full semitone down. 255 is one full semitone up.
		 * @param restTics	The amount of tics before the next event gets processed.
		 * @throws IllegalArgumentException if <code>channel</code> is not between 0 and 15, 
		 * or <code>note</code> is not between 0 and 127,
		 * or <code>pitch</code> is not between 0 to 255.
		 */
		private PitchEvent(byte channel, int pitch, int restTics)
		{
			super(TYPE_PITCH, channel, restTics);
			setPitch(pitch);
		}
		
		/**
		 * @return this event's pitch value.
		 */
		public int getPitch()
		{
			return pitch;
		}

		/**
		 * Sets this event's pitch.
		 * @param pitch the pitch value.
		 * @throws IllegalArgumentException if <code>pitch</code> is not between 0 and 255.
		 */
		public void setPitch(int pitch)
		{
			if (pitch < 0 || pitch > 255)
				throw new IllegalArgumentException("Pitch must be between 0 and 255.");
			this.pitch = pitch;
		}

		@Override
		public byte[] toBytes()
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				SerialWriter sw = new SerialWriter(SerialWriter.LITTLE_ENDIAN);
				sw.writeByte(bos, (byte)((isLast() ? 0x80 : 0x00) | (type << 4) | channel));
				sw.writeByte(bos, (byte)(pitch & 0x00ff));
				if (isLast())
					sw.writeVariableLengthInt(bos, restTics);
				return bos.toByteArray();
			} catch (IOException ioe) {
				return null;
			}
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append("MUSEvent ");

			sb.append("Pitch Wheel");
			sb.append(" ");

			sb.append("Channel: ");
			sb.append(channel);
			sb.append(" Rest: ");
			sb.append(restTics);

			sb.append(" Pitch: ");
			sb.append(pitch);

			return sb.toString();
		}

	}
	
	/**
	 * System event.
	 */
	public static class SystemEvent extends Event
	{
		public static final byte SYSTEM_SOUND_OFF = 10;
		public static final byte SYSTEM_NOTES_OFF = 11;
		public static final byte SYSTEM_MONO = 12;
		public static final byte SYSTEM_POLY = 13;
		public static final byte SYSTEM_RESET_ALL_CONTROLLERS = 14;

		/** The type. */
		protected byte sysType;
		
		/**
		 * Creates a "system" event.
		 * @param channel Event channel.
		 * @param sysType The system type.
		 * @throws IllegalArgumentException if <code>channel</code> is not between 0 and 15, 
		 * or <code>sysType</code> is not between 10 and 14.
		 */
		private SystemEvent(byte channel, byte sysType)
		{
			this(channel, sysType, 0);
		}
		
		/**
		 * Creates a "system" event.
		 * @param channel	Event channel.
		 * @param sysType	The system type.
		 * @param restTics	The amount of tics before the next event gets processed.
		 * @throws IllegalArgumentException if <code>channel</code> is not between 0 and 15, 
		 * or <code>sysType</code> is not between 10 and 14.
		 */
		private SystemEvent(byte channel, byte sysType, int restTics)
		{
			super(TYPE_SYSTEM, channel, restTics);
			setSystemType(sysType);
		}
		
		/**
		 * @return this event's sysType.
		 */
		public byte getSystemType()
		{
			return sysType;
		}
	
		/**
		 * Sets this event's sysType.
		 * @param sysType the new system type.
		 * @throws IllegalArgumentException if <code>sysType</code> is not between 10 and 14.
		 */
		public void setSystemType(byte sysType)
		{
			if (sysType < 10 || sysType > 14)
				throw new IllegalArgumentException("System Type must be between 10 and 14.");
			this.sysType = sysType;
		}
	
		@Override
		public byte[] toBytes()
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				SerialWriter sw = new SerialWriter(SerialWriter.LITTLE_ENDIAN);
				sw.writeByte(bos, (byte)((isLast() ? 0x80 : 0x00) | (type << 4) | channel));
				sw.writeByte(bos, sysType);
				if (isLast())
					sw.writeVariableLengthInt(bos, restTics);
				return bos.toByteArray();
			} catch (IOException ioe) {
				return null;
			}
		}
	
		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append("MUSEvent ");

			sb.append("System");
			sb.append(" ");

			sb.append("Channel: ");
			sb.append(channel);
			sb.append(" Rest: ");
			sb.append(restTics);

			sb.append(" ");
			sb.append(SYSTEM_EVENT_NAME[sysType-10]);

			return sb.toString();
		}

	}

	/**
	 * Controller Change event.
	 */
	public static class ControllerChangeEvent extends Event
	{
		public static final byte CONTROLLER_INSTRUMENT = 0;
		public static final byte CONTROLLER_BANK_SELECT = 1;
		public static final byte CONTROLLER_MODULATION_POT = 2;
		public static final byte CONTROLLER_VOLUME = 3;
		public static final byte CONTROLLER_PANNING = 4;
		public static final byte CONTROLLER_EXPRESSION_POT = 5;
		public static final byte CONTROLLER_REVERB = 6;
		public static final byte CONTROLLER_CHORUS = 7;
		public static final byte CONTROLLER_SUSTAIN_PEDAL = 8;
		public static final byte CONTROLLER_SOFT_PEDAL = 9;
		
		/** The controller number to change. */
		protected byte controllerNumber;
		/** The controller value. */
		protected byte controllerValue;
		
		/**
		 * Creates a "controller change" event.
		 * @param channel	Event channel.
		 * @param controllerNumber	The number of the controller (0 to 9).
		 * @param controllerValue	The controller value (0 to 127).
		 * @throws IllegalArgumentException if <code>channel</code> is not between 0 and 15, 
		 * or <code>controllerNumber</code> is not between 0 and 9,
		 * or <code>controllerValue</code> is not between 0 and 127.
		 */
		private ControllerChangeEvent(byte channel, byte controllerNumber, byte controllerValue)
		{
			this(channel, controllerNumber, controllerValue, 0);
		}
		
		/**
		 * Creates a "controller change" event.
		 * @param channel			Event channel.
		 * @param controllerNumber	The number of the controller (0 to 9).
		 * @param controllerValue	The controller value (0 to 127).
		 * @param restTics			The amount of tics before the next event gets processed.
		 * @throws IllegalArgumentException if <code>channel</code> is not between 0 and 15, 
		 * or <code>controllerNumber</code> is not between 0 and 9,
		 * or <code>controllerValue</code> is not between 0 and 127.
		 */
		private ControllerChangeEvent(byte channel, byte controllerNumber, byte controllerValue, int restTics)
		{
			super(TYPE_CHANGE_CONTROLLER, channel, restTics);
			setController(controllerNumber);
			setControllerValue(controllerValue);
		}
		
		/**
		 * @return this event's target controller.
		 */
		public byte getController()
		{
			return controllerNumber;
		}

		/**
		 * Sets this event's target controller.
		 * @param controllerNumber the controller number.
		 * @throws IllegalArgumentException if <code>controllerNumber</code> is not between 0 and 9.
		 */
		public void setController(byte controllerNumber)
		{
			if (controllerNumber < 0 || controllerNumber > 9)
				throw new IllegalArgumentException("Controller must be between 0 and 9.");
			this.controllerNumber = controllerNumber;
		}

		/**
		 * @return this event's controller value.
		 */
		public byte getValue()
		{
			return controllerValue;
		}

		/**
		 * Sets this event's controller value.
		 * @param controllerValue the new controller value.
		 * @throws IllegalArgumentException if <code>controllerValue</code> is not between 0 and 127.
		 */
		public void setControllerValue(byte controllerValue)
		{
			if (controllerValue < 0 || controllerValue > 127)
				throw new IllegalArgumentException("Value must be between 0 and 127.");
			this.controllerValue = controllerValue;
		}

		@Override
		public byte[] toBytes()
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				SerialWriter sw = new SerialWriter(SerialWriter.LITTLE_ENDIAN);
				sw.writeByte(bos, (byte)((isLast() ? 0x80 : 0x00) | (type << 4) | channel));
				sw.writeByte(bos, controllerNumber);
				sw.writeByte(bos, controllerValue);
				if (isLast())
					sw.writeVariableLengthInt(bos, restTics);
				return bos.toByteArray();
			} catch (IOException ioe) {
				return null;
			}
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append("MUSEvent ");

			sb.append("Controller Change");
			sb.append(" ");

			sb.append("Channel: ");
			sb.append(channel);
			sb.append(" Rest: ");
			sb.append(restTics);

			sb.append(" Type: ");
			sb.append(CONTROLLER_NAME[controllerNumber]);
			sb.append(" Value: ");
			
			if (controllerValue == CONTROLLER_INSTRUMENT)
				sb.append(INSTRUMENT_NAME[controllerValue]);
			else
				sb.append(controllerValue);

			return sb.toString();
		}

	}

	/**
	 * Score ending event.
	 */
	public static class ScoreEndEvent extends Event
	{
		/**
		 * Creates a "score ending" event.
		 * @param channel	Event channel.
		 */
		private ScoreEndEvent(byte channel)
		{
			this(channel, 0);
		}
		
		/**
		 * Creates a "score ending" event.
		 * @param channel	Event channel.
		 * @param restTics	The amount of tics before the next event gets processed.
		 */
		private ScoreEndEvent(byte channel, int restTics)
		{
			super(TYPE_SCORE_END, channel, restTics);
		}
		
		@Override
		public byte[] toBytes()
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				SerialWriter sw = new SerialWriter(SerialWriter.LITTLE_ENDIAN);
				sw.writeByte(bos, (byte)((isLast() ? 0x80 : 0x00) | (type << 4) | channel));
				if (isLast())
					sw.writeVariableLengthInt(bos, restTics);
				return bos.toByteArray();
			} catch (IOException ioe) {
				return null;
			}
		}
	
		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append("MUSEvent ");

			sb.append("Score End");
			sb.append(" ");

			sb.append("Channel: ");
			sb.append(channel);
			sb.append(" Rest: ");
			sb.append(restTics);

			return sb.toString();
		}

	}

}
