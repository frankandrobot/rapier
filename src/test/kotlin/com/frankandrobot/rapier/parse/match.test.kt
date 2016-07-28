package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.Rule
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class MatchTest : Spek({

  val anyPreFiller = {PatternItem("prefiller")}
  val anyFiller = {PatternItem("filler")}
  val anyPostFiller = {PatternItem("postfiller")}

  val simpleRule = { Rule(
    preFiller = Pattern(anyPreFiller()),
    filler = Pattern(anyFiller()),
    postFiller = Pattern(anyPostFiller())
  )}

  describe("match") {

    it("should match a simple rule") {

      val text = textToTokenIterator("start prefiller filler postfiller end")

      val result = simpleRule()._match(text)

      assertEquals(textToTokenList("filler"), result)
    }

    it("should repeatedly match a simple rule") {

      val text = textToTokenIterator(
        "start prefiller filler postfiller and prefiller filler postfiller"
      )

      val result = simpleRule()._match(text)

      assertEquals(textToTokenList("filler", "filler"), result)
    }

    it("should NOT find matches when prefiller doesn't match") {

      val text = textToTokenIterator("nope filler postfiller")

      val result = simpleRule()._match(text)

      assertEquals(emptyList<Token>(), result)
    }

    it("should NOT find matches when filler doesn't match") {

      val text = textToTokenIterator("prefiller nope postfiller")

      val result = simpleRule()._match(text)

      assertEquals(emptyList<Token>(), result)
    }

    it("should NOT find matches when postfiller doesn't match") {

      val text = textToTokenIterator("prefiller filler nope")

      val result = simpleRule()._match(text)

      assertEquals(emptyList<Token>(), result)
    }
  }
})

