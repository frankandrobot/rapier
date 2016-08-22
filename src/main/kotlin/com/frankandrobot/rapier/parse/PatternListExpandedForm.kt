package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.PatternList
import java.util.*


/**
 * The collection of all possible PatternItem lists the PatternList represents.
 * For example, the pattern list {word:[foo, bar], length: 2} expands to:
 *
 * - `` (empty)
 * - `foo`
 * - `foo`, `foo`
 * - `foo`, `bar`
 * - `bar`
 * - `bar`, `foo`
 * - `bar`, `bar`
 *
 * Each element of the collection is of type ParsePatternItemList.
 */
class PatternListExpandedForm(private val patternList : PatternList) {

  val expansion: ArrayList<ParsePatternItemList> by lazy { expand(patternList) }

  /**
   * Converts the PatternList into a collection of PatternItemLists i.e.,
   * it expands the pattern list into lists of pattern items.
   *
   * Ex: {word: foo, length: 2} => [], [{word: foo}], [{word: foo}, {word: foo}]
   */
  internal fun expand(patternList : PatternList) : ArrayList<ParsePatternItemList> {

    val patternItem = PatternItem(
      patternList.wordConstraints,
      patternList.posTagContraints,
      patternList.semanticConstraints
    )

    return (0..patternList.length).fold(ArrayList<ParsePatternItemList>(), { total, count ->

      if (count === 0) { total.add(ParsePatternItemList()) }
      else { total.add(ParsePatternItemList((1..count).map { patternItem } as ArrayList<PatternItem>)) }

      total
    })
  }

  operator fun invoke() = expansion
}