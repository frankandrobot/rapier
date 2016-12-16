package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.meta.Slot
import com.frankandrobot.rapier.meta.SlotFiller
import com.frankandrobot.rapier.meta.SlotName
import com.frankandrobot.rapier.nlp.wordTokens
import com.frankandrobot.rapier.pattern.BaseRule
import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.patternItemOfWords
import com.frankandrobot.rapier.patternOfWordItems
import com.frankandrobot.rapier.patternOfWordsList
import com.frankandrobot.rapier.textToTokenIterator
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotContain
import org.funktionale.option.Option.None
import org.funktionale.option.Option.Some
import org.jetbrains.spek.api.Spek
import java.util.*


class MatchSpec : Spek({

  val anyText = { textToTokenIterator("A a b C D e f Z") }
  val anySlot = { Slot(SlotName("any slot"), slotFillers = HashSet<SlotFiller>()) }

  describe("match") {

    describe("pattern items") {

      val patternItemRule = { BaseRule(
        preFiller = patternOfWordItems("a", "b"),
        filler = patternOfWordItems("C", "D"),
        postFiller = patternOfWordItems("e", "f"),
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
          filler = patternOfWordItems("C", "D"),
          postFiller = patternOfWordItems("e", "f"),
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
          preFiller = patternOfWordItems("a", "b"),
          filler = patternOfWordItems("C", "D"),
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

    describe("pattern list") {

      /**
       * Expanded patterns:
       *
       * - a B c
       * - a B
       * - B c
       * - B
       */
      val patternListRule = { BaseRule(
        preFiller = patternOfWordsList(length = 1, word = "a"),
        filler = patternOfWordItems("B"),
        postFiller = patternOfWordsList(length = 1, word = "c"),
        slot = anySlot()
      )}
      val text = { textToTokenIterator("a B c") }

      it("should match all expanded patterns") {

        val result = patternListRule()._exactMatch(text())
        result.size shouldEqual 4
      }

      it("should match full expansion: a B c") {

        val result = patternListRule()._exactMatch(text())
        result shouldContain MatchResult(
          preFillerMatch = wordTokens("a"),
          fillerMatch = wordTokens("B"),
          postFillerMatch = wordTokens("c")
        )
      }

      it("should match the expansions with no prefiller: B c and B") {
        val text = textToTokenIterator("start B c end")
        val result = patternListRule()._exactMatch(text)
        result.size shouldEqual 2
        result shouldContain MatchResult(
          preFillerMatch = None,
          fillerMatch = Some(wordTokens("B")),
          postFillerMatch = Some(wordTokens("c"))
        )
        result shouldContain MatchResult(
          preFillerMatch = None,
          fillerMatch = Some(wordTokens("B")),
          postFillerMatch = None
        )
      }

      it("should match the expansions with no postfiller: a B and B") {
        val text = textToTokenIterator("start a B end")
        val result = patternListRule()._exactMatch(text)
        result.size shouldEqual 2
        result shouldContain MatchResult(
          preFillerMatch = Some(wordTokens("a")),
          fillerMatch = Some(wordTokens("B")),
          postFillerMatch = None
        )
        result shouldContain MatchResult(
          preFillerMatch = None,
          fillerMatch = Some(wordTokens("B")),
          postFillerMatch = None
        )
      }

      it("should match the expansion with no prefiller/postfiller: B") {
        val text = textToTokenIterator("start B end")
        val result = patternListRule()._exactMatch(text)
        result.size shouldEqual 1
        result shouldContain MatchResult(
          preFillerMatch = None,
          fillerMatch = Some(wordTokens("B")),
          postFillerMatch = None
        )
      }

      it("should NOT match when filler is missing") {
        val text = textToTokenIterator("start end")
        val result = patternListRule()._exactMatch(text)
        result shouldNotContain MatchResult(
          preFillerMatch = None,
          fillerMatch = None,
          postFillerMatch = None
        )
      }

      it("should NOT match when filler is missing") {
        val text = textToTokenIterator("start end")
        val patternListRule = BaseRule(
          preFiller = patternOfWordsList(length = 1, word = "a"),
          filler = patternOfWordsList(length = 1, word = "B"),
          postFiller = patternOfWordsList(length = 1, word = "c"),
          slot = anySlot()
        )

        val result = patternListRule._exactMatch(text)
        result shouldNotContain MatchResult(
          preFillerMatch = None,
          fillerMatch = None,
          postFillerMatch = None
        )
      }
    }
  }
})

