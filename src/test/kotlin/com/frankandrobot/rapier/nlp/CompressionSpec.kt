package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.meta.*
import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.patternOfWordItems
import com.frankandrobot.rapier.rule.BaseRule
import com.frankandrobot.rapier.textTokenList
import com.frankandrobot.rapier.wordTokens
import org.amshove.kluent.shouldEqual
import org.funktionale.option.Option.None
import org.funktionale.option.Option.Some
import org.jetbrains.spek.api.Spek


class CompressionSpec : Spek({
  describe("testBestRule") {
    it("should return best rule if passes constraint") {
      /**
       * This rule has exactly 1 positive match and 0 negative matches
       */
      val rule = BaseRule(
        preFiller = Pattern(),
        filler = patternOfWordItems("a"),
        postFiller = Pattern(),
        slotName = SlotName("slot")
      )
      val example = Example(
        BlankTemplate("test", slots = slotNames("slot")),
        Document(tokens = textTokenList("xxxxx a xxxxxx")),
        FilledTemplate(slots(SlotName("slot") to slotFillers(wordTokens("a"))))
      )
      val examples = Examples(listOf(example))
      val result = testBestRule(rule, examples)
      result shouldEqual Some(rule)
    }

    fit("should not return best rule if does not pass constraint") {
      val rule = BaseRule(
        preFiller = Pattern(),
        filler = patternOfWordItems("b"),
        postFiller = Pattern(),
        slotName = SlotName("slot")
      )
      val example = Example(
        BlankTemplate("test", slots = slotNames("slot")),
        Document(tokens = textTokenList("xxxxx b xxxxxx")),
        FilledTemplate(slots(SlotName("slot") to slotFillers(wordTokens("a"))))
      )
      val examples = Examples(listOf(example))
      val result = testBestRule(rule, examples)
      result shouldEqual None
    }
  }
})
