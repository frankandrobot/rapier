# rapier
## Intellij 15 Setup

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
