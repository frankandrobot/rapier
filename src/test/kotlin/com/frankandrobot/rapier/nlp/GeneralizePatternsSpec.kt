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

package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.meta.RapierParams
import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.PatternList
import com.frankandrobot.rapier.pattern.words
import com.frankandrobot.rapier.patternOfWordItems
import org.jetbrains.spek.api.Spek
import java.util.*
import kotlin.test.assertEquals


class GeneralizePatternsSpec : Spek ({

  describe("findExactMatchIndices") {

    it("should work when one pattern is empty") {
      val emptyPat = Pattern()
      val result = findExactMatchIndices(emptyPat, patternOfWordItems("a", "b", "c"))

      assertEquals(2, result.size)
    }

    it("should ignore partial matches") {
      val left = Pattern(PatternItem(listOf("x"), listOf("tag1")))
      val right = patternOfWordItems("x")
      val result = findExactMatchIndices(left, right)

      assertEquals(2, result.size)
    }

    it("should find exact matches") {
      val left = Pattern(PatternItem(listOf("x"), listOf("tag1")))
      val right = Pattern(PatternItem(listOf("x"), listOf("tag1")))
      val result = findExactMatchIndices(left, right)

      assertEquals(3, result.size)
    }

    describe("match order") {

      it("should return the first match for x") {
        val left = patternOfWordItems("1", "x", "2")
        val right = patternOfWordItems("3", "x", "x")
        val result = findExactMatchIndices(left, right)

        assertEquals(MatchIndices(leftIndex = 1, rightIndex = 1), result[1])
        assertEquals(3, result.size)
      }

      it("should NOT match x since y does not have a chance to match") {
        val left = patternOfWordItems("1", "x", "y")
        val right = patternOfWordItems("3", "4", "5", "x")
        val result = findExactMatchIndices(left, right)

        assertEquals(2, result.size)
      }

      it("should match y but not x") {
        val left = patternOfWordItems("1", "x", "y")
        val right = patternOfWordItems("3", "4", "5", "y", "x")
        val result = findExactMatchIndices(left, right)

        assertEquals(3, result.size)
        assertEquals(MatchIndices(leftIndex = 2, rightIndex = 3), result[1])
      }
    }

    describe("example") {

      val left = patternOfWordItems("a", "b", "c")
      val right = patternOfWordItems("1", "a", "3", "b", "c")

      var result = ArrayList<MatchIndices>()

      beforeEach {
        result = findExactMatchIndices(left, right)
      }

      it("should return 3 matches plus start/end fake matches") {
        assertEquals(5, result.size)
      }

      it("should return start-fake match at beginning") {
        assertEquals(MatchIndices(leftIndex = -1, rightIndex = -1), result[0])
      }

      it("should return end-fake match at end") {
        assertEquals(MatchIndices(leftIndex = 3, rightIndex = 5), result[4])
      }

      it("should match a, b, c") {
        assertEquals(MatchIndices(leftIndex = 0, rightIndex = 1), result[1])
        assertEquals(MatchIndices(leftIndex = 1, rightIndex = 3), result[2])
        assertEquals(MatchIndices(leftIndex = 2, rightIndex = 4), result[3])
      }
    }
  }

  describe("partitionByExactMatches") {

    it("should work when one of the patterns is empty") {
      val left = Pattern()
      val right = patternOfWordItems("a")
      val result = partitionByExactMatches(left, right)

      assertEquals(Pair(left, right), result[0])
    }

    it("should work when no partitions between matches") {
      val left = patternOfWordItems("x", "y")
      val right = patternOfWordItems("x", "y")
      val result = partitionByExactMatches(left, right)

      assertEquals(2, result.size)
      assertEquals(Pair(patternOfWordItems("x"), patternOfWordItems("x")), result[0])
      assertEquals(Pair(patternOfWordItems("y"), patternOfWordItems("y")), result[1])
    }

    describe("example") {

      val left = patternOfWordItems("x", "y", "z")
      val right = patternOfWordItems("1", "x", "2", "3", "z", "4")

      var result = emptyList<Pair<Pattern,Pattern>>()

      beforeEach {
        result = partitionByExactMatches(left, right)
      }

      it("should find 5 partitions") {
        assertEquals(5, result.size)
      }

      it("should match 1 to an empty pattern") {
        assertEquals(Pair(Pattern(), patternOfWordItems("1")), result[0])
      }

      it("should match x") {
        assertEquals(Pair(patternOfWordItems("x"), patternOfWordItems("x")), result[1])
      }

      it("should match y to 2 and 3") {
        assertEquals(Pair(patternOfWordItems("y"), patternOfWordItems("2", "3")), result[2])
      }

      it("should match z") {
        assertEquals(Pair(patternOfWordItems("z"), patternOfWordItems("z")), result[3])
      }

      it("should match 4 to an empty patten") {
        assertEquals(Pair(Pattern(), patternOfWordItems("4")), result[4])
      }
    }
  }

  describe("generalize") {

    describe("example 2") {
      val params = RapierParams()

      var a : Pattern
      var b : Pattern
      var result = emptyList<Pattern>()

      beforeEach{
        a = patternOfWordItems("kansas", "city")
        b = patternOfWordItems("atlanta")
        result = generalize(a, b, params = params)
      }

      it("should produce two patterns") {
        assertEquals(2, result.size)
      }

      it("should produce two patterns each with one pattern list of length 2") {
        result.forEach{ pattern ->
          assertEquals(1, pattern.length)
          assertEquals(true, pattern()[0] is PatternList)
          assertEquals(2, pattern()[0].length)
        }
      }

      it("should produce two pattern lists, one that is the union and the other an " +
        "unconstrained pattern list") {

        val union = Pattern(PatternList(words("kansas", "city", "atlanta"), length = 2))
        val unconstrained = Pattern(PatternList(length = 2))
        val patterns = listOf(result[0]) + result[1]
        assertEquals(true, patterns.contains(union))
        assertEquals(true, patterns.contains(unconstrained))
      }
    }
  }
})
