package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.meta.Slot
import com.frankandrobot.rapier.meta.SlotFiller
import com.frankandrobot.rapier.meta.SlotName
import com.frankandrobot.rapier.nlp.wordTokens
import com.frankandrobot.rapier.pattern.BaseRule
import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.patternItemOfWords
import com.frankandrobot.rapier.patternOfItemWords
import com.frankandrobot.rapier.textToTokenIterator
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import java.util.*


class MatchSpec : Spek({

  val anyText = { textToTokenIterator("A a b C D e f Z") }
  val anySlot = { Slot(SlotName("any slot"), slotFillers = HashSet<SlotFiller>()) }

  describe("match") {

    describe("pattern items") {

      val patternItemRule = { BaseRule(
        preFiller = patternOfItemWords("a", "b"),
        filler = patternOfItemWords("C", "D"),
        postFiller = patternOfItemWords("e", "f"),
        slot = anySlot()
      )}


      it("should match a simple rule") {
        val result = patternItemRule()._exactMatch(anyText())
        result.size shouldEqual 1
        result.first() shouldEqual MatchResult(
          preFillerMatch = wordTokens("a", "b"),
          fillerMatch = wordTokens("C", "D"),
          postFillerMatch = wordTokens("e", "f")
        )
      }

      it("should repeatedly match a simple rule") {
        val text = textToTokenIterator(
          "start  a b C D e f  x x x  a b C D e f   end"
        )
        val result = patternItemRule()._exactMatch(text)
        result.size shouldEqual 2
        result.first() shouldEqual MatchResult(
          preFillerMatch = wordTokens("a", "b"),
          fillerMatch = wordTokens("C", "D"),
          postFillerMatch = wordTokens("e", "f")
        )
        result.last() shouldEqual MatchResult(
          preFillerMatch = wordTokens("a", "b"),
          fillerMatch = wordTokens("C", "D"),
          postFillerMatch = wordTokens("e", "f")
        )
      }

      it("should NOT find matches when prefiller doesn't match") {
        val text = textToTokenIterator("x C D e f")
        val result = patternItemRule()._exactMatch(text)
        result.size shouldEqual 0
      }

      it("should NOT find matches when filler doesn't match") {
        val text = textToTokenIterator("a b X e f")
        val result = patternItemRule()._exactMatch(text)
        result.size shouldEqual 0
      }

      it("should NOT find matches when postfiller doesn't match") {
        val text = textToTokenIterator("a b C D x")
        val result = patternItemRule()._exactMatch(text)
        result.size shouldEqual 0
      }

      it("should find a match when preFiller patterns have more than one constraint") {
        val rule = BaseRule(
          preFiller = Pattern(patternElement = patternItemOfWords("a", "b")),
          filler = patternOfItemWords("C", "D"),
          postFiller = patternOfItemWords("e", "f"),
          slot = anySlot()
        )
        val result = rule._exactMatch(textToTokenIterator("a C D e f xxxxx b C D e f"))
        result.size shouldEqual 2
        result.first() shouldEqual MatchResult(
          preFillerMatch = wordTokens("a"),
          fillerMatch = wordTokens("C", "D"),
          postFillerMatch = wordTokens("e", "f")
        )
        result.last() shouldEqual MatchResult(
          preFillerMatch = wordTokens("b"),
          fillerMatch = wordTokens("C", "D"),
          postFillerMatch = wordTokens("e", "f")
        )
      }

      it("should find a match when postFiller patterns have more than one constraint") {
        val rule = BaseRule(
          preFiller = patternOfItemWords("a", "b"),
          filler = patternOfItemWords("C", "D"),
          postFiller = Pattern(patternElement = patternItemOfWords("e", "f")),
          slot = anySlot()
        )
        val result = rule._exactMatch(textToTokenIterator("a b C D e xxxxx a b C D f"))
        result.size shouldEqual 2
        result.first() shouldEqual MatchResult(
          preFillerMatch = wordTokens("a", "b"),
          fillerMatch = wordTokens("C", "D"),
          postFillerMatch = wordTokens("e")
        )
        result.last() shouldEqual MatchResult(
          preFillerMatch = wordTokens("a", "b"),
          fillerMatch = wordTokens("C", "D"),
          postFillerMatch = wordTokens("f")
        )
      }
    }

/*    describe("pattern list") {

      *//**
       * Expanded patterns:
       *
       * - prefiller filler postfiller
       * - prefiller filler
       * - filler postfiller
       * - filler
       *//*
      val patternListRule = { BaseRule(
        preFiller = Pattern(PatternList(words("prefiller"), length = 1)),
        filler = Pattern(PatternItem(words("filler"))),
        postFiller = Pattern(PatternList(words("postfiller"), length = 1)),
        slot = anySlot()
      )}


      it("should match full expansion") {

        val text = textToTokenIterator("prefiller filler postfiller")
        val result = patternListRule()._exactMatch(text)

        assertEquals(wordTokens("filler"), result.distinct())
      }

      it("should generate all patterns") {

        val text = textToTokenIterator("prefiller filler postfiller")
        val result = patternListRule()._exactMatch(text)

        assertEquals(4, result.size)
      }

      it("should match variation 1: no prefiller") {

        val text = textToTokenIterator("start filler postfiller end")
        val result = patternListRule()._exactMatch(text)

        assertEquals(wordTokens("filler"), result.distinct())
      }

      it("should match variation 2: no postfiller") {

        val text = textToTokenIterator("start prefiller filler end")

        val result = patternListRule()._exactMatch(text)

        assertEquals(wordTokens("filler"), result.distinct())
      }

      it("should match variation 3: no prefiller/postfiller") {

        val text = textToTokenIterator("start filler end")

        val result = patternListRule()._exactMatch(text)

        assertEquals(wordTokens("filler"), result.distinct())
      }

      it("should match variation 4: empty") {

        val text = textToTokenIterator("start end")

        val result = patternListRule()._exactMatch(text)

        assertEquals(emptyList(), result.distinct())
      }
    }*/
  }
})

