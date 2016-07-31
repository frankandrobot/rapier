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
 * expands to the following ParsePatternItemList:
 *
 * -  [{word: foo}, {word: bar}]
 *
 *
 * Example:
 * [{word: foo}, {word: bar, length: 1}] // pattern item followed by pattern list
 *
 * expands to the following ParsePatternItemLists:
 *
 * -  [{word: foo}]
 * -  [{word: foo}, {word: bar}]
 *
 *
 * Example:
 * [{word: 'foo', length: 1}, {word: 'bar', length: 1}] // two pattern lists
 *
 * expands to the following ParsePatternItemLists:
 *
 * -  []
 * -  [{word: foo}]
 * -  [{word: bar}]
 * -  [{word: foo}, {word: bar}]
 *
 */
data class PatternExpandedForm(private val pattern : Pattern) {

  private var _expansion = ArrayList<ParsePatternItemList>(pattern().size)

  /**
   * By making this lazy, you guarantee that the construction is synchronized across threads
   */
  val expansion: ArrayList<ParsePatternItemList> by lazy {

    _expansion.add(ParsePatternItemList())

    pattern().forEach{ patternElement -> add(patternElement) }

    _expansion
  }

  private fun add(patternElement: PatternElement) {

    if (patternElement is PatternItem) {

      _expansion.forEach{ pattern -> pattern.items.add(patternElement) }
    }
    else if (patternElement is PatternList) {

      split(patternElement)
    }
  }

  private fun split(patternList : PatternList) {

    val patternListExpansion = patternList.expandedForm()
    val newSize = _expansion.size * patternListExpansion.size
    val prevPatterns = _expansion

    _expansion = ArrayList<ParsePatternItemList>(newSize)

    patternListExpansion.forEach{ newItemList ->
      prevPatterns.forEach{ prevItemList ->

        val clone = prevItemList.items.clone() as ArrayList<PatternItem>

        clone.addAll(newItemList.items)

        _expansion.add(ParsePatternItemList(clone))
      }
    }
  }

  operator fun invoke() = expansion

  operator fun get(i : Int) = expansion[i]
}
