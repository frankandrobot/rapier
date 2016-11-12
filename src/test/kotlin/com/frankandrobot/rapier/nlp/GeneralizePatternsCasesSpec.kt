package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.pattern.*
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class GeneralizePatternsCasesSpec : Spek({

  val empty = Pattern()
  val one = Pattern("one")
  val two = Pattern("one", "two")
  val three = Pattern("one", "two", "three")
  val anyPattern = Pattern("one", "two", "three")

  describe("caseEqualLengthPatterns") {

    describe("patterns of length 1") {

      it("should work like generalize pattern elements") {
        val a = PatternItem(listOf("man"), listOf("tag"))
        val b = PatternItem(listOf("woman"), listOf("tag"))
        val expected = generalize(a, b)
        val actual = caseEqualLengthPatterns(Pattern(a), Pattern(b)).get().flatMap { it() }

        assertEquals(true, expected.containsAll(actual))
        assertEquals(true, actual.containsAll(expected))
      }
    }

    describe("patterns of length greater than 1") {
      val a = Pattern("ate", "the", "pasta")
      val b = Pattern("hit", "the", "ball")

      var result = emptyList<Pattern>()

      beforeEach{
        result = caseEqualLengthPatterns(a, b).get()
      }

      it("should generalize patterns by pairing corresponding elements") {
        val a = Pattern("a", "x")
        val b = Pattern("b", "x")
        val result = caseEqualLengthPatterns(a, b).get()

        assertEquals(2, result.size)
        assertEquals(true, result.any{ it ==
          Pattern(
            PatternItem(),
            PatternItem(words("x"))
          )})
        assertEquals(true, result.any{ it ==
          Pattern(
            PatternItem(words("a", "b")),
            PatternItem(words("x"))
          )})
      }

      it("should have 2 x 2 or 4 generalizations") {
        assertEquals(4, result.size)
        assertEquals(true, result.any{ it ==
          Pattern(
            PatternItem(words("ate", "hit")),
            PatternItem(words("the")),
            PatternItem(words("pasta", "ball"))
          )})
        assertEquals(true, result.any{ it ==
          Pattern(
            PatternItem(),
            PatternItem(words("the")),
            PatternItem(words("pasta", "ball"))
          )})
        assertEquals(true, result.any{ it ==
          Pattern(
            PatternItem(words("ate", "hit")),
            PatternItem(words("the")),
            PatternItem()
          )})
        assertEquals(true, result.any{ it ==
          Pattern(
            PatternItem(),
            PatternItem(words("the")),
            PatternItem()
          )})
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
      assertEquals(Pattern(PatternList(words("one"), length = 1)), result[0])
    }

    it("should generalize the pattern's elements") {
      val result = caseAnEmptyPattern(empty, three).get()
      assertEquals(2, result.size)
      assertEquals(true, result.contains(Pattern(PatternList(length = 3))))
      assertEquals(
        true,
        result.contains(Pattern(PatternList(words("one", "two", "three"), length = 3)))
      )
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
      assertEquals(true, result.contains(Pattern(PatternList(length = 2))))
      assertEquals(
        true,
        result.contains(Pattern(PatternList(words("one", "two"), length = 2)))
      )
    }

    it("should generalize the pattern's elements") {
      val result = casePatternHasSingleElement(one, three).get()
      assertEquals(2, result.size)
      assertEquals(true, result.contains(Pattern(PatternList(length = 3))))
      assertEquals(
        true,
        result.contains(Pattern(PatternList(words("one", "two", "three"), length = 3)))
      )
    }

    describe("example") {
      var a : Pattern
      var b : Pattern
      var result = emptyList<Pattern>()

      beforeEach{
        a = Pattern(
          PatternItem(words("bank"), tags("nn")),
          PatternItem(words("vault"), tags("nn"))
        )
        b = Pattern(
          PatternList(posTagConstraints = hashSetOf(PosTagConstraint("nnp")), length = 3)
        )
        result = casePatternHasSingleElement(a, b).get()
      }

      it("should have two generalizations") {
        assertEquals(2, result.size)
      }

      it("should have one pattern list with unconstrained words of length 3") {
        assertEquals(true, result.contains(
          Pattern(
            PatternList(
              posTagConstraints = hashSetOf(
                PosTagConstraint("nnp"),
                PosTagConstraint("nn")
              ),
              length = 3
            )
          )
        ))
      }

      it("should have unconstrained pattern list of length 3") {
        assertEquals(true, result.contains(Pattern(PatternList(length = 3))))
      }
    }
  }


  describe("caseVeryLongPatterns") {

    describe("max pattern length difference") {

      val length = 5
      val maxPatternLength = length + maxDifferenceInPatternLength + 1
      val a = {pattern(1..length)}
      val b = {pattern(1..maxPatternLength)}

      var result = emptyList<Pattern>()

      beforeEach {
        result = caseVeryLongPatterns(a(), b()).get()
      }

      it("should return one pattern") {
        assertEquals(1, result.size)
      }

      it("should return one pattern with a single pattern list") {
        assertEquals(1, result[0].length())
        assertEquals(true, result[0]()[0] is PatternList)
      }

      it("should return one pattern with a single unconstrained pattern list") {
        assertEquals(1, result[0]().size)
        assertEquals(PatternList(length = b().length()), result[0]()[0])
      }
    }

    describe("max unequal pattern length") {

      val maxPatternLength = maxUnequalPatternLength + 2
      val a = {pattern(1..maxUnequalPatternLength)}
      val b = {pattern(1..maxPatternLength)}

      var result = emptyList<Pattern>()

      beforeEach {
        result = caseVeryLongPatterns(a(), b()).get()
      }

      it("should return one pattern") {
        assertEquals(1, result.size)
      }

      it("should return one pattern with a single pattern list") {
        assertEquals(1, result[0].length())
        assertEquals(true, result[0]()[0] is PatternList)
      }

      it("should return one pattern with a single unconstrained pattern list") {
        assertEquals(1, result[0]().size)
        assertEquals(PatternList(length = b().length()), result[0]()[0])
      }
    }

    describe("longest pattern") {

      val maxPatternLength = maxPatternLength + 1

      it("should return a single unconstrained pattern list if a pattern is too long") {

        val a = pattern(1..2)
        val b = pattern(1..maxPatternLength)
        val result = caseVeryLongPatterns(a, b).get()

        assertEquals(1, result.size)
        assertEquals(true, result[0]()[0] is PatternList)
        assertEquals(b.length(), result[0]()[0].length)
      }
    }
  }

  describe("case handling") {

    describe("areEqualLengths") {

      it("should not handle the case when a pattern is empty") {
        assertEquals(false, areEqualLengths(empty, two))
        assertEquals(false, areEqualLengths(empty, empty))
      }

      it("should return true only when both patterns are equal and non-empty") {
        assertEquals(true, areEqualLengths(one, one))
        assertEquals(true, areEqualLengths(two, two))
        assertEquals(false, areEqualLengths(empty, empty))
        assertEquals(false, areEqualLengths(one, two))
        assertEquals(false, areEqualLengths(two, three))
      }
    }

    describe("exactlyOneIsEmpty") {

      it("should return true only when exactly one pattern is empty") {
        assertEquals(true, exactlyOneIsEmpty(empty, one))
        assertEquals(true, exactlyOneIsEmpty(two, empty))
        assertEquals(false, exactlyOneIsEmpty(one, two))
        assertEquals(false, exactlyOneIsEmpty(one, one))
        assertEquals(false, exactlyOneIsEmpty(empty, empty))
      }
    }

    describe("exactlyOneHasOneElement") {

      it("should return true only when exactly one pattern has length 1") {
        assertEquals(true, exactlyOneHasOneElement(one, two))
        assertEquals(true, exactlyOneHasOneElement(two, one))
        assertEquals(false, exactlyOneHasOneElement(one, one))
        assertEquals(false, exactlyOneHasOneElement(two, three))
        assertEquals(false, exactlyOneHasOneElement(empty, empty))
      }

      it("should not handle the case when one pattern is empty") {
        assertEquals(false, exactlyOneHasOneElement(empty, one))
      }
    }

    describe("areVeryLong") {
      //(longer.length() >= 3 && diff > maxDifferenceInPatternLength) ||
      //(longer.length() > maxUnequalPatternLength && diff >= 2) ||
      //longer.length() > maxPatternLength
      //(length<=2 || diff<maxDiff) && (length <= maxUn || diff <= 1) && length <= maxPattern)
      it("should not handle the case when the pattern lengths are less than 3") {
        assertEquals(false, areVeryLong(empty, empty))
        assertEquals(false, areVeryLong(empty, one))
        assertEquals(false, areVeryLong(one, one))
        assertEquals(false, areVeryLong(two, one))
        assertEquals(false, areVeryLong(two, two))
      }

      it("should return false then the diff is <= maxDifferenceInPatternLength and the" +
        " pattern lengths are <= maxUnequalPatternLegnth ") {
        val a = pattern(1)
        val b = pattern(maxDifferenceInPatternLength)
        assertEquals(false, areVeryLong(a, b))
      }

      it("should return false then the diff is <= 1 and pattern lengths are <= " +
        "maxPatternLength") {
        val a = pattern(maxPatternLength)
        val b = pattern(maxPatternLength - 1)
        assertEquals(false, areVeryLong(a, b))
      }
    }
  }

  describe("extend") {
    var a : Pattern
    var result : List<Pattern> = emptyList()

    beforeEach {
      a = pattern(3)
      result = extend(a().listIterator(), a.length(), 5)
    }

    it("should return 6 patterns") {
      assertEquals(6, result.size)
    }

    it("should extend pattern to length 5") {
      result.forEach { assertEquals(5, it.length()) }
    }

    it("should satisfy constraints") {
      assertEquals(true, result.contains(Pattern(1,1,1,2,3)))
      assertEquals(true, result.contains(Pattern(1,1,2,2,3)))
      assertEquals(true, result.contains(Pattern(1,2,2,2,3)))
      assertEquals(true, result.contains(Pattern(1,2,2,3,3)))
      assertEquals(true, result.contains(Pattern(1,2,3,3,3)))
      assertEquals(true, result.contains(Pattern(1,1,2,3,3)))
    }
  }
})

private fun pattern(r : IntRange) =
  Pattern(r.map { it.toString() }.map{ PatternItem(words(it)) })
private fun pattern(x : Int) =
  Pattern((1..x).map { it.toString() }.map{ PatternItem(words(it)) })
