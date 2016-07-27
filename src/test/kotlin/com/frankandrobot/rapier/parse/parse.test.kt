package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.pattern.PatternItem
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class parseTest : Spek({

  val anyItem = { PatternItem("one") }

  describe("parse PatternItem") {

    val startToken = 0
    val nextToken = 1

    val initialTokens = textToTokenIterator("one two", startToken)
    val nextTokens = textToTokenIterator("one two", nextToken)

    it("should parse a match") {

      val result = anyItem().parse(Glob(initialTokens))

      assertEquals(Glob(nextTokens, matchFound = true, matches = "one"), result)
    }

    it("should work when no match") {

      val noMatch = textToTokenIterator("two three")
      val result = anyItem().parse(Glob(noMatch))

      assertEquals(Glob(noMatch, matchFound = false), result)
    }
  }

  describe("parse PatternList") {}
})
