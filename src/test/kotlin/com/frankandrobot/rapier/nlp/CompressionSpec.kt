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

package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.*
import com.frankandrobot.rapier.meta.*
import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.rule.*
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


  describe("main loop") {

    it("should increment n") {}

    it("should exit when perfect rule found") {}

    it("should exit when more than consecutive number of failures") {}

    it("should call specializePreFiller with n") {}

    it("should call specializePostFiller with n") {}

    it("should add new prefiller rules to rule list") {}

    it("should add new postfiller rules to rule list") {}
  }


  /**
   * This example fails because rules are identical (nothing to generalize)
   */
  describe("example with identical rules") {
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
    val examples = Examples(listOf(
      Example(
        BlankTemplate(name = "test", slots = slotNames("slot")),
        Document(tokens = textTokenList("xxxx a xxxx")),
        FilledTemplate(slots(
          SlotName("slot") to slotFillers(wordTokens("a"))
        ))
      )
    ))
    val params = RapierParams()

    it("should compress to one rule") {
      val result = arrayListOf(rule1, rule2).compressRuleArray(params, examples)
      result shouldEqual listOf(DerivedRule(
        preFiller = Pattern(),
        filler = patternOfWordItems("a"),
        postFiller = Pattern(),
        slotName = SlotName("slot"),
        baseRule1 = rule1,
        baseRule2 = rule2
      ))
    }
  }


  describe("example with identical pre/post fillers") {
    val rule1 = BaseRule(
      preFiller = patternOfWordItems("1"),
      filler = patternOfWordItems("a"),
      postFiller = patternOfWordItems("2"),
      slotName = SlotName("slot")
    )
    val rule2 = BaseRule(
      preFiller = patternOfWordItems("1"),
      filler = patternOfWordItems("b"),
      postFiller = patternOfWordItems("2"),
      slotName = SlotName("slot")
    )
    val examples = Examples(listOf(
      Example(
        BlankTemplate(name = "test", slots = slotNames("slot")),
        Document(tokens = textTokenList("xxxx a xxxx b xxxx")),
        FilledTemplate(slots(
          SlotName("slot") to slotFillers(wordTokens("a"), wordTokens("b"))
        ))
      )
    ))
    val params = RapierParams(metricMinPositiveMatches = 1)

    it("should compress to one rule") {
      val result = arrayListOf(rule1, rule2).compressRuleArray(params, examples)

      result shouldEqual listOf(DerivedRule(
        preFiller = Pattern(),
        filler = Pattern(patternItemOfWords("a","b")),
        postFiller = Pattern(),
        slotName = SlotName("slot"),
        baseRule1 = rule1,
        baseRule2 = rule2
      ))
    }
  }


  describe("example with longer identical pre/post fillers") {
    val rule1 = BaseRule(
      preFiller = patternOfWordItems("1","2"),
      filler = patternOfWordItems("a"),
      postFiller = patternOfWordItems("3","4"),
      slotName = SlotName("slot")
    )
    val rule2 = BaseRule(
      preFiller = patternOfWordItems("1","2"),
      filler = patternOfWordItems("b"),
      postFiller = patternOfWordItems("3","4"),
      slotName = SlotName("slot")
    )
    val examples = Examples(listOf(
      Example(
        BlankTemplate(name = "test", slots = slotNames("slot")),
        Document(tokens = textTokenList("xxxx 1 2 a 3 4 xxxx 1 2 b 3 4 xxxx")),
        FilledTemplate(slots(
          SlotName("slot") to slotFillers(wordTokens("a"), wordTokens("b"))
        ))
      )
    ))
    val params = RapierParams(metricMinPositiveMatches = 1, ruleSizeWeight = 0.001)

    it("should compress to one rule") {
      val result = arrayListOf(rule1, rule2).compressRuleArray(params, examples)

      result.map(::toBaseRule) shouldEqual listOf(BaseRule(
        preFiller = patternOfWordsList(1, "2"),
        filler = Pattern(patternItemOfWords("a","b")),
        postFiller = patternOfWordsList(1, "3"),
        slotName = SlotName("slot")
      ))
    }
  }
})
