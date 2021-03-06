# Library Philosophy

This file is for answering questions around some of this library's design. This can also
serve as a reference point for why certain parts of the library are packaged a certain
way, or explaining choices made during creation, so that it can guide future decisions.

And if, down the line, a better approach is proposed, at least there's a paper trail for
the decisions already made so that more *educated* decisions can be made.


### What is the primary goal of this project?

To read Doom Engine data structures and allow the users of the library to use that data 
to create or manipulate other Doom-related data. Features that ease "map editing" or 
advanced graphics or sound manipulation or other decision-making logic layers are not considered 
to be within the scope of this project.


### Lots of things are somewhat "low-level." Why?

Personal API philosophy. Assuming the intent of the user of an API will limit its design and
its usefulness in application. Better to expose low level functionality and wrap in "smarter"
structures, rather than ease-of-use first, then down to bits and bytes. Then, you can still have
flexibility as well as convenience. 


### Why are WadEntries immutable?

WadEntries are immutable because they can be used by other Wads - changing an entry's values
like offset, length, or name could pollute use of an entry whose reference is used in many places.


### Why Doom/Hexen/UDMF Linedef/Thing objects? Why not a single object for all things/linedefs?

Those object types are first and foremost abstractions of data representation in Doom, 
and so all of the limitations of that data must be managed by them as well. Doing this
ensures that user errors are caught sooner (at read and incidental modification) rather 
than later (when that data needs to be written back to a binary or textual format that 
Doom engines can understand).


### Why are bitflags a single field on Doom/Hexen things/linedefs and not broken out into significant is/set Getter/Setters?

Again, those object types are first and foremost abstractions of data representation in Doom.
The meaning placed on the bitflag section of these objects are *per game or source port* and not according 
to the data model itself. So, a mechanism for checking these flags is necessary, but not down to the class level,
where it can mislead those unfamiliar with other engines or ports.


### In Wad, why are there no "entry name seeking" variant methods for replaceEntry() / renameEntry() / removeEntry() / deleteEntry()? 

These methods alter data, and require you to be more precise about what you are changing. Most of
the other methods that do "seeking" are read-only - finding the wrong entry to read has no direct
permanent impact.

### How come MapElementView takes *Ints* instead of Enums for enumerating field types?

Java Enums cannot be extended. If a developer creates a class that is an extension of another MapElementView, the same
values can be used for the enumerated fields instead of needing to potentially create another full Enum set that
the new view class would accept. While this approach is a little more error-prone for end-users, this would still enable developers
to add only what they would need to implement and call the parent implementation for handling an unknown field type.
