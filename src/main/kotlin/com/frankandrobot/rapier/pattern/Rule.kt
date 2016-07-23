package com.frankandrobot.rapier.pattern

import com.frankandrobot.rapier.parse.PatternItemList
import java.util.*


/**
 * Expands the pattern element lists into all the possible combinations.
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
internal data class ExpandedPatterns(val pattern : Pattern) {

  var patterns : ArrayList<PatternItemList>

  init {

    patterns = ArrayList<PatternItemList>(pattern.patternElements.size)

    patterns.add(PatternItemList())

    pattern.patternElements.forEach{ patternElement -> add(patternElement) }
  }

  inline fun add(patternElement: PatternElement) {

    if (patternElement is PatternItem) {

      patterns.forEach{ pattern -> pattern.patternItemList.add(patternElement) }
    }
    else if (patternElement is PatternList) {

      split(patternElement)
    }
  }

  inline fun split(patternList : PatternList) {

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

class Rule(val preFiller: Pattern, val filler: Pattern, val postFiller: Pattern) {

  internal val expandedPrefillerPatterns : ExpandedPatterns by lazy {

    ExpandedPatterns(preFiller)
  }

  internal val expandedFillerPatterns : ExpandedPatterns by lazy {

    ExpandedPatterns(filler)
  }

  internal val expandedPostfillerPatterns : ExpandedPatterns by lazy {

    ExpandedPatterns(postFiller)
  }

//  fun match(doc : Document) : List<SlotFiller> {
//
//    val foo = doc.tokens.iterator()
//  }

  private fun _match(tokens : List<Token>) {

    tokens.iterator()
  }

  private fun generateCombinations(pattern : Pattern) {

  }
}
