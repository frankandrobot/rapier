package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.words
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class parseTest : Spek({

  val anyItem = { PatternItem(words("one")) }
  val anyItemList = { ParsePatternItemList("one", "two") }

  val tokens = { start : Int -> textToTokenIterator("one two three four", start) }

  describe("parse PatternItem") {

    val startToken = 0
    val nextToken = 1

    it("should parse a match") {

      val initialTokens = tokens(startToken)
      val nextTokens = tokens(nextToken)

      val result = anyItem().parse(initialTokens)

      assertEquals(ParseResult(nextTokens, matchFound = true, matches = "one"), result)
    }

    it("should work when no match") {

      val noMatch = textToTokenIterator("two three")
      val result = anyItem().parse(noMatch)

      assertEquals(ParseResult(noMatch, matchFound = false), result)
    }
  }

  describe("parse PatternList") {

    val startToken = 0
    val nextToken = 2

    it("should parse a match") {

      val initialTokens = tokens(startToken)
      val nextTokens = tokens(nextToken)

      val result = anyItemList().parse(ParseResult(initialTokens))

      assertEquals(ParseResult(nextTokens, true, "one", "two"), result)
    }

    it("should work when no match") {

      val noMatch = textToTokenIterator("two three")
      val result = anyItemList().parse(ParseResult(noMatch))

      assertEquals(ParseResult(noMatch, matchFound = false), result)
    }
  }
})
