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

package com.frankandrobot.rapier.rule

import com.frankandrobot.rapier.meta.*
import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternElement
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.patternOfWordItems
import com.frankandrobot.rapier.tokens
import com.frankandrobot.rapier.wordTokens
import org.amshove.kluent.`should not be`
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import java.util.*
import kotlin.test.assertEquals


class MostSpecificRulesSpec : Spek({

  describe("#mostSpecificRuleBase") {

    val blankTemplate = BlankTemplate(
      name = "test",
      slots = slotNames("a", "b")
    )
    val example1 = Example(
      blankTemplate = blankTemplate,
      document = Document(tokens = tokens("a1", "a2", "b1", "b2")),
      filledTemplate = FilledTemplate(
        slots = slots(
          SlotName("a") to slotFillers(wordTokens("a1"), wordTokens("a2")),
          SlotName("b") to disabledSlotFillers(wordTokens("b1", "b2"))
        )
      )
    )
    val example2 = Example(
      blankTemplate = blankTemplate,
      document = Document(tokens = tokens("A1", "A2", "B1", "B2")),
      filledTemplate = FilledTemplate(
        slots = slots(
          SlotName("a") to slotFillers(wordTokens("A1", "A2")),
          SlotName("b") to disabledSlotFillers(wordTokens("B1", "B2"))
        )
      )
    )
    var result : HashMap<SlotName, ArrayList<IRule>> = HashMap()

    beforeEach() {
      result = mostSpecificRules(blankTemplate, Examples(listOf(example1, example2)))
    }

    it("should ignore disabled slots") {
      val cResult = result[SlotName("b")]
      cResult `should not be` null
      cResult!!.size shouldEqual 0
    }

    it("should get rules for all examples for a") {
      val aResult = result[SlotName("a")]
      aResult `should not be` null
      val aRules = aResult!!

      aRules.size shouldEqual 3

      aRules shouldContain MostSpecificRule(
        preFiller = Pattern(),
        filler = patternOfWordItems("a1"),
        postFiller = patternOfWordItems("a2", "b1", "b2"),
        slotName = SlotName("a")
      )

      aRules shouldContain MostSpecificRule(
        preFiller = patternOfWordItems("a1"),
        filler = patternOfWordItems("a2"),
        postFiller = patternOfWordItems("b1", "b2"),
        slotName = SlotName("a")
      )

      aRules shouldContain MostSpecificRule(
        preFiller = Pattern(),
        filler = patternOfWordItems("A1", "A2"),
        postFiller = patternOfWordItems("B1", "B2"),
        slotName = SlotName("a")
      )
    }
  }

  describe("#mostSpecificSlotRule") {

    var slot = Slot(SlotName("any"), HashSet<SlotFiller>())
    var doc = ArrayList<Token>()
    var result = emptyList<IRule>()
    var patternElements = emptyList<PatternElement>()

    beforeEach {
      slot = Slot(
        SlotName("any slot"),
        hashSetOf(SlotFiller(tokens = wordTokens("2", "3")))
      )
      doc = tokens("1", "2", "3", "4")

      result = mostSpecificSlotRules(slot, doc)

      patternElements = result.flatMap{ it.preFiller() + it.filler() + it.postFiller() }
    }


    describe("algorithm requirements") {

      it("should create PatternElements with *no* semantic constraints") {

        patternElements.forEach{ assertEquals(it.semanticConstraints.size, 0) }
      }

      it("should create PatternElements that are PatternItems only") {

        patternElements.forEach{ assert(it is PatternItem) }
      }

      it("should create a correct IRule") {

        val expected = MostSpecificRule(
          preFiller = patternOfWordItems("1"),
          filler = patternOfWordItems("2", "3"),
          postFiller = patternOfWordItems("4"),
          slotName = slot.name
        )

        result[0] shouldEqual expected
      }
    }


    it("should create one rule") {
      1 shouldEqual result.size
    }

    it("should create a correct IRule when no prefiller") {

      val tokens = tokens("2", "3", "4")
      val result = mostSpecificSlotRules(slot, tokens)

      val expected = MostSpecificRule(
        preFiller = Pattern(),
        filler = patternOfWordItems("2", "3"),
        postFiller = patternOfWordItems("4"),
        slotName = slot.name
      )

      result[0] shouldEqual expected
    }

    it("should create a correct IRule when no postfiller") {

      val tokens = tokens("1", "2", "3")
      val result = mostSpecificSlotRules(slot, tokens)

      val expected = MostSpecificRule(
        preFiller = patternOfWordItems("1"),
        filler = patternOfWordItems("2", "3"),
        postFiller = Pattern(),
        slotName = slot.name
      )

      result[0] shouldEqual expected
    }

    it("should find multiple matches") {

      val tokens = tokens("2", "3", "2", "3")
      val result = mostSpecificSlotRules(slot, tokens)

      result.size shouldEqual 2
      result[0] shouldEqual MostSpecificRule(
        preFiller = Pattern(),
        filler = patternOfWordItems("2", "3"),
        postFiller = patternOfWordItems("2", "3"),
        slotName = slot.name
      )
      result[1] shouldEqual MostSpecificRule(
        preFiller = patternOfWordItems("2", "3"),
        filler = patternOfWordItems("2", "3"),
        postFiller = Pattern(),
        slotName = slot.name
      )
    }

    it("should find matches for each slot filler") {
      slot = Slot(
        SlotName("any slot"),
        slotFillers = hashSetOf(
          SlotFiller(tokens = wordTokens("2")),
          SlotFiller(tokens = wordTokens("3"))
        )
      )
      result = mostSpecificSlotRules(slot, doc)

      result.size shouldEqual 2
      result shouldContain MostSpecificRule(
        preFiller = patternOfWordItems("1"),
        filler = patternOfWordItems("2"),
        postFiller = patternOfWordItems("3", "4"),
        slotName = slot.name
      )
      result shouldContain MostSpecificRule(
        preFiller = patternOfWordItems("1", "2"),
        filler = patternOfWordItems("3"),
        postFiller = patternOfWordItems("4"),
        slotName = slot.name
      )
    }

    it("should find multiple matches in correct location") {
      slot = Slot(
        SlotName("any slot"),
        hashSetOf(SlotFiller(tokens = wordTokens("a", "a")))
      )
      doc = tokens("a", "a", "a", "a")
      result = mostSpecificSlotRules(slot, doc)

      result.size shouldEqual 2
      result shouldContain MostSpecificRule(
        preFiller = Pattern(),
        filler = patternOfWordItems("a", "a"),
        postFiller = patternOfWordItems("a", "a"),
        slotName = slot.name
      )
      result shouldContain MostSpecificRule(
        preFiller = patternOfWordItems("a", "a"),
        filler = patternOfWordItems("a", "a"),
        postFiller = Pattern(),
        slotName = slot.name
      )
    }

    it("should work when the slot doesn't repeat in document") {

      slot = Slot(
        SlotName("any slot"),
        hashSetOf(SlotFiller(tokens = wordTokens("a")))
      )
      doc = tokens("a", "b", "c", "d")

      result = mostSpecificSlotRules(slot, doc)

      result.size shouldEqual 1
      result.first() shouldEqual MostSpecificRule(
        preFiller = Pattern(),
        filler = patternOfWordItems("a"),
        postFiller = patternOfWordItems("b", "c", "d"),
        slotName = slot.name
      )
    }

    it("should work when slot repeats in document") {

      slot = Slot(
        SlotName("any slot"),
        hashSetOf(SlotFiller(tokens = wordTokens("a")))
      )
      doc = tokens("a", "b", "a", "d")

      result = mostSpecificSlotRules(slot, doc)

      result.size shouldEqual 2
      result shouldContain MostSpecificRule(
        preFiller = Pattern(),
        filler = patternOfWordItems("a"),
        postFiller = patternOfWordItems("b", "a", "d"),
        slotName = slot.name
      )
      result shouldContain MostSpecificRule(
        preFiller = patternOfWordItems("a", "b"),
        filler = patternOfWordItems("a"),
        postFiller = patternOfWordItems("d"),
        slotName = slot.name
      )
    }
  }
})
