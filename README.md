# rapier
## Development
### Intellij 15 Setup

-    `Import Project` then `Import project from external model` > `Gradle`
      -    Select "Use auto-import"
      -    Select "Use default gradle (recommended)"
      -    Select Java 1.8 as Gradle JVM.
-    Setup directories:
      -    Mark `out` directory as excluded.
      -    Mark `src/main/kotlin` directory as sources root.
      -    Mark `src/main/resources` directory as resources.
      -    Mark `src/test/kotlin` directory as test.
      -    Do NOT mark `src` as the sources root. There is a bug in Intellij
           where it does not setup the project structure correctly.

## Installation

For now, you'll either have to build the library yourself or download a version of the 
library from here. We'll eventually [setup this Github repo as a maven repo]
(http://stackoverflow.com/questions/14013644/hosting-a-maven-repository-on-github) so 
that you'll be able to use this library in the usual maven/gradle workflow; 
however, this task has low priority so no ETA.  

### Build

```bash
gradle build # the JAR will be in the build/libs folder
```

## Usage

Except for [funktionale](https://github.com/MarioAriasC/funKTionale), this library has 
no dependencies. You'll have to parse example files yourself.


