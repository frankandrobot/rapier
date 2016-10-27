package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.PatternList
import org.funktionale.option.Option
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class GeneralizePatternsCasesSpec : Spek({

  val empty = Pattern()
  val one = Pattern("one")
  val two = Pattern("one", "two")
  val three = Pattern("one", "two", "three")
  val anyPattern = Pattern("one", "two", "three")

  describe("casePatternsEqualSize") {

    describe("patterns of length 1") {

      it("should work like generalize pattern elements") {
        val a = PatternItem(listOf("man"), listOf("tag"))
        val b = PatternItem(listOf("woman"), listOf("tag"))
        val expected = generalize(a, b)
        val actual = casePatternsEqualSize(Pattern(a), Pattern(b)).get().flatMap { it() }

        assertEquals(true, expected.containsAll(actual))
        assertEquals(true, actual.containsAll(expected))
      }
    }

    describe("patterns of length greater than 1") {
      val a = Pattern("ate", "the", "pasta")
      val b = Pattern("hit", "the", "ball")

      var result = emptyList<Pattern>()

      beforeEach{
        result = casePatternsEqualSize(a, b).get()
      }

      it("should generalize patterns by pairing corresponding elements") {
        val a = Pattern("a", "x")
        val b = Pattern("b", "x")
        val result = casePatternsEqualSize(a, b).get()

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

  describe("caseAnEmptyPattern") {

    it("should return nothing but pattern lists") {
      val result = caseAnEmptyPattern(empty, anyPattern).get()
      assertEquals(true, result.all{ it().all{ it is PatternList }})
    }

    it("should return the same pattern element transformed to a list for patterns of length 1") {
      val result = caseAnEmptyPattern(empty, Pattern("one")).get()
      assertEquals(1, result.size)
      assertEquals(Pattern(PatternList("one", length = 1)), result[0])
    }

    it("should generalize the pattern's elements") {
      val result = caseAnEmptyPattern(empty, three).get()
      assertEquals(2, result.size)
      assertEquals(true, result.contains(Pattern(PatternList(length = 3))))
      assertEquals(true, result.contains(Pattern(PatternList("one", "two", "three", length = 3))))
    }
  }

  describe("casePatternHasSingleElement") {

    it("should return nothing but pattern lists") {
      val result = casePatternHasSingleElement(one, anyPattern).get()
      assertEquals(true, result.all{ it().all{ it is PatternList }})
    }

    it("should return the generalization of both patterns for patterns of length 1") {
      val result = casePatternHasSingleElement(one, two).get()
      assertEquals(2, result.size)
      assertEquals(true, result.contains(Pattern(PatternList(length = 3))))
      assertEquals(true, result.contains(Pattern(PatternList("one", "two", length = 3))))
    }

    it("should generalize the pattern's elements") {
      val result = casePatternHasSingleElement(one, three).get()
      assertEquals(2, result.size)
      assertEquals(true, result.contains(Pattern(PatternList(length = 4))))
      assertEquals(true, result.contains(Pattern(PatternList("one", "two", "three", length = 4))))
    }
  }

  describe("case handling") {

    describe("casePatternsEqualSize") {

      it("should return None when one of the patterns is empty") {
        assertEquals(Option.None, casePatternsEqualSize(empty, two))
      }

      it("should return None when both patterns are empty") {
        assertEquals(Option.None, casePatternsEqualSize(empty, empty))
      }

      it("should return None when patterns have different lengths") {
        assertEquals(Option.None, casePatternsEqualSize(one, two))
        assertEquals(Option.None, casePatternsEqualSize(two, three))
      }

      it("should handle case when both patterns have length 1") {
        assertEquals(false, casePatternsEqualSize(one, one) is Option.None)
      }
    }

    describe("caseAnEmptyPattern") {

      it("should return None when both patterns are empty") {
        assertEquals(Option.None, caseAnEmptyPattern(empty, empty))
      }

      it("should return None when both patterns are non-empty") {
        assertEquals(Option.None, caseAnEmptyPattern(one, two))
      }

      it("should return None when both patterns are non-empty and equal length") {
        assertEquals(Option.None, caseAnEmptyPattern(one, one))
      }
    }

    describe("casePatternHasSingleElement") {

      it("should return None when the other pattern is empty") {
        assertEquals(Option.None, casePatternHasSingleElement(empty, one))
      }

      it("should return None when both patterns are empty") {
        assertEquals(Option.None, casePatternHasSingleElement(empty, empty))
      }

      it("should return None when both patterns are length 1") {
        assertEquals(Option.None, casePatternHasSingleElement(one, one))
      }

      it("should return None when both patterns have length > 1") {
        assertEquals(Option.None, casePatternHasSingleElement(two, two))
        assertEquals(Option.None, casePatternHasSingleElement(two, three))
      }
    }
  }
})
