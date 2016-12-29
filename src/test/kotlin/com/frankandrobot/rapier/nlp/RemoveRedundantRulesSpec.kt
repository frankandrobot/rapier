package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.meta.*
import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.patternOfWordItems
import com.frankandrobot.rapier.patternOfWordsList
import com.frankandrobot.rapier.rule.BaseRule
import com.frankandrobot.rapier.textTokenList
import com.frankandrobot.rapier.wordTokens
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek


class RemoveRedundantRulesSpec : Spek({

  describe("#removeRedundantRules") {
    val rule = BaseRule(
      preFiller = Pattern(),
      filler = patternOfWordItems("hi"),
      postFiller = Pattern(),
      slotName = SlotName("slot")
    )
    val newRule = BaseRule(
      preFiller = Pattern(),
      filler = patternOfWordsList(1, "hi", "ho"),
      postFiller = Pattern(),
      slotName = SlotName("slot")
    )
    val example = Example(
      BlankTemplate(name = "test", slots = slotNames("slot")),
      Document(tokens = textTokenList("AAAA hi ho BBBBB CCCC")),
      FilledTemplate(
        slots(SlotName("slot") to slotFillers(wordTokens("hi"), wordTokens("ho")))
      )
    )

    it("should remove a redundant rule") {
      val result = listOf(rule).removeRedundantRules(newRule, Examples(listOf(example)))
      result.size shouldEqual 0
    }

    it("should not remove a rule that is not redundant") {
      val result = listOf(newRule).removeRedundantRules(rule, Examples(listOf(example)))
      result shouldEqual listOf(newRule)
    }

    it("should fail if slots don't match") {
      val anotherRule = BaseRule(Pattern(), Pattern(), Pattern(), SlotName("uh oh"))
      var fail = true
      try {
        listOf(anotherRule).removeRedundantRules(rule, Examples(listOf(example)))
      }
      catch(e : AssertionError) {
        fail = false
      }
      fail shouldEqual false
    }
  }
})
