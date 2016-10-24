package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternItem
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class GeneralizePatternsCasesSpec : Spek({

  describe("caseEqualSize") {

    describe("patterns of length 1") {

      it("should work like generalize pattern elements") {
        val a = PatternItem(listOf("man"), listOf("tag"))
        val b = PatternItem(listOf("woman"), listOf("tag"))
        val expected = generalize(a, b)
        val actual = caseEqualSize(Pattern(a), Pattern(b)).get().flatMap { it() }

        assertEquals(true, expected.containsAll(actual))
        assertEquals(true, actual.containsAll(expected))
      }
    }

    describe("patterns of length greater than 1") {
      val a = Pattern("ate", "the", "pasta")
      val b = Pattern("hit", "the", "ball")

      var result = emptyList<Pattern>()

      beforeEach{
        result = caseEqualSize(a, b).get()
      }

      it("should generalize patterns by pairing corresponding elements") {
        val a = Pattern("a", "x")
        val b = Pattern("b", "x")
        val result = caseEqualSize(a, b).get()

        assertEquals(2, result.size)
        assertEquals(true, result.any{ it == Pattern(PatternItem(), PatternItem("x")) })
        assertEquals(true, result.any{ it == Pattern(PatternItem("a", "b"), PatternItem("x")) })
      }

      it("should have 2 x 2 or 4 generalizations") {
        assertEquals(4, result.size)
        assertEquals(true, result.any{ it == Pattern(PatternItem("ate", "hit"), PatternItem("the"), PatternItem("pasta", "ball")) })
        assertEquals(true, result.any{ it == Pattern(PatternItem(), PatternItem("the"), PatternItem("pasta", "ball")) })
        assertEquals(true, result.any{ it == Pattern(PatternItem("ate", "hit"), PatternItem("the"), PatternItem()) })
        assertEquals(true, result.any{ it == Pattern(PatternItem(), PatternItem("the"), PatternItem()) })
      }
    }
  }
})
