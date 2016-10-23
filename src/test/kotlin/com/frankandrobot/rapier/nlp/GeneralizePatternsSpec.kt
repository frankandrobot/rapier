package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternItem
import org.jetbrains.spek.api.Spek
import java.util.*
import kotlin.test.assertEquals


class GeneralizePatternsSpec : Spek ({


  describe("findExactMatchIndices") {

    val anyShorterPattern = Pattern(listOf("a", "b", "c").map{ PatternItem(it) })
    val anyLongerPattern = Pattern(listOf("1", "a", "3", "b", "c").map{ PatternItem(it) })

    var result = ArrayList<MatchIndices>()

    beforeEach{
      result = findExactMatchIndices(anyShorterPattern, anyLongerPattern)
    }

    it("should return 3 matches plus 2 dummy fillers") {
      assertEquals(5, result.size)
    }

    it("should return dummy filler at beginning") {
      assertEquals(MatchIndices(leftIndex = -1, rightIndex = -1), result[0])
    }

    it("should return dummy filler at end") {
      assertEquals(MatchIndices(leftIndex = 3, rightIndex = 5), result[4])
    }

    it("should match a, b, c") {
      assertEquals(MatchIndices(leftIndex = 0, rightIndex = 1), result[1])
      assertEquals(MatchIndices(leftIndex = 1, rightIndex = 3), result[2])
      assertEquals(MatchIndices(leftIndex = 2, rightIndex = 4), result[3])
    }

    it("should return the first match") {
      val left = Pattern(listOf("1", "x", "2").map{ PatternItem(it) })
      val right = Pattern(listOf("3", "x", "x").map{ PatternItem(it) })
      val result = findExactMatchIndices(left, right)

      assertEquals(MatchIndices(leftIndex = 1, rightIndex = 1), result[1])
      assertEquals(3, result.size)
    }

    it("should NOT return match if elements in shorter pattern cannot match up 1") {
      val left = Pattern(listOf("1", "x", "y").map{ PatternItem(it) })
      val right = Pattern(listOf("3", "4", "5", "x").map{ PatternItem(it) })
      val result = findExactMatchIndices(left, right)

      assertEquals(2, result.size)
    }

    it("should NOT return match if elements in shorter pattern cannot match up 2") {
      val left = Pattern(listOf("1", "x", "y").map{ PatternItem(it) })
      val right = Pattern(listOf("3", "4", "5", "y", "x").map{ PatternItem(it) })
      val result = findExactMatchIndices(left, right)

      assertEquals(3, result.size)
      assertEquals(MatchIndices(leftIndex = 2, rightIndex = 3), result[1])
    }
  }

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
