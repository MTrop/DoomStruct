Doom Struct (C) 2015-2019 
=========================
by Matt Tropiano et al. (see AUTHORS.txt)


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
