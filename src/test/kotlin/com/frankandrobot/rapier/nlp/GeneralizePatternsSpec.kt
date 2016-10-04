package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternItem
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class GeneralizePatternsSpec : Spek ({

  val anyPattern = Pattern(listOf("a", "b", "c").map{ PatternItem(it)})

  describe("findMatches") {

    val anyLongerPattern = Pattern(listOf("1", "a", "3", "b", "c").map{ PatternItem(it)})

    it ("should work") {

      val result = findMatches(anyPattern, anyLongerPattern)

      assertEquals(3, result.size)
      assertEquals(Match(matchIndexSmall = 0, matchIndexLarge = 1), result[0])
      assertEquals(Match(matchIndexSmall = 1, matchIndexLarge = 3), result[1])
      assertEquals(Match(matchIndexSmall = 2, matchIndexLarge = 4), result[2])
    }
  }

  describe("caseEqualSize") {

    it ("should work") {

      val anotherSameSizePattern = Pattern(listOf("1", "2", "3").map{ PatternItem(it)})

      val result = caseEqualSize(anyPattern, anotherSameSizePattern)

      // should contain all combinations of:
      // [], [a,1]
      // [], [b,2]
      // [], [c,3]
      // so a total of 8 patterns
      assertEquals(8, result.size)
      //empty
      assert(result.contains(Pattern(PatternItem(), PatternItem(), PatternItem())))
      assert(result.contains(Pattern(PatternItem(), PatternItem(), PatternItem("c", "3"))))
      assert(result.contains(Pattern(PatternItem(), PatternItem("b", "2"), PatternItem())))
      assert(result.contains(Pattern(PatternItem(), PatternItem("b", "2"), PatternItem("c", "3"))))
      assert(result.contains(Pattern(PatternItem("a", "1"), PatternItem(), PatternItem())))
      assert(result.contains(Pattern(PatternItem("a", "1"), PatternItem(), PatternItem("c", "3"))))
      assert(result.contains(Pattern(PatternItem("a", "1"), PatternItem("b", "2"), PatternItem())))
      assert(result.contains(Pattern(PatternItem("a", "1"), PatternItem("b", "2"), PatternItem("c", "3"))))
    }
  }
})
