# Versioning

This project makes use of Semantic Versioning as its guidelines for advancing versions. The version of this project is {Major}.{Minor}.{Patch} with an optional fourth digit ({Info}) for tiny changes that do not affect program logic (but possibly documentation/build).

**Major** changes entail refactorings and removal of deprecated methods/classes/fields ("Will updating cause errors in dependent code?") 

**Minor** changes entail additions or deprecations of public/protected/default fields/methods/classes ("Will updating keep things working in dependent code?")

**Patch** changes entail logic changes within methods and classes that are not visible to end users.

**Info** changes are changes to comments or documentation - no logic changes (unless it's to the build scripts).

The "highest" type of change dictates what number is advanced. The rest of the lower digits are set to 0 (or removed, if "info" change).

