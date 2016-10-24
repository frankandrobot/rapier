package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternItem
import org.jetbrains.spek.api.Spek
import java.util.*
import kotlin.test.assertEquals


class GeneralizePatternsSpec : Spek ({

  describe("findExactMatchIndices") {

    val anyShorterPattern = Pattern("a", "b", "c")
    val anyLongerPattern = Pattern("1", "a", "3", "b", "c")

    var result = ArrayList<MatchIndices>()

    beforeEach{
      result = findExactMatchIndices(anyShorterPattern, anyLongerPattern)
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

    it("should return the first match") {
      val left = Pattern("1", "x", "2")
      val right = Pattern("3", "x", "x")
      val result = findExactMatchIndices(left, right)

      assertEquals(MatchIndices(leftIndex = 1, rightIndex = 1), result[1])
      assertEquals(3, result.size)
    }

    it("should NOT return match for x if y does not have a chance to match") {
      val left = Pattern("1", "x", "y")
      val right = Pattern("3", "4", "5", "x")
      val result = findExactMatchIndices(left, right)

      assertEquals(2, result.size)
    }

    it("should NOT return matches out of order i.e., should match only y") {
      val left = Pattern("1", "x", "y")
      val right = Pattern("3", "4", "5", "y", "x")
      val result = findExactMatchIndices(left, right)

      assertEquals(3, result.size)
      assertEquals(MatchIndices(leftIndex = 2, rightIndex = 3), result[1])
    }

    it("should work in degenerate case") {
      val emptyPat = Pattern()
      val result = findExactMatchIndices(emptyPat, anyShorterPattern)

      assertEquals(2, result.size)
    }

    it("should find only in exact matches") {
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

  }

  describe("partitionByExactMatches") {

    val left = Pattern("x","y","z")
    val right = Pattern("1","x","2","3","z","4")

    var result = emptyList<Pair<Pattern,Pattern>>()

    beforeEach {
      result = partitionByExactMatches(left, right)
    }

    it("should find 5 partitions") {
      assertEquals(5, result.size)
    }

    it("should return a partition with an empty pattern before x") {
      assertEquals(Pair(Pattern(), Pattern("1")), result[0])
    }

    it("should return a partition with an empty pattern after z") {
      assertEquals(Pair(Pattern(), Pattern("4")), result[4])
    }

    it("should return x and z matches") {
      assertEquals(Pair(Pattern("x"), Pattern("x")), result[1])
      assertEquals(Pair(Pattern("z"), Pattern("z")), result[3])
    }

    it("should return partitions with non-empty patterns between x and z") {
      assertEquals(Pair(Pattern("y"), Pattern("2","3")), result[2])
    }

    it("should work in degenerate case") {
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
  }

  //internal fun Pr(vararg patternItems : String) = Pattern(patternItems.map{PatternItem(it)})

//  describe("caseEqualSize") {
//
//    it ("should work") {
//
//      val anotherSameSizePattern = Pattern(listOf("1", "2", "3").map{ PatternItem(it)})
//
//      val result = caseEqualSize(anyPattern, anotherSameSizePattern)
//
//      // should contain all combinations of:
//      // [], [a,1]
//      // [], [b,2]
//      // [], [c,3]
//      // so a total of 8 patterns
//      assertEquals(8, result.size)
//      //empty
//      assert(result.contains(Pattern(PatternItem(), PatternItem(), PatternItem())))
//      assert(result.contains(Pattern(PatternItem(), PatternItem(), PatternItem("c", "3"))))
//      assert(result.contains(Pattern(PatternItem(), PatternItem("b", "2"), PatternItem())))
//      assert(result.contains(Pattern(PatternItem(), PatternItem("b", "2"), PatternItem("c", "3"))))
//      assert(result.contains(Pattern(PatternItem("a", "1"), PatternItem(), PatternItem())))
//      assert(result.contains(Pattern(PatternItem("a", "1"), PatternItem(), PatternItem("c", "3"))))
//      assert(result.contains(Pattern(PatternItem("a", "1"), PatternItem("b", "2"), PatternItem())))
//      assert(result.contains(Pattern(PatternItem("a", "1"), PatternItem("b", "2"), PatternItem("c", "3"))))
//    }
//  }
})
