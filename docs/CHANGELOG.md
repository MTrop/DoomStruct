Doom Struct (C) 2015-2020 
=========================
by Matt Tropiano et al. (see AUTHORS.txt)


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
