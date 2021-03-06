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

import com.frankandrobot.rapier.pattern.*
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

  val noWordConstraints = HashSetContraints<WordConstraint>()
  val anotherNoWordConstraints = HashSetContraints<WordConstraint>()

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

    it("should return empty when both constraints are empty") {

      val result = generalize(noWordConstraints(), anotherNoWordConstraints())

      assertEquals(1, result.size)
      assertEquals(hashSetOf<WordConstraint>(), result[0])
    }

    it("should return empty when one constraint is empty") {

      val result = generalize(noWordConstraints(), anyWordConstraint())

      assertEquals(1, result.size)
      assertEquals(noWordConstraints(), result[0])
    }

    it("should return the superset when one is a superset") {

      val superset = HashSetContraints((anyWordConstraint() + WordConstraint("e")) as HashSet<WordConstraint>)
      val result = generalize(anyWordConstraint(), superset())

      assertEquals(superset(), result[0])
      assertEquals(1, result.size)
    }
  }

  describe("generalize pattern elements") {

    val anyTagConstraint = HashSetContraints(PosTagConstraint("tag1"), PosTagConstraint("tag2"))
    val anyOtherTagConstraint = HashSetContraints(PosTagConstraint("tag3"), PosTagConstraint("tag4"))
    val anyPatternItem = PatternItem(anyWordConstraint(), anyTagConstraint())
    val anyPatternList = PatternList(anyWordConstraint(), anyTagConstraint(), length = 2)
    val anyBiggerPatternList = PatternList(anyOtherWordConstraint(), anyOtherTagConstraint(), length = 3)

    describe("pattern items") {

      val anyOtherPatternitem = PatternItem(
        anyOtherWordConstraint(),
        anyOtherTagConstraint()
      )

      it("should work with pattern items") {

        val result = generalize(anyPatternItem, anyOtherPatternitem)

        val pattern1 = PatternItem()
        val pattern2 = PatternItem(posTagConstraints = anyTagConstraint + anyOtherTagConstraint)
        val pattern3 = PatternItem(anyWordConstraint + anyOtherWordConstraint)
        val pattern4 = PatternItem(anyWordConstraint + anyOtherWordConstraint, anyTagConstraint + anyOtherTagConstraint)

        assert(result.contains(pattern1))
        assert(result.contains(pattern2))
        assert(result.contains(pattern3))
        assert(result.contains(pattern4))
        assertEquals(4, result.size)
      }

      it("should work when one of the pattern items has no word constraints") {

        val noWordContraints = PatternItem(posTagConstraints = anyOtherTagConstraint())

        val result = generalize(noWordContraints, anyPatternItem)

        val pattern1 = PatternItem()
        val pattern2 = PatternItem(posTagConstraints = anyTagConstraint + anyOtherTagConstraint)

        assertEquals(2, result.size)
        assert(result.contains(pattern1))
        assert(result.contains(pattern2))
      }

      it("should work when one of the pattern items has no constraints at all") {

        val noConstraints = PatternItem()

        val result = generalize(noConstraints, anyPatternItem)

        val pattern1 = PatternItem()

        assertEquals(1, result.size)
        assert(result.contains(pattern1))
      }
    }

    describe("two pattern lists") {

      var result = emptyList<PatternElement>()

      beforeEach {
        result = generalize(anyPatternList, anyBiggerPatternList)
      }

      it("should produce four patterns") {
        assertEquals(4, result.size)
      }

      it("should produce pattern lists") {
        result.forEach { assertEquals(true, it is PatternList) }
      }

      it("should produce pattern lists with same length as longer list") {
        result.forEach { assertEquals(anyBiggerPatternList.length, it.length)}
      }

      it("should produce an unconstrained pattern list") {
        assertEquals(true, result.any { it == PatternList(length = anyBiggerPatternList.length) })
      }

      it("should produce pattern lists that are combinations of the union of constraints") {
        assertEquals(true, result.any { it == PatternList(
          posTagConstraints = anyTagConstraint + anyOtherTagConstraint,
          length = anyBiggerPatternList.length
        )})
        assertEquals(true, result.any { it == PatternList(
          anyWordConstraint + anyOtherWordConstraint,
          length = anyBiggerPatternList.length
        )})
        assertEquals(true, result.any { it == PatternList(
          anyWordConstraint + anyOtherWordConstraint,
          anyTagConstraint + anyOtherTagConstraint,
          length = anyBiggerPatternList.length
        )})
      }
    }

    describe("a pattern list and a pattern item") {

      it("should work just like two pattern lists") {

        val result = generalize(anyPatternItem, anyBiggerPatternList)

        val pattern1 = PatternList(length = anyBiggerPatternList.length)
        val pattern2 = PatternList(posTagConstraints = anyTagConstraint + anyOtherTagConstraint, length = anyBiggerPatternList.length)
        val pattern3 = PatternList(anyWordConstraint + anyOtherWordConstraint, length = anyBiggerPatternList.length)
        val pattern4 = PatternList(anyWordConstraint + anyOtherWordConstraint, anyTagConstraint + anyOtherTagConstraint, length = anyBiggerPatternList.length)

        assert(result.contains(pattern1))
        assert(result.contains(pattern2))
        assert(result.contains(pattern3))
        assert(result.contains(pattern4))
        assertEquals(4, result.size)

        result.forEach { assertEquals(anyBiggerPatternList.length, (it as PatternList).length) }
      }
    }
  }
})
