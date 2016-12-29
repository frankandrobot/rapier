package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.emptyExamples
import com.frankandrobot.rapier.meta.*
import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.patternOfWordItems
import com.frankandrobot.rapier.rule.BaseRule
import com.frankandrobot.rapier.rule.ComparableRule
import com.frankandrobot.rapier.rule.IRule
import com.frankandrobot.rapier.rule.derivedRuleWithEmptyPreAndPostFillers
import com.frankandrobot.rapier.textTokenList
import com.frankandrobot.rapier.wordTokens
import org.amshove.kluent.shouldEqual
import org.funktionale.option.Option.None
import org.funktionale.option.Option.Some
import org.jetbrains.spek.api.Spek
import java.util.*


class CompressionSpec : Spek({

  describe("setup") {

    val params = RapierParams(compressionRandomPairs = 1)
    val rule1 = BaseRule(
      preFiller = Pattern(),
      filler = patternOfWordItems("a"),
      postFiller = Pattern(),
      slotName = SlotName("slot")
    )
    val rule2 = BaseRule(
      preFiller = Pattern(),
      filler = patternOfWordItems("a"),
      postFiller = Pattern(),
      slotName = SlotName("slot")
    )
    val list = arrayListOf(rule1, rule2)


    it("should generate random pairs using rapier param") {
      var called = false

      val randomPairs = { list : ArrayList<out IRule>, k : Int ->
        k shouldEqual params.compressionRandomPairs
        called = true
        emptyList<Pair<IRule,IRule>>()
      }

      setup(list = arrayListOf(), params = params, examples = emptyExamples,
        randomPairs = randomPairs)

      called shouldEqual true
    }


    it("should create the initial rule list") {
      val randomPairs = { list : ArrayList<out IRule>, k : Int ->
        listOf(Pair(list[0], list[1]))
      }
      val result = setup(list = list, params = params, examples = emptyExamples, randomPairs
      = randomPairs)

      result shouldEqual listOf(
        derivedRuleWithEmptyPreAndPostFillers(
          pattern = patternOfWordItems("a"),
          baseRule1 = rule1,
          baseRule2 = rule2
        )
      ).map{ ComparableRule(examples = emptyExamples, params = params, rule = it) }
    }

    it("should call #randomPairsWrapper correctly") {
      val result = setup(list = list, params = params, examples = emptyExamples, randomPairs
      = randomPairsWrapper(Random()))

      result shouldEqual listOf(
        derivedRuleWithEmptyPreAndPostFillers(
          pattern = patternOfWordItems("a"),
          baseRule1 = rule1,
          baseRule2 = rule2
        )
      ).map{ ComparableRule(examples = emptyExamples, params = params, rule = it) }
    }
  }


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

    it("should not return best rule if does not pass constraint") {
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
