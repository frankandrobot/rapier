package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.parseResult
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.words
import com.frankandrobot.rapier.textTokenIterator
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class ParseSpec : Spek({

  val anyItem = { PatternItem(words("one")) }
  val anyItemList = { ParsePatternItemList("one", "two") }

  val tokenIterator = { start : Int -> textTokenIterator(
    "one two three four",
    start = start
  )}


  describe("parse PatternItem") {

    val startToken = 0
    val nextToken = 1

    it("should parse a match") {
      val initialTokens = tokenIterator(startToken)
      val nextTokens = tokenIterator(nextToken)
      val result = anyItem().parse(initialTokens)

      assertEquals(parseResult(nextTokens, matchFound = true, matches = "one"), result)
    }

    it("should work when no match") {
      val noMatch = textTokenIterator("two three")
      val result = anyItem().parse(noMatch)

      assertEquals(ParseResult(noMatch, matchFound = false), result)
    }
  }


  describe("parse PatternList") {

    val startToken = 0
    val nextToken = 2

    it("should parse a match") {
      val initialTokens = tokenIterator(startToken)
      val nextTokens = tokenIterator(nextToken)
      val result = anyItemList().parse(ParseResult(initialTokens))

      assertEquals(parseResult(nextTokens, true, "one", "two"), result)
    }

    it("should work when no match") {
      val noMatch = textTokenIterator("two three")
      val result = anyItemList().parse(ParseResult(noMatch))

      assertEquals(ParseResult(noMatch, matchFound = false), result)
    }
  }
})
