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

import com.frankandrobot.rapier.pattern.*
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class PatternExpandedFormSpec : Spek ({

  describe("ExpandedPattern") {

    val aPatternItem = { PatternItem(words("one")) }
    val anotherPatternItem = { PatternItem(words("two")) }

    val anyList = { PatternList(WordConstraint("one")) }
    val anyOtherList = { PatternList(WordConstraint("two")) }

    val patternSingleItem = {Pattern(aPatternItem())}
    val patternMultiItem = {Pattern(aPatternItem(), anotherPatternItem())}

    val patternSingleList = {Pattern(anyList())}
    val patternItemList = {Pattern(aPatternItem(), anyOtherList())}


    it("should expand a Pattern of one PatternItem into a single list of " +
      "PatternItems with one PatternItem") {

      val result = PatternExpandedForm(patternSingleItem())

      assertEquals(1, result().size)
      assertEquals(ParsePatternItemList(aPatternItem()), result[0])
    }


    it("should expand a Pattern of two PatternItems into a single list of " +
      "PatternItems with two PatternItems") {

      val result = PatternExpandedForm(patternMultiItem())

      assertEquals(1, result().size)
      assertEquals(ParsePatternItemList(aPatternItem(), anotherPatternItem()), result[0])
    }


    it("should expand a Pattern of one PatternList into two lists") {
      val result = PatternExpandedForm(patternSingleList())

      assertEquals(2, result().size)
      assertEquals(ParsePatternItemList(), result[0])
      assertEquals(ParsePatternItemList(aPatternItem()), result[1])
    }


    it("should expand a pattern with an item and a list") {
      val result = PatternExpandedForm(patternItemList())

      assertEquals(2, result().size)
      assertEquals(ParsePatternItemList(aPatternItem()), result[0])
      assertEquals(ParsePatternItemList(aPatternItem(), anotherPatternItem()), result[1])
    }
  }
})
