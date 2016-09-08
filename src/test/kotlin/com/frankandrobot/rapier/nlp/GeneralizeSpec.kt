package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.PosTagConstraint
import com.frankandrobot.rapier.pattern.WordConstraint
import org.jetbrains.spek.api.Spek
import java.util.*
import kotlin.test.assertEquals


fun <T> _equals(a : HashSet<out T>, b : HashSet<out T>) = a.size == b.size && a.containsAll(b) && b.containsAll(a)

class GeneralizeSpec : Spek({

  describe("generalize constraints") {

    val anyConstraint = hashSetOf(WordConstraint("a"), WordConstraint("b"))
    val anyConstraint2 = hashSetOf(WordConstraint("b"), WordConstraint("a"))
    val anyOtherContraint = hashSetOf(WordConstraint("c"), WordConstraint("d"))
    val emptyContraint = hashSetOf<WordConstraint>()
    val anotherEmptyConstraint = hashSetOf<WordConstraint>()


    it("should return the same constraints if they are the same") {

      val result = generalize(anyConstraint, anyConstraint2)

      assert(_equals(result[0], anyConstraint))
    }

    it("should return an empty constraint when constraints differ") {

      val result = generalize(anyConstraint, anyOtherContraint)

      val emptyIndex = if (result[0].isEmpty()) 0 else if (result[1].isEmpty()) 1 else -1

      assert(emptyIndex >= 0)
    }

    it("should return the union of the contraints when constraints differ") {

      val result = generalize(anyConstraint, anyOtherContraint)

      val nonEmptyIndex = if (!result[0].isEmpty()) 0 else if (!result[1].isEmpty()) 1 else -1

      assert(nonEmptyIndex >= 0)
      assert(_equals(result[nonEmptyIndex], (anyConstraint + anyOtherContraint) as HashSet<out WordConstraint>))
    }

    it("should work when both constraints are empty") {

      val result = generalize(emptyContraint, anotherEmptyConstraint)

      assertEquals(1, result.size)
      assert(_equals(result[0], hashSetOf<WordConstraint>()))
    }

    it("should work when one of the constraints is empty") {

      val result = generalize(emptyContraint, anyConstraint)

      assertEquals(2, result.size)
      assert(_equals(result[0], hashSetOf<WordConstraint>()))
      assert(_equals(result[1], anyConstraint))
    }

    it("should work with duplicate constraints") {

      val duplicates = hashSetOf(anyConstraint.first(), WordConstraint("e"))
      val result = generalize(anyConstraint, duplicates)

      assertEquals(0, result[0].size)
      assertEquals(3, result[1].size)
    }
  }

  describe("generalize pattern elements") {

    describe("pattern items") {

      val anyWordConstraint1 = hashSetOf(WordConstraint("word1"), WordConstraint("word2"))
      val anyWordConstraint2 = hashSetOf(WordConstraint("word3"), WordConstraint("word4"))

      val anyTagConstraint1 = hashSetOf(PosTagConstraint("tag1"), PosTagConstraint("tag2"))
      val anyTagConstraint2 = hashSetOf(PosTagConstraint("tag3"), PosTagConstraint("tag4"))

      val anyPatternElem = PatternItem(
        anyWordConstraint1,
        anyTagConstraint1
      )
      val anyOtherPatternElem = PatternItem(
        anyWordConstraint2,
        anyTagConstraint2
      )

      it("should work with pattern items") {

        val pattern1 = PatternItem()
        val pattern2 = PatternItem(
          posTagContraints = (anyTagConstraint1 + anyTagConstraint2) as HashSet<out PosTagConstraint>
        )
        val pattern3 = PatternItem(
          (anyWordConstraint1 + anyWordConstraint2) as HashSet<out WordConstraint>
        )
        val pattern4 = PatternItem(
          (anyWordConstraint1 + anyWordConstraint2) as HashSet<out WordConstraint>,
          (anyTagConstraint1 + anyTagConstraint2) as HashSet<out PosTagConstraint>
        )

        val result = generalize(anyPatternElem, anyOtherPatternElem)

        println(result)
        assert(result.contains(pattern1))
        assert(result.contains(pattern2))
        assert(result.contains(pattern3))
        assert(result.contains(pattern4))
        assertEquals(4, result.size)
      }
    }
  }
})
