/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.struct;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import net.mtrop.doom.BinaryObject;

import com.blackrook.commons.Common;
import com.blackrook.commons.list.List;
import com.blackrook.commons.list.SortedList;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * Abstraction of MUS formatted music sequence data.
 * @author Matthew Tropiano
 */
public class DMXMUS implements BinaryObject, Iterable<DMXMUS.Event>
{
	public static final byte[] MUS_ID = {0x4d, 0x55, 0x53, 0x1a}; 
	
	public static final String[] NOTE_NAMES = {
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
	
	public static final String[] SYSTEM_EVENT_NAME = {
		"Sound Off",
		"Notes Off",
		"Monophonic",
		"Polyphonic",
		"Reset All Controllers",
	};
	
	public static final String[] CONTROLLER_NAME = {
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

	public static final String[] INSTRUMENT_NAME = {
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
	
	private List<Event> eventList;

	/**
	 * Creates a blank DMXMUS lump with no events.
	 */
	public DMXMUS()
	{
		eventList = new List<Event>();
	}
	
	/**
	 * Reads and creates a new DMXMUS object from an array of bytes.
	 * This reads from the array until a full MUS score is read.
	 * @param bytes the byte array to read.
	 * @return a new DMXMUS object.
	 * @throws IOException if the stream cannot be read.
	 */
	public static DMXMUS create(byte[] bytes) throws IOException
	{
		DMXMUS out = new DMXMUS();
		out.fromBytes(bytes);
		return out;
	}

	/**
	 * Reads and creates a new DMXMUS from an {@link InputStream} implementation.
	 * This reads from the stream until a full MUS score is read.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @return a new DMXMUS object.
	 * @throws IOException if the stream cannot be read.
	 */
	public static DMXMUS read(InputStream in) throws IOException
	{
		DMXMUS out = new DMXMUS();
		out.readBytes(in);
		return out;
	}

	@Override
	public Iterator<Event> iterator() 
	{
		return eventList.iterator();
	}

	@Override
	public byte[] toBytes()
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try { writeBytes(bos); } catch (IOException e) { /* Shouldn't happen. */ }
		return bos.toByteArray();
	}

	@Override
	public void fromBytes(byte[] data) throws IOException
	{
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		readBytes(bin);
		Common.close(bin);
	}

	@Override
	public void readBytes(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in, SuperReader.LITTLE_ENDIAN);
		
		byte[] head = sr.readBytes(4);
		if (!java.util.Arrays.equals(head, MUS_ID))
			throw new IOException("Not an MUS data chunk.");
		
		int scoreLen = sr.readUnsignedShort();
		int scoreOffset = sr.readUnsignedShort();
		
		/*int channels = */ sr.readUnsignedShort();
		/*int secondaryChannels = */ sr.readUnsignedShort();
		int instrumentCount = sr.readUnsignedShort();
		
		sr.readUnsignedShort();	// read dummy short.
		
		int[] instruments = new int[instrumentCount];
		for (int i = 0; i < instruments.length; i++)
			instruments[i] = sr.readUnsignedShort();
		
		in.skip(scoreOffset - (4 + (2*(6+instrumentCount))));
		
		eventList = new List<Event>(scoreLen/2);
		
		boolean foundEnd = false;
		
		while (!foundEnd)
		{
			byte eventDesc = sr.readByte();
			boolean last = Common.bitIsSet(eventDesc, 0x80);
			byte channel = (byte)(eventDesc & 0x0f);

			switch ((eventDesc & 0x70) >>> 4)
			{
				case Event.TYPE_RELEASE:
				{
					byte b = sr.readByte();
					int tics = 0;
					if (last)
						tics = sr.readVariableLengthInt();
					eventList.add(new NoteReleaseEvent(channel, b, last, tics));
				}
					break;
					
				case Event.TYPE_PLAY:
				{
					byte b = sr.readByte();
					byte note = (byte)(b & 0x7f);
					byte volume = NotePlayEvent.VOLUME_NO_CHANGE;
					if (Common.bitIsSet(b, 0x80))
						volume = sr.readByte();
					int tics = 0;
					if (last)
						tics = sr.readVariableLengthInt();
					eventList.add(new NotePlayEvent(channel, note, volume, last, tics));
				}
					break;

				case Event.TYPE_PITCH:
				{
					short b = sr.readUnsignedByte();
					int tics = 0;
					if (last)
						tics = sr.readVariableLengthInt();
					eventList.add(new PitchEvent(channel, b, last, tics));
				}
					break;
					
				case Event.TYPE_SYSTEM:
				{
					byte b = sr.readByte();
					int tics = 0;
					if (last)
						tics = sr.readVariableLengthInt();
					eventList.add(new SystemEvent(channel, b, last, tics));
				}
					break;
				
				case Event.TYPE_CHANGE_CONTROLLER:
				{
					byte b = sr.readByte();
					byte b2 = sr.readByte();
					int tics = 0;
					if (last)
						tics = sr.readVariableLengthInt();
					eventList.add(new ControllerChangeEvent(channel, b, b2, last, tics));
				}
					break;

				case Event.TYPE_SCORE_END:
				{
					int tics = 0;
					if (last)
						tics = sr.readVariableLengthInt();
					eventList.add(new ScoreEndEvent(channel, last, tics));
					foundEnd = true;
				}
					break;
			}
		}
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);
		
		ByteArrayOutputStream ebos = new ByteArrayOutputStream();
		SuperWriter esw = new SuperWriter(ebos, SuperWriter.LITTLE_ENDIAN);
		
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
			
			esw.writeBytes(event.toBytes());
		}
		
		byte[] data = ebos.toByteArray();
		
		sw.writeBytes(MUS_ID);
		sw.writeUnsignedShort(data.length);
		sw.writeUnsignedShort(4 + (2*(6+instruments.size())));
		sw.writeUnsignedShort(channels.size()-1);
		sw.writeUnsignedShort(0);
		sw.writeUnsignedShort(instruments.size());
		sw.writeUnsignedShort(0);
		for (Integer i : instruments)
			sw.writeUnsignedShort(i);
		sw.writeBytes(data);
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
		/** Event is last in group. */
		protected boolean last;
		/** Time to rest in tics. */
		protected int restTics;
		
		/**
		 * Creates a new MUS event.
		 * Sets "isLast" to false.
		 * @param type		Event type. Must be valid TYPE.
		 * @param channel	Event channel.
		 */
		protected Event(byte type, byte channel)
		{
			this(type, channel, false, 0);
		}

		/**
		 * Creates a new MUS event.
		 * @param type		Event type. Must be valid EVENT_TYPE.
		 * @param channel	Event channel.
		 * @param last		Is this the "last" event before another?
		 * @param restTics	The amount of tics before the next event gets processed.
		 */
		protected Event(byte type, byte channel, boolean last, int restTics)
		{
			if (type < 0 || type > 6 || type == 5)
				throw new IllegalArgumentException("Type must be from 0 to 6, inclusively, but not 5.");

			if (channel < 0 || channel > 15)
				throw new IllegalArgumentException("Channel must be from 0 to 15, inclusively.");
			
			this.type = type;
			this.channel = channel;
			this.last = last;
			this.restTics = Math.max(0, restTics);
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
		 */
		public void setChannel(byte channel)
		{
			this.channel = channel;
		}

		/**
		 * Checks if this is the last event in a group, before a rest needs to be taken?
		 * @return true if so, false if not.
		 */
		public boolean isLast()
		{
			return last;
		}

		/**
		 * Sets if this the last event in a group, before a rest needs to be taken.
		 * @param last true if so, false if not.
		 */
		public void setLast(boolean last)
		{
			this.last = last;
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
	 * Note release event.
	 */
	public static class NoteReleaseEvent extends Event
	{
		/** The note that will be released. */
		protected byte note;
		
		/**
		 * Creates a "release note" event.
		 * @param channel	Event channel.
		 * @param note		The note, from 0 to 127. 60 is Middle C. Each integer either way is one semitone.
		 * @throws IllegalArgumentException if <code>note</code> is not between 0 and 127.
		 */
		public NoteReleaseEvent(byte channel, byte note)
		{
			this(channel, note, false, 0);
		}
		
		/**
		 * Creates a "release note" event.
		 * @param channel	Event channel.
		 * @param note		The note, from 0 to 127. 60 is Middle C. Each integer either way is one semitone.
		 * @param last		Is this the "last" event before another?
		 * @param restTics	The amount of tics before the next event gets processed.
		 * @throws IllegalArgumentException if <code>note</code> is not between 0 and 127.
		 */
		public NoteReleaseEvent(byte channel, byte note, boolean last, int restTics)
		{
			super(TYPE_RELEASE, channel, last, restTics);
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
		 */
		public void setNote(byte note)
		{
			if (note < 0)
				throw new IllegalArgumentException("Note must be between 0 and 127.");
			this.note = note;
		}

		@Override
		public byte[] toBytes()
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				SuperWriter sw = new SuperWriter(bos, SuperWriter.LITTLE_ENDIAN);
				sw.writeByte((byte)((last ? 0x80 : 0x00) | (type << 4) | channel));
				sw.writeByte(note);
				sw.writeVariableLengthInt(restTics);
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
			sb.append(channel != CHANNEL_DRUM ? NOTE_NAMES[note] : NOTE_NAMES[note] + " (" + DRUM_INSTRUMENT_NAME[note-35] + ")");

			return sb.toString();
		}

	}
	
	/**
	 * Note play event.
	 */
	public static class NotePlayEvent extends Event
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
		 */
		public NotePlayEvent(byte channel, byte note, byte volume)
		{
			this(channel, note, volume, false, 0);
		}
		
		/**
		 * Creates a "play note" event.
		 * @param channel	Event channel.
		 * @param note		The note, from 0 to 127. 60 is Middle C. Each integer either way is one semitone.
		 * @param volume	The channel volume change from 0 to 127, or VOLUME_NO_CHANGE for same as last note.
		 * @param last		Is this the "last" event before another?
		 * @param restTics	The amount of tics before the next event gets processed.
		 */
		public NotePlayEvent(byte channel, byte note, byte volume, boolean last, int restTics)
		{
			super(TYPE_PLAY, channel, last, restTics);
			setNote(note);
			setVolume(volume);
		}
		
		/**
		 * Gets this event's note.
		 */
		public byte getNote()
		{
			return note;
		}
	
		/**
		 * Sets this event's note.
		 */
		public void setNote(byte note)
		{
			if (note < 0)
				throw new IllegalArgumentException("Note must be between 0 and 127.");
			this.note = note;
		}
	
		/**
		 * Gets this event's volume.
		 */
		public byte getVolume()
		{
			return volume;
		}
	
		/**
		 * Sets this event's volume, or no change.
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
				SuperWriter sw = new SuperWriter(bos, SuperWriter.LITTLE_ENDIAN);
				sw.writeByte((byte)((last ? 0x80 : 0x00) | (type << 4) | channel));
				sw.writeByte((byte)(note | (volume != VOLUME_NO_CHANGE ? 0x80 : 0x00)));
				if (volume != VOLUME_NO_CHANGE)
					sw.writeByte((byte)(volume & 0x7f));
				sw.writeVariableLengthInt(restTics);
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
			sb.append(channel != CHANNEL_DRUM ? NOTE_NAMES[note] : DRUM_INSTRUMENT_NAME[note-35]);
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
		protected short pitch;
		
		/**
		 * Creates a "pitch wheel" event.
		 * @param channel	Event channel.
		 * @param pitch		The pitch, from 0 to 255. 128 is no adjustment. 
		 * 					0 is one full semitone down. 255 is one full semitone up.
		 */
		public PitchEvent(byte channel, short pitch)
		{
			this(channel, pitch, false, 0);
		}
		
		/**
		 * Creates a "pitch wheel" event.
		 * @param channel	Event channel.
		 * @param pitch		The pitch, from 0 to 255. 128 is no adjustment. 
		 * 					0 is one full semitone down. 255 is one full semitone up.
		 * @param last		Is this the "last" event before another?
		 * @param restTics	The amount of tics before the next event gets processed.
		 */
		public PitchEvent(byte channel, short pitch, boolean last, int restTics)
		{
			super(TYPE_PITCH, channel, last, restTics);
			setPitch(pitch);
		}
		
		/**
		 * Gets this event's pitch.
		 */
		public short getPitch()
		{
			return pitch;
		}

		/**
		 * Sets this event's pitch.
		 */
		public void setPitch(short pitch)
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
				SuperWriter sw = new SuperWriter(bos, SuperWriter.LITTLE_ENDIAN);
				sw.writeByte((byte)((last ? 0x80 : 0x00) | (type << 4) | channel));
				sw.writeByte((byte)(pitch & 0x00ff));
				sw.writeVariableLengthInt(restTics);
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
		 * @param channel	Event channel.
		 * @param sysType	The system type.
		 */
		public SystemEvent(byte channel, byte sysType)
		{
			this(channel, sysType, false, 0);
		}
		
		/**
		 * Creates a "system" event.
		 * @param channel	Event channel.
		 * @param sysType	The system type.
		 * @param last		Is this the "last" event before another?
		 * @param restTics	The amount of tics before the next event gets processed.
		 */
		public SystemEvent(byte channel, byte sysType, boolean last, int restTics)
		{
			super(TYPE_SYSTEM, channel, last, restTics);
			setSystemType(sysType);
		}
		
		/**
		 * Gets this event's sysType.
		 */
		public short getSystemType()
		{
			return sysType;
		}
	
		/**
		 * Sets this event's sysType.
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
				SuperWriter sw = new SuperWriter(bos, SuperWriter.LITTLE_ENDIAN);
				sw.writeByte((byte)((last ? 0x80 : 0x00) | (type << 4) | channel));
				sw.writeByte(sysType);
				sw.writeVariableLengthInt(restTics);
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
		 */
		public ControllerChangeEvent(byte channel, byte controllerNumber, byte controllerValue)
		{
			this(channel, controllerNumber, controllerValue, false, 0);
		}
		
		/**
		 * Creates a "controller change" event.
		 * @param channel			Event channel.
		 * @param controllerNumber	The number of the controller (0 to 9).
		 * @param controllerValue	The controller value (0 to 127).
		 * @param last				Is this the "last" event before another?
		 * @param restTics			The amount of tics before the next event gets processed.
		 */
		public ControllerChangeEvent(byte channel, byte controllerNumber, byte controllerValue, boolean last, int restTics)
		{
			super(TYPE_CHANGE_CONTROLLER, channel, last, restTics);
			setController(controllerNumber);
			setValue(controllerValue);
		}
		
		/**
		 * Gets this event's target controller.
		 */
		public byte getController()
		{
			return controllerNumber;
		}

		/**
		 * Sets this event's target controller.
		 */
		public void setController(byte controllerNumber)
		{
			if (controllerNumber < 0 || controllerNumber > 9)
				throw new IllegalArgumentException("Controller must be between 0 and 9.");
			this.controllerNumber = controllerNumber;
		}

		/**
		 * Gets this event's controller value.
		 */
		public byte getValue()
		{
			return controllerValue;
		}

		/**
		 * Sets this event's controller value.
		 */
		public void setValue(byte controllerValue)
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
				SuperWriter sw = new SuperWriter(bos, SuperWriter.LITTLE_ENDIAN);
				sw.writeByte((byte)((last ? 0x80 : 0x00) | (type << 4) | channel));
				sw.writeByte(controllerNumber);
				sw.writeByte(controllerValue);
				sw.writeVariableLengthInt(restTics);
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
		public ScoreEndEvent(byte channel)
		{
			this(channel, false, 0);
		}
		
		/**
		 * Creates a "score ending" event.
		 * @param channel	Event channel.
		 * @param last		Is this the "last" event before another?
		 * @param restTics	The amount of tics before the next event gets processed.
		 */
		public ScoreEndEvent(byte channel, boolean last, int restTics)
		{
			super(TYPE_SCORE_END, channel, last, restTics);
		}
		
		@Override
		public byte[] toBytes()
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				SuperWriter sw = new SuperWriter(bos, SuperWriter.LITTLE_ENDIAN);
				sw.writeByte((byte)((last ? 0x80 : 0x00) | (type << 4) | channel));
				sw.writeVariableLengthInt(restTics);
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
