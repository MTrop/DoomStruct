# Doom Struct

Copyright (c) 2015 - 2019 Matt Tropiano  

### NOTICE

This library is an offshoot/rewrite of [Black Rook Software's Doom Struct Project](https://github.com/BlackRookSoftware/Doom), 
of which I have been granted rights to rewrite (the rights were granted to myself BY myself).

All deprecated classes from the origin project will not be in this one.
All end users are encouraged to switch to this one, as this project will be 
actively maintained.

### Required Libraries

NONE

### Required Modules

[java.desktop](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/module-summary.html)  
* [java.xml](https://docs.oracle.com/en/java/javase/11/docs/api/java.xml/module-summary.html)  
* [java.datatransfer](https://docs.oracle.com/en/java/javase/11/docs/api/java.datatransfer/module-summary.html)  
* [java.base](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/module-summary.html)  

### Source

The MASTER branch contains stable code (I hope). The DEVELOP branch's contents may always
shift. 

The library will be in flux until version 2.0. 

### Introduction

The purpose of the Doom Struct project is to provide a means to read/write
data structures for the Doom Engine and similar derivatives.

### Why?

There are several libraries out there for reading data from Doom in many different programming languages,
but there isn't a decent one for Java. And by decent, I mean:

* Useful
* Documented
* Performant
* Efficient

The goal with this library is to get end-users up-and-running quickly with little boilerplate code,
and to ensure clarity within documentation and with written code, and future-proofing to a reasonable
extent.


### Implemented Features (so far)

* Reads/edits WAD files.
* Reads PK3 files (zips).
* Full UDMF parsing/writing support.
* Reads/edits all Doom data structures in Doom, Hexen/ZDoom, or Strife 
  formats. This includes textures, patches, lines, vertices, things, sectors,
  nodes, palettes, colormaps, text, PNG data, music data, sound data, flats, blockmaps,
  reject, and even ENDOOM-type VGA lumps.
* Supports PNGs with offset data (grAb).
* Reads/edits Boom-engine data lumps like ANIMATED and SWITCHES. 
* Contains a utility class for converting Doom graphics to standard Java
  graphics structures, and vice-versa.
* Contains a utility class for converting Doom DMX sound data to other formats via
  the Java SPI, and vice-versa.
* Contains a utility class for managing texture sets without needing to care too much
  about Doom's nightmarish texture data setup. 

### In the Future...

* Better ENDOOM rendering options.
* Stuff for drawing maps more easily.
* Inline lump content editing (not just reading).

### Library

Contained in this release is a series of libraries that allow reading, writing,
and extracting data in Doom Engine structures, found in the **net.mtrop.doom** 
packages. 

### Examples

Open a WAD file (and close it).

	WadFile wad = new WadFile("doom2.wad");
	wad.close();

Open `DOOM2.WAD` and read `MAP01` into a Doom Map.

	WadFile wad = new WadFile("doom2.wad");
	DoomMap map = MapUtils.createDoomMap(wad, "map01");
	wad.close();

Open `DOOM2.WAD` and fetch all sectors in `MAP29` with the `BLOOD1` floor texture.

	WadFile wad = new WadFile("doom2.wad");
	Set<DoomSector> set = wad.getDataAsList("sectors", "map29", DoomSector.class, DoomSector.LENGTH).stream()
		.filter((sector) -> sector.getFloorTexture().equals("BLOOD1"))
		.collect(Collectors.toSet());
	wad.close();

Open `HEXEN.WAD` and fetch all things in `MAP01` that have a special.

	WadFile wad = new WadFile("hexen.wad");
	Set<HexenThing> set = wad.getDataAsList("things", "map01", HexenThing.class, HexenThing.LENGTH).stream()
		.filter((thing) -> thing.getSpecial() > 0)
		.collect(Collectors.toSet());
	wad.close();

Open `DOOM.WAD` and fetch all maps that have less than 1000 linedefs.

	final WadFile wad = new WadFile("doom.wad");
	Set<String> set = Arrays.asList(MapUtils.getAllMapHeaders(wad)).stream()
		.filter((header) -> wad.getEntry("linedefs", header).getSize() / DoomLinedef.LENGTH < 1000)
		.collect(Collectors.toSet());
	wad.close();

Open `SQUARE1.PK3`, fetch `maps/E1A1.WAD` and read it into a UDMF Map.

	DoomPK3 pk3 = new DoomPK3("square1.pk3");
	WadBuffer wad = pk3.getDataAsWadBuffer("maps/e1a1.wad");
	UDMFMap map = MapUtils.createUDMFMap(wad, "e1a1");
	pk3.close();

Open `DOOM2.WAD`, read `DEMO2` and figure out its duration in seconds.

	WadFile wad = new WadFile("doom2.wad");
	Demo demo = wad.getDataAs("demo2", Demo.class);
	wad.close();
	double duration = demo.getLength();

Open `DOOM2.WAD`, fetch all `TROO*` (graphic) entries and export them as PNGs.

	WadFile wad = new WadFile("doom2.wad");
	final Palette pal = wad.getDataAs("playpal", Palette.class);
	for (WadEntry entry : wad) {
		if (entry.getName().startsWith("TROO")) {
			Picture p = wad.getDataAs(entry, Picture.class);
			ImageIO.write(GraphicUtils.createImage(p, pal), "PNG", new File(entry.getName()+".png"));
		}
	}
	wad.close();

Open `DOOM.WAD`, fetch all `DS*` (audio) entries, upsample them to 22kHz (cosine interpolation), and export them as WAVs.

	WadFile wad = new WadFile("doom.wad");
	for (WadEntry entry : wad) {
		if (entry.getName().startsWith("DS")) {
			DMXSound sound = wad.getDataAs(entry, DMXSound.class)
					.resample(InterpolationType.COSINE, DMXSound.SAMPLERATE_22KHZ);
			SoundUtils.writeSoundToFile(sound, AudioFileFormat.Type.WAVE, new File(entry.getName() + ".wav"));
		}
	}
	wad.close();


### Compiling with Ant

To compile this library with Apache Ant, type:

	ant compile

To make Maven-compatible JARs of this library (placed in the *build/jar* directory), type:

	ant jar

To make Javadocs (placed in the *build/docs* directory):

	ant javadoc

To compile main and test code and run tests (if any):

	ant test

To make Zip archives of everything (main src/resources, bin, javadocs, placed in the *build/zip* directory):

	ant zip

To compile, JAR, test, and Zip up everything:

	ant release

To clean up everything:

	ant clean
	
### Other

This program/library and the accompanying materials
are made available under the terms of the GNU Lesser Public License v2.1
which accompanies this distribution, and is available at
http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html

A copy of the LGPL should have been included in this release (LICENSE.txt).
If it was not, please contact me for a copy, or to notify me of a distribution
that has not included it. 

This contains code copied from Black Rook Base, under the terms of the MIT License (docs/LICENSE-BlackRookBase.txt).

<sub>Eat your heart out, <a href="https://github.com/devinacker/omgifol">OMGIFOL</a>!</sub>