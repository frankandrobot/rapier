package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternElement
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.PatternList
import java.util.*


/**
 * Expands Patterns into PatternItemLists.
 * (As will become clear, this is fine because we expect these to be local to a filler...hence, small)
 *
 * Example:
 * [{word: foo}, {word: bar}] // two pattern items
 *
 * expands to the following PatternItemList:
 *
 * -  [{word: foo}, {word: bar}]
 *
 *
 * Example:
 * [{word: foo}, {word: bar, length: 1}] // pattern item followed by pattern list
 *
 * expands to the following PatternItemLists:
 *
 * -  [{word: foo}]
 * -  [{word: foo}, {word: bar}]
 *
 *
 * Example:
 * [{word: 'foo', length: 1}, {word: 'bar', length: 1}] // two pattern lists
 *
 * expands to the following PatternItemLists:
 *
 * -  []
 * -  [{word: foo}]
 * -  [{word: bar}]
 * -  [{word: foo}, {word: bar}]
 *
 */
data class PatternExpandedForm(private val pattern : Pattern) {

  private var patterns : ArrayList<PatternItemList>

  init {

    patterns = ArrayList<PatternItemList>(pattern.patternElements.size)

    patterns.add(PatternItemList())

    pattern.patternElements.forEach{ patternElement -> add(patternElement) }
  }

  private fun add(patternElement: PatternElement) {

    if (patternElement is PatternItem) {

      patterns.forEach{ pattern -> pattern.items.add(patternElement) }
    }
    else if (patternElement is PatternList) {

      split(patternElement)
    }
  }

  private fun split(patternList : PatternList) {

    val patternItemLists = patternList.expandedForm()
    val newSize = patterns.size * patternItemLists.size
    val oldPatterns = patterns

    patterns = ArrayList<PatternItemList>(newSize)

    patternItemLists.forEach{ newPattern ->

      oldPatterns.forEach{ oldPattern ->

        val clone = oldPattern.items.clone() as ArrayList<PatternItem>

        clone.addAll(newPattern.items)

        patterns.add(PatternItemList(clone))
      }
    }
  }

  operator fun invoke() = patterns
  operator fun get(i : Int) = patterns[i]
}
