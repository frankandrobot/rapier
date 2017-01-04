/*
 *    Copyright 2016 Uriel Avalos
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.PatternList
import java.util.*


/**
 * A PatternList expands to a collection of PatternItem lists.
 * For example, the PatternList {word:[foo, bar], length: 2} expands to:
 *
 * - `` (empty)
 * - `foo`
 * - `bar`
 * - `foo`, `foo`
 * - `foo`, `bar`
 * - `bar`, `foo`
 * - `bar`, `bar`
 *
 * Note that the empty expansion is also included. This corresponds to an
 * expansion of length 0.
 */
class ParsePatternListExpandedForm(private val patternList : PatternList) {

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
      patternList.posTagConstraints,
      patternList.semanticConstraints
    )

    return (0..patternList.length).fold(ArrayList<ParsePatternItemList>(), { total, count ->

      if (count === 0) { total.add(ParsePatternItemList()) }
      else { total.add(ParsePatternItemList((1..count).map { patternItem } as ArrayList<PatternItem>)) }

      total
    })
  }

  /**
   * returns cached version of the expansion
   */
  operator fun invoke() = expansion
}
