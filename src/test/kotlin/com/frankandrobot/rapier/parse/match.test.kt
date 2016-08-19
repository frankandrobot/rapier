package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.PatternList
import com.frankandrobot.rapier.pattern.Rule
import com.frankandrobot.rapier.template.Slot
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class MatchTest : Spek({

  val anyText = {textToTokenIterator("start prefiller filler postfiller end")}
  val anySlot = { Slot("any slot") }

  describe("match") {

    describe("pattern items") {

      val anyPreFiller = {PatternItem("prefiller")}
      val anyFiller = {PatternItem("filler")}
      val anyPostFiller = {PatternItem("postfiller")}

      val patternItemRule = { Rule(
        preFiller = Pattern(anyPreFiller()),
        filler = Pattern(anyFiller()),
        postFiller = Pattern(anyPostFiller()),
        slot = anySlot()
      )}

      it("should match a simple rule") {

        val result = patternItemRule()._match(anyText())

        assertEquals(textToTokenList("filler"), result)
      }

      it("should repeatedly match a simple rule") {

        val text = textToTokenIterator(
          "start prefiller filler postfiller and prefiller filler postfiller"
        )

        val result = patternItemRule()._match(text)

        assertEquals(textToTokenList("filler", "filler"), result)
      }

      it("should NOT find matches when prefiller doesn't match") {

        val text = textToTokenIterator("nope filler postfiller")

        val result = patternItemRule()._match(text)

        assertEquals(emptyList<Token>(), result)
      }

      it("should NOT find matches when filler doesn't match") {

        val text = textToTokenIterator("prefiller nope postfiller")

        val result = patternItemRule()._match(text)

        assertEquals(emptyList<Token>(), result)
      }

      it("should NOT find matches when postfiller doesn't match") {

        val text = textToTokenIterator("prefiller filler nope")

        val result = patternItemRule()._match(text)

        assertEquals(emptyList<Token>(), result)
      }
    }

    describe("pattern list") {

      /**
       * Expanded patterns:
       *
       * - prefiller filler postfiller
       * - prefiller filler
       * - filler postfiller
       * - filler
       */
      val patternListRule = { Rule(
        preFiller = Pattern(PatternList("prefiller")),
        filler = Pattern(PatternItem("filler")),
        postFiller = Pattern(PatternList("postfiller")),
        slot = anySlot()
      )}


      it("should match full expansion") {

        val text = textToTokenIterator("prefiller filler postfiller")
        val result = patternListRule()._match(text)

        assertEquals(textToTokenList("filler"), result.distinct())
      }

      it("should generate all patterns") {

        val text = textToTokenIterator("prefiller filler postfiller")
        val result = patternListRule()._match(text)

        assertEquals(4, result.size)
      }

      it("should match variation 1: no prefiller") {

        val text = textToTokenIterator("start filler postfiller end")
        val result = patternListRule()._match(text)

        assertEquals(textToTokenList("filler"), result.distinct())
      }

      it("should match variation 2: no postfiller") {

        val text = textToTokenIterator("start prefiller filler end")

        val result = patternListRule()._match(text)

        assertEquals(textToTokenList("filler"), result.distinct())
      }

      it("should match variation 3: no prefiller/postfiller") {

        val text = textToTokenIterator("start filler end")

        val result = patternListRule()._match(text)

        assertEquals(textToTokenList("filler"), result.distinct())
      }

      it("should match variation 4: empty") {

        val text = textToTokenIterator("start end")

        val result = patternListRule()._match(text)

        assertEquals(emptyList(), result.distinct())
      }
    }
  }
})

