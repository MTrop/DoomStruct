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

### Implemented Features (so far)

- Reads WAD files.
- Can read PK3 package type.
- Reads all Doom map and data structures in Doom, Hexen/ZDoom, or Strife 
  formats. This includes textures, patches, lines, vertices, things, sectors,
  nodes, palettes, colormaps, text, PNG data, MUS data, flats, blockmaps,
  reject, and even ENDOOM-type VGA lumps.
- Contains a utility class for converting Doom graphics to standard Java
  graphics structures.
- Can read/edit Boom-engine data lumps like ANIMATED and SWITCHES. 
- Full UDMF parsing/writing support.

### Library

Contained in this release is a series of libraries that allow reading, writing,
and extracting data in Doom Engine structures, found in the **net.mtrop.doom** 
packages. 

### Examples

Open a WAD file (and close it).

	WadFile wad = new WadFile("doom2.wad");
	wad.close();

Open `MAP01` in `DOOM2.WAD`.

	WadFile wad = new WadFile("doom2.wad");
	DoomMap map = MapUtils.createDoomMap(wad, "MAP01");
	wad.close();

Open `MAP29` in `DOOM2.WAD` and fetch all sectors with the `BLOOD1` floor texture.

	WadFile wad = new WadFile("doom2.wad");
	Set<DoomSector> set = new HashSet<>();
	for (DoomSector sector : wad.getDataAs("SECTORS", "MAP29", DoomSector.class, DoomSector.LENGTH))
		if (sector.getFloorTexture().equals("BLOOD1"))
			set.add(sector);
	wad.close();

Open `MAP01` in `HEXEN.WAD` and fetch all things that have a special.

	WadFile wad = new WadFile("hexen.wad");
	Set<HexenThing> set = new HashSet<>();
	for (HexenThing thing : wad.getDataAs("THINGS", "MAP01", HexenThing.class, HexenThing.LENGTH))
		if (thing.getSpecial() > 0)
			set.add(thing);
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
