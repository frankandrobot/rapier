package com.frankandrobot.rapier.pattern

import com.frankandrobot.rapier.nlp.Token
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class PatternElementSpec : Spek({

  describe("PatternElement") {

    val anyToken = Token("any word", "any tag")

    describe("PatternItem") {

      it("should work on unconstrained patterns") {
        val elem = PatternItem()
        assertEquals(true, elem.test(anyToken))
      }
    }

    describe("PatternList") {

    }

    describe("PatternItem equality") {
      it("should work when equal constrains are out of order") {
        val a = PatternItem(
          listOf("a", "b").map{WordConstraint(it)}.toHashSet(),
          listOf("tag1", "tag2").map{PosTagConstraint(it)}.toHashSet()
        )
        val b = PatternItem(
          listOf("b", "a").map{WordConstraint(it)}.toHashSet(),
          listOf("tag2", "tag1").map{PosTagConstraint(it)}.toHashSet()
        )
        assertEquals(true, a == b)
      }
    }

    describe("PatternList equality") {
      it("should work when equal constrains are out of order") {
        val a = PatternList(
          listOf("a", "b").map{WordConstraint(it)}.toHashSet(),
          listOf("tag1", "tag2").map{PosTagConstraint(it)}.toHashSet(),
          length = 1
        )
        val b = PatternList(
          listOf("b", "a").map{WordConstraint(it)}.toHashSet(),
          listOf("tag2", "tag1").map{PosTagConstraint(it)}.toHashSet(),
          length = 1
        )
        assertEquals(true, a == b)
      }
    }
  }
})
