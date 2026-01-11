Doom Struct (C) 2015-2026
=========================
by Matt Tropiano et al. (see AUTHORS.txt)


Changed in 2.21.0
-----------------

- `Fixed` Pictures would nullify the 255th palette index as a viable pixel color.
- `Deprecated` Palette.getNearestColorIndex(...) calls that exclude the 255th index.


Changed in 2.20.0
-----------------

- `Added` ZDoom Activation types to HexenLinedef.


Changed in 2.19.5
-----------------

- `Fixed` Allow C-style comments in UMAPINFO.


Changed in 2.19.4
-----------------

- `Fixed` WadFile.deleteEntry(int) did not properly set up a file for size truncation after entry flush.


Changed in 2.19.3
-----------------

- `Changed` Spaces are now allowed in Texture/Entry names.


Changed in 2.19.2
-----------------

- `Fixed` HexenMapInfo.nextTokens() would skip `sky1` or `sky2` entries with a single value.
- `Added` EternityMapInfo.getEntry(String).
- `Added` UniversalMapInfo.getEntry(String).


Changed in 2.19.1
-----------------

- `Added` TextObject.read(Class<TO>, InputStream)
- `Added` TextObject.read(Class<TO>, InputStream, Charset)
- `Added` TextObject.read(Class<TO>, File)
- `Added` TextObject.read(Class<TO>, File, Charset)


Changed in 2.19.0
-----------------

- `Added` Text.
- `Added` HexenMapInfo.
- `Added` EternityMapInfo.
- `Added` ZDoomMapInfo.
- `Added` UniversalMapInfo.


Changed in 2.18.1
-----------------

- `Changed` TextureUtils.importTextureSet(Wad) now considers sets with TEXTURE2/PNAMES only as valid.


Changed in 2.18.0
-----------------

- `Fixed` PatchNames.readBytes(...) is now tolerant of PNAMES lumps that may contain duplicate patch names (since it can corrupt a TextureSet).
- `Added` PatchNames.add(int, boolean) for allowing duplicate patch names.
- `Changed` Slightly more efficient texture exporting in TextureSet.


Changed in 2.17.0
-----------------

- `Added` TextureSet.replaceTextureByName(String).


Changed in 2.16.0
-----------------

- `Fixed` GraphicUtils.createPicture(PNGPicture, Palette, Colormap) should not have considered palette index 255 for a viable match.
- `Added` Palette.getNearestColorIndex(int, int, int, boolean).
- `Added` Palette.getNearestColorIndex(int, boolean).


Changed in 2.15.8
-----------------

- `Fixed` Some incongruities in allowed ranges in `DoomSector`.


Changed in 2.15.7
-----------------

- `Changed` Added more legal characters for `NameUtils.toValidEntryName(String)`.


Changed in 2.15.6
-----------------

- `Changed` Added more legal characters for `NameUtils.isValidEntryName()`.


Changed in 2.15.5
-----------------

- `Changed` Omit split at 128 pixels for graphics export in Picture.


Changed in 2.15.4
-----------------

- `Fixed` Tall patches/pictures were still mangled on export. (Issue #20)


Changed in 2.15.3
-----------------

- `Fixed` Palette.getColor() did not convert palette components properly. (Issue #18)


Changed in 2.15.2
-----------------

- `Fixed` Proper padding values on DMX sound export. (Issue #19)


Changed in 2.15.1
-----------------

- `Changed` NameUtils: Valid entry names and texture names are virtually the same.


Changed in 2.15.0
-----------------

- `Fixed` WadBuffer.unmapEntries() did not update the header.
- `Added` WadBuffer now has capacity control methods/constructors (Enhancement #15).
- `Changed` MapUtils.getAllMapIndices() was changed to include missing `GL_` entries. (Issue #16).


Changed in 2.14.2
-----------------

- `Changed` Added `+` as a valid character for entries and textures (PR #12).


Changed in 2.14.1
-----------------

- `Fixed` Force dot `.` as the decimal separator in the UDMF parser. It used to rely on the locale's separator
  by default. This is wildly incorrect.


Changed in 2.14.0
-----------------

- `Added` WadFile constructors can now specify read or write mode.
- `Changed` WadFiles are now opened in an appropriate mode due to file read/write capability by default.


Changed in 2.13.0
-----------------

- `Fixed` GraphicUtils did not set transparent pixels when converted from full color images.
- `Fixed` Some GraphicUtils method documentation was either misleading or unclear.
- `Fixed` Tall Patch Graphics are now supported. (Issue #9)
- `Added` BinaryObject file-reading methods for convenience.
- `Added` TextObject file-reading methods for convenience.
- `Added` GraphicUtils methods for converting PNGPictures and preserving offsets for Pictures.
- `Changed` IndexedGraphic interface is now deprecated.


Changed in 2.12.0
-----------------

- `Removed` ALL Deprecated fields and methods. If you need a transitional version of this library, use version `2.11.1`.


Changed in 2.11.1
-----------------

- `Fixed` WadFile.writeHeader() did not write the IWAD/PWAD magic number on change.


Changed in 2.11.0
-----------------

- `Fixed` Palette.getNearestColorIndex(), because apparently it never worked.


Changed in 2.10.1
-----------------

- `Fixed` WadBuffer.WadBufferInputStream may sometimes return 0xff (-1) itself, killing the stream prematurely. This has been fixed.


Changed in 2.10.0
-----------------

- `Added` TextureUtils.TextureCopier methods: copyTexture() with replace, ignoreFlat().
- `Fixed` NameUtils.isValidEntryName() and NameUtils.toValidEntryName() now accept the carat `^`.


Changed in 2.9.1
----------------

- `Added` Missing Hexen UDMF Thing attribute.
- `Added` Additional methods to UDMFWriter.
- `Changed` UDMFWriter.writeObject(...) now has a nullable count field (to match docs).


Changed in 2.9.0
----------------

- `Added` ZDoom UDMF attributes.
- `Fixed` Safer input stream/reader use in Wad default methods.
- `Changed` Altered lots of map-centric class methods in the name of "naming uniformity."
- `Changed` Some MathUtils moved to `net.mtrop.doom.util`.


Changed in 2.8.1
----------------

- `Added` Additional missing flags and UDMF attributes.


Changed in 2.8.0
----------------

- `Added` More common UDMF attributes.
- `Fixed` Safer input stream use in DoomPK3.
- `Changed` Clarified a Strife Thing flag (and deprecated the other). 
- `Changed` Modified Scanner and InlineScanner to not repeat reads on testing for next.
- `Changed` HexenLinedef: Removed use of separate field for activation type. 


Changed in 2.7.0
----------------

- `Added` Wad: More addData(...) methods.
- `Added` WadFile bulk add methods, deprecated "noFlush" variant methods. 
- `Changed` Deprecated: Wad.addAllData*(...) methods.


Changed in 2.6.1
----------------

- `Changed` Wad: Made addData/addAllData methods call addDataAt/addAllDataAt by default.
- `Changed` Some Javadocs clarifications and fixes.


Changed in 2.6.0
----------------

- `Added` MapElementView.
- `Added` To WadUtils: createWadAnd(...)
- `Added` TextureUtils.createTextureCopier().
- `Fixed` WadUtils: openWadAnd(...), openWadAndGet(...), openWadAndExtractTo(...), openWadAndExtractBuffer(...) now does not throw RuntimeException.
- `Fixed` TextureUtils.importTextureSet(...) did not throw TextureException if it did not find TEXTURE1.
- `Fixed` TextureSet.addTexture(...) did not deep-copy the input texture. Now it does.


Changed in 2.5.0
----------------

- `Added` To Wad: addFrom(...), addFromAt(...)
- `Added` To WadUtils: openWadAnd(...), openWadAndGet(...), openWadAndExtractTo(...), openWadAndExtractBuffer(...)
- `Changed` Moved MathUtils from `util` to `struct` since it is not meant to be used outside of the library.
- `Removed` HexenThing "isXXX()" boolean functions. They didn't work, anyway. [Issue 5](https://github.com/MTrop/DoomStruct/issues/5)


Changed in 2.4.0
----------------

- `Added` Wad.fetchContent(int, int, byte[], int) and all of its implementations.
- `Added` Wad.fetchData(...).
- `Added` Wad.replaceEntry(...) variants.
- `Added` UDMFScanner - a pull-oriented UDMF reader.
- `Fixed` Wad.unmapEntries(int, WadEntry...) implementations did not append excess entries.
- `Changed` WadFile and WadBuffer's implementations of Wad.getContent(...) have been removed (new interface default is used).
- `Changed` `net.mtrop.doom.io` refactor - this package was moved into `struct`, since it should not be used outside of this library.


Changed in 2.3.0
----------------

- `Added` Wad.isWAD(File).


Changed in 2.2.0
----------------

- `Added` SerialWriter.writeBytes(OutputStream out, byte[], int, int).
- `Added` Wad.getContent(int, int) : byte[]. Implementing classes changed as well.
- `Added` Wad.add[All]Data[At] (..., BinaryObject) variants.
- `Added` Wad.addData[At] (..., TextObject, Charset) variants.
- `Added` Palette mixing functions plus copying.
- `Added` WadFile.addAllData[At] (...) "No Flush" variants.
- `Added` GraphicUtils.setColormap(...).
- `Added` GraphicUtils.createColormapsFromGraphic(...).
- `Fixed` GraphicUtils.createPicture(...) / GraphicUtils.createFlat(...) handles non-opaque pixels accurately.
- `Changed` Some test refactoring.
- `Changed` WadBuffer - made some protected fields private and merged the header in with content.
- `Changed` Flat and Picture now extend IndexedGraphic.
- `Changed` WadUtils.cleanEntries(..) shortened.
- `Changed` WadUtils.extract(..) made more efficient.
- `Changed` Increased BinaryObject initial byte buffer size from 128 to 512.
- `Changed` Default Wad/DoomPK3.getReader(...) implementations return BufferedReaders, now.


Changed in 2.1.1
----------------

- `Fixed` Wad.transformData(..., length, ...) did not scan through all objects.
- `Fixed` WadBuffer.replaceEntry() did not write data back correctly.
- `Fixed` BinaryObject.InlineScanner did not read objects properly.


Changed in 2.1.0
----------------

- `Added` Wad.transformData(...), Wad.transformTextData()
- `Added` WadEntry.withNewXXX()
- `Added` WadFile.extract(...)
- `Added` WadBuffer.extract(...)
- `Added` MapUtils.getMapEntries(...)
- `Fixed` WadBuffer.deleteEntry(...) now adjusts offsets properly.
- `Fixed` MUS.NotePlayEvent had shadowed the "note" field in NoteEvent.
- `Changed` Reduced visibility of some methods of WadEntry and it is no longer a BinaryObject.
- `Changed` Better handling of MapUtils.getMapEntryCount(...).


Changed in 2.0.0
----------------

- New base release. Fixed bugs from previous release.
