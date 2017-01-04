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

package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.parseResult
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.words
import com.frankandrobot.rapier.textTokenIterator
import org.amshove.kluent.shouldEqual
import org.funktionale.option.Option.None
import org.funktionale.option.Option.Some
import org.jetbrains.spek.api.Spek


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

      result shouldEqual parseResult(
        tokens = nextTokens,
        index = Some(startToken),
        matches = "one"
      )
    }

    it("should work when no match") {
      val noMatch = textTokenIterator("two three")
      val result = anyItem().parse(noMatch)

      result shouldEqual ParseResult(tokens = noMatch, index = None)
    }
  }


  describe("parse PatternList") {

    val startToken = 0
    val nextToken = 2

    it("should parse a match") {
      val initialTokens = tokenIterator(startToken)
      val nextTokens = tokenIterator(nextToken)
      val result = anyItemList().parse(ParseResult(initialTokens))

      result shouldEqual parseResult(
        nextTokens,
        Some(startToken),
        "one", "two"
      )
    }

    it("should work when no match") {
      val noMatch = textTokenIterator("two three")
      val result = anyItemList().parse(ParseResult(noMatch))

      result shouldEqual parseResult(tokens = noMatch)
    }
  }
})
