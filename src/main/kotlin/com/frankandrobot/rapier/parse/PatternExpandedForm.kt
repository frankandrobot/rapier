package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternElement
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.PatternList
import java.util.*


/**
 * Recall, a Pattern is a list of PatternElements (which are PatternItems or PatternLists).
 * This class expands a Pattern into one or more lists of PatternItems.
 *
 * # Examples
 *
 * ## Example 1
 *
 * pattern = [{word: foo}, {word: bar}] // two PatternItems
 *
 * expands to a single ParsePatternItemList:
 *
 * -  [{word: foo}, {word: bar}]
 *
 *
 * ## Example 2
 *
 * pattern = [{word: foo}, {word: bar, length: 1}] // PatternItem followed by PatternList
 *
 * expands to the following ParsePatternItemLists:
 *
 * -  [{word: foo}]
 * -  [{word: foo}, {word: bar}]
 *
 *
 * ## Example 3
 *
 * pattern = [{word: 'foo', length: 1}, {word: 'bar', length: 1}] // two PatternLists
 *
 * expands to the following ParsePatternItemLists:
 *
 * -  [] //empty
 * -  [{word: foo}]
 * -  [{word: bar}]
 * -  [{word: foo}, {word: bar}]
 *
 * # Too Inefficient?
 *
 * Note that while this is very inefficient, this is fine because we expect these to be
 * local to a filler...hence, small.
 *
 * Note also that it's cached.
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

      _expansion.forEach{ pattern -> pattern().add(patternElement) }
    }
    else if (patternElement is PatternList) {

      split(patternElement)
    }
  }


  private fun split(patternList : PatternList) {

    val patternListExpansion = patternList.expandedForm
    val newSize = _expansion.size * patternListExpansion.size
    val prevPatterns = _expansion

    _expansion = ArrayList<ParsePatternItemList>(newSize)

    patternListExpansion.forEach{ newItemList ->
      prevPatterns.forEach{ prevItemList ->

        val clone = prevItemList().clone() as ArrayList<PatternItem>

        clone.addAll(newItemList())

        _expansion.add(ParsePatternItemList(clone))
      }
    }
  }

  /**
   * Returns a cached copy of the expansion.
   */
  operator fun invoke() = expansion

  operator fun get(i : Int) = expansion[i]
}
