package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternElement
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.PatternList
import java.util.*

/**
 * Expands pattern element lists into all the possible combinations.
 * (This is fine because we expect these to be local to a filler...hence, small)
 *
 * Example:
 * [{word: 'foo', length: 1}, {word: 'bar', length: 1}]
 *
 * expands to the pattern items:
 *
 * {word: foo}
 * {word: bar}
 * [{word: foo}, {word: bar}]
 */
data class ExpandedPatterns(val pattern : Pattern) {

  var patterns : ArrayList<PatternItemList>

  init {

    patterns = ArrayList<PatternItemList>(pattern.patternElements.size)

    patterns.add(PatternItemList())

    pattern.patternElements.forEach{ patternElement -> add(patternElement) }
  }

  private fun add(patternElement: PatternElement) {

    if (patternElement is PatternItem) {

      patterns.forEach{ pattern -> pattern.patternItemList.add(patternElement) }
    }
    else if (patternElement is PatternList) {

      split(patternElement)
    }
  }

  private fun split(patternList : PatternList) {

    val patternItemLists = patternList.expand()
    val newSize = patterns.size * patternItemLists.size
    val oldPatterns = patterns

    patterns = ArrayList<PatternItemList>(newSize)

    patternItemLists.forEach{ newPattern ->

      oldPatterns.forEach{ oldPattern ->

        val clone = oldPattern.patternItemList.clone() as ArrayList<PatternItem>

        clone.addAll(newPattern.patternItemList)

        patterns.add(PatternItemList(clone))
      }
    }

  }
}
