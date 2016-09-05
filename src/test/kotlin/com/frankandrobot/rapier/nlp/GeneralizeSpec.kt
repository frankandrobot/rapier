package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.pattern.WordConstraint
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


fun <T> equals(a : List<T>, b : List<T>) = a.size == b.size && a.containsAll(b) && b.containsAll(a)

class GeneralizeSpec : Spek({

  describe("generalize") {

    val anyConstraint = listOf(WordConstraint("a"), WordConstraint("b"))
    val anyConstraint2 = listOf(WordConstraint("b"), WordConstraint("a"))
    val anyOtherContraint = listOf(WordConstraint("c"), WordConstraint("d"))

    it("should return the same constraints if they are the same") {

      val result = generalize(anyConstraint, anyConstraint2)

      assert(equals(result[0], anyConstraint))
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
      assert(equals(result[nonEmptyIndex], anyConstraint + anyOtherContraint))
    }
  }
})
