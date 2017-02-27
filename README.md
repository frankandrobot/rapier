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

## Building

```bash
gradle build # the JAR will be in the build/libs folder
```

## Usage

Except for [funktionale](https://github.com/MarioAriasC/funKTionale) and several 
natural language processing libraries, this library has no dependencies. For now, 
you'll need to use Kotlin (or any JVM language) to load this library and process the 
data. However, a command line interface is in the works. In the mean time, checkout 
[RapierSpec.kt](src/test/kotlin/RapierSpec.kt) for sample usage.

1.   Define a BlankTemplate. 
     
     ```kotlin
     val blankTemplate = BlankTemplate(
       name = "test",
       slots = slotNames("title")
     )
     ```

2.   Create a Document.

     ```kotlin
     val document = Document("""
     Looking for a Senior Software Engineer and a QA developer.
     """)
     ```

3.   Fill out a template for the given slot using the given document.

     ```kotlin
      val filledTemplate = FilledTemplate(slots(
        SlotName("title") to slotFillers(
          wordTokens("Senior", "Software", "Engineer"),
          wordTokens("QA", "developer")
        )
     ))
     ```

4.   Put everything together as an Example.

     ```kotlin
      val filledTemplate = Example(
        blankTemplate,
        document,
        filledTemplate
      )
     ```

5.   Repeat Steps 2--4 on other documents.
6.   Setup the desired Rapier params. Checkout 
     [RapierParams.kt](src/main/kotlin/com/frankandrobot/rapier/meta/RapierParams.kt)
     for a list of all avail params.

     ```kotlin
     val params = RapierParams(
       compressionFails = 7,
       metricMinPositiveMatches = 1,
       compressionPriorityQueueSize = 5
     )
     ```
6.   Run rapier on the Examples.

     ```kotlin
     val examples = Examples(listOf(example))
     val learnedRules = 
       rapier(blankTemplate, examples = examples, params = params)
         .normalize() // remove useless rules and convert to simpler rule format
     ```

7.   Extract information from new documents using the learned rules.
  
     ```kotlin
     val rulesForTitleSlot = learnedRules[SlotName("title")]
     val results = rulesForTitleSlot.findMatches(aDocument)
     println(result[SlotName("title")])
     ```
     
     Or find all the matches for each slot in one shot.
     
     ```kotlin
     val allResults = learnedRules.all().findMatches(aDocument)
     println(allResults[SlotName("title")])
     ```
