# Doom Struct

Copyright (c) 2015 Matt Tropiano  

### NOTICE

This library is an offshoot/rewrite of [Black Rook Software's Doom Struct Project](https://github.com/BlackRookSoftware/Doom), 
of which I have been granted rights to rewrite (the rights were granted to myself BY myself).

All deprecated classes from the origin project will not be in this one.
All end users are encouraged to switch to this one, as this project will be 
actively maintained.

### Releases

Releases will be found [here]. (link not working, yet - no releases).

### Required Libraries

Black Rook Commons 2.20.2+  
[https://github.com/BlackRookSoftware/Common](https://github.com/BlackRookSoftware/Common)

Black Rook Common I/O 2.3.0+  
[https://github.com/BlackRookSoftware/CommonIO](https://github.com/BlackRookSoftware/CommonIO)

Black Rook Common Lang 2.3.0+  
[https://github.com/BlackRookSoftware/CommonLang](https://github.com/BlackRookSoftware/CommonLang)

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

### Other

This program/library and the accompanying materials
are made available under the terms of the GNU Lesser Public License v2.1
which accompanies this distribution, and is available at
http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html

A copy of the LGPL should have been included in this release (LICENSE.txt).
If it was not, please contact me for a copy, or to notify me of a distribution
that has not included it. 
