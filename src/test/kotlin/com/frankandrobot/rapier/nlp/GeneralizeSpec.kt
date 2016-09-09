package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.pattern.Constraint
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.PosTagConstraint
import com.frankandrobot.rapier.pattern.WordConstraint
import org.jetbrains.spek.api.Spek
import java.util.*
import kotlin.test.assertEquals


private fun <T : Constraint> _equals(a : HashSet<out T>, b : HashSet<out T>) = a.size == b.size && a.containsAll(b) && b.containsAll(a)
private fun <T : Constraint> _equals(a : HashSet<out T>, b : HashSetContraints<out T>) = _equals(a, b())

private data class HashSetContraints<T : Constraint>(val hashSet : HashSet<out T>)
{
  constructor(vararg constraint : T) : this(constraint.toHashSet())

  operator fun invoke() = hashSet
  operator fun plus(other : T) = (hashSet + other) as HashSet<out T>
  operator fun plus(other : HashSetContraints<T>) = (hashSet + other()) as HashSet<out T>
}


class GeneralizeSpec : Spek({

  val anyWordConstraint = HashSetContraints(WordConstraint("a"), WordConstraint("b"))
  val anyWordConstraintInAnotherOrder = HashSetContraints(WordConstraint("b"), WordConstraint("a"))
  val anyOtherWordConstraint = HashSetContraints(WordConstraint("c"), WordConstraint("d"))

  val emptyWordConstraint = HashSetContraints<WordConstraint>()
  val anotherEmptyWordConstraint = HashSetContraints<WordConstraint>()

  describe("generalize constraints") {

    it("should return the same constraints if they are the same") {

      val result = generalize(anyWordConstraint(), anyWordConstraintInAnotherOrder())

      assertEquals(1, result.size)
      assertEquals(anyWordConstraint(), result[0])
    }

    it("should return an empty constraint when constraints differ") {

      val result = generalize(anyWordConstraint(), anyOtherWordConstraint())

      assertEquals(2, result.size)
      assert(result[0].isEmpty() || result[1].isEmpty())
    }

    it("should return the union of the constraints when constraints differ") {

      val result = generalize(anyWordConstraint(), anyOtherWordConstraint())
      val union = anyWordConstraint + anyOtherWordConstraint

      assertEquals(2, result.size)
      assert(union == result[0] || union == result[1])
    }

    it("should work when both constraints are empty") {

      val result = generalize(emptyWordConstraint(), anotherEmptyWordConstraint())

      assertEquals(1, result.size)
      assertEquals(hashSetOf<WordConstraint>(), result[0])
    }

    it("should work when one of the constraints is empty") {

      val result = generalize(emptyWordConstraint(), anyWordConstraint())

      assertEquals(2, result.size)
      assertEquals(emptyWordConstraint(), result[0])
      assertEquals(anyWordConstraint(), result[1])
    }

    it("should work with duplicate constraints") {

      val duplicates = HashSetContraints(anyWordConstraint().first(), WordConstraint("e"))
      val result = generalize(anyWordConstraint(), duplicates())

      assertEquals(0, result[0].size)
      assertEquals(3, result[1].size)
      assertEquals(duplicates + anyWordConstraint, result[1])
    }
  }

  describe("generalize pattern elements") {

    val anyTagConstraint = HashSetContraints(PosTagConstraint("tag1"), PosTagConstraint("tag2"))
    val anyOtherTagConstraint = HashSetContraints(PosTagConstraint("tag3"), PosTagConstraint("tag4"))

    describe("pattern items") {

      val anyPatternElem = PatternItem(
        anyWordConstraint(),
        anyTagConstraint()
      )
      val anyOtherPatternElem = PatternItem(
        anyOtherWordConstraint(),
        anyOtherTagConstraint()
      )

      it("should work with pattern items") {

        val result = generalize(anyPatternElem, anyOtherPatternElem)

        val pattern1 = PatternItem()
        val pattern2 = PatternItem(posTagContraints = anyTagConstraint + anyOtherTagConstraint)
        val pattern3 = PatternItem(anyWordConstraint + anyOtherWordConstraint)
        val pattern4 = PatternItem(anyWordConstraint + anyOtherWordConstraint, anyTagConstraint + anyOtherTagConstraint)

        assert(result.contains(pattern1))
        assert(result.contains(pattern2))
        assert(result.contains(pattern3))
        assert(result.contains(pattern4))
        assertEquals(4, result.size)
      }

      it("should work when one of the pattern items has no word constraints") {

        val noWordContraints = PatternItem(posTagContraints = anyOtherTagConstraint())

        val result = generalize(noWordContraints, anyPatternElem)

        val pattern1 = PatternItem()
        val pattern2 = PatternItem(posTagContraints = anyTagConstraint + anyOtherTagConstraint)
        val pattern3 = PatternItem(anyWordConstraint())
        val pattern4 = PatternItem(anyWordConstraint(), anyTagConstraint + anyOtherTagConstraint)

        assert(result.contains(pattern1))
        assert(result.contains(pattern2))
        assert(result.contains(pattern3))
        assert(result.contains(pattern4))
        assertEquals(4, result.size)
      }

      it("should work when one of the pattern items has no constraints at all") {

        val noConstraints = PatternItem()

        val result = generalize(noConstraints, anyPatternElem)

        val pattern1 = PatternItem()
        val pattern2 = PatternItem(posTagContraints = anyPatternElem.posTagContraints)
        val pattern3 = PatternItem(anyPatternElem.wordConstraints)
        val pattern4 = anyPatternElem

        assert(result.contains(pattern1))
        assert(result.contains(pattern2))
        assert(result.contains(pattern3))
        assert(result.contains(pattern4))
        assertEquals(4, result.size)
      }
    }
  }
})
