package com.frankandrobot.rapier.pattern

import com.frankandrobot.rapier.nlp.Token
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class PatternElementSpec : Spek({

  describe("PatternElement") {

    val anyToken = Token("any word", "any tag")

    describe("PatternItem") {

      it("should work on empty constraints") {

        val elem = PatternItem()

        assertEquals(true, elem.test(anyToken))
      }
    }

    describe("PatternList") {

    }
  }
})
