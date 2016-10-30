package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternItem
import org.jetbrains.spek.api.Spek
import java.util.*
import kotlin.test.assertEquals


class GeneralizePatternsSpec : Spek ({

  describe("findExactMatchIndices") {

    it("should work when one pattern is empty") {
      val emptyPat = Pattern()
      val result = findExactMatchIndices(emptyPat, Pattern("a", "b", "c"))

      assertEquals(2, result.size)
    }

    it("should ignore partial matches") {
      val left = Pattern(PatternItem(listOf("x"), listOf("tag1")))
      val right = Pattern("x")
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
        val left = Pattern("1", "x", "2")
        val right = Pattern("3", "x", "x")
        val result = findExactMatchIndices(left, right)

        assertEquals(MatchIndices(leftIndex = 1, rightIndex = 1), result[1])
        assertEquals(3, result.size)
      }

      it("should NOT match x since y does not have a chance to match") {
        val left = Pattern("1", "x", "y")
        val right = Pattern("3", "4", "5", "x")
        val result = findExactMatchIndices(left, right)

        assertEquals(2, result.size)
      }

      it("should match y but not x") {
        val left = Pattern("1", "x", "y")
        val right = Pattern("3", "4", "5", "y", "x")
        val result = findExactMatchIndices(left, right)

        assertEquals(3, result.size)
        assertEquals(MatchIndices(leftIndex = 2, rightIndex = 3), result[1])
      }
    }

    describe("example") {

      val left = Pattern("a", "b", "c")
      val right = Pattern("1", "a", "3", "b", "c")

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
      val right = Pattern("a")
      val result = partitionByExactMatches(left, right)

      assertEquals(Pair(left, right), result[0])
    }

    it("should work when no partitions between matches") {
      val left = Pattern("x", "y")
      val right = Pattern("x", "y")
      val result = partitionByExactMatches(left, right)

      assertEquals(2, result.size)
      assertEquals(Pair(Pattern("x"), Pattern("x")), result[0])
      assertEquals(Pair(Pattern("y"), Pattern("y")), result[1])
    }

    describe("example") {

      val left = Pattern(     "x","y",    "z")
      val right = Pattern("1","x","2","3","z","4")

      var result = emptyList<Pair<Pattern,Pattern>>()

      beforeEach {
        result = partitionByExactMatches(left, right)
      }

      it("should find 5 partitions") {
        assertEquals(5, result.size)
      }

      it("should match 1 to an empty pattern") {
        assertEquals(Pair(Pattern(), Pattern("1")), result[0])
      }

      it("should match x") {
        assertEquals(Pair(Pattern("x"), Pattern("x")), result[1])
      }

      it("should match y to 2 and 3") {
        assertEquals(Pair(Pattern("y"), Pattern("2","3")), result[2])
      }

      it("should match z") {
        assertEquals(Pair(Pattern("z"), Pattern("z")), result[3])
      }

      it("should match 4 to an empty patten") {
        assertEquals(Pair(Pattern(), Pattern("4")), result[4])
      }
    }
  }
})
