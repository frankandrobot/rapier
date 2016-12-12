package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.meta.*
import com.frankandrobot.rapier.pattern.*
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
      slots = hashSetOf(SlotName("a"), SlotName("b"))
    )
    val example1 = Example(
      blankTemplate = blankTemplate,
      document = Document(tokens = wordTokens("a1","a2","b1","b2")),
      filledTemplate = FilledTemplate(
        slots = slots(
          SlotName("a") to slotFillers(wordTokens("a1"), wordTokens("a2")),
          SlotName("b") to disabledSlotFillers(wordTokens("b1","b2"))
        )
      )
    )
    val example2 = Example(
      blankTemplate = blankTemplate,
      document = Document(tokens = wordTokens("A1","A2","B1","B2")),
      filledTemplate = FilledTemplate(
        slots = slots(
          SlotName("a") to slotFillers(wordTokens("A1","A2")),
          SlotName("b") to disabledSlotFillers(wordTokens("B1","B2"))
        )
      )
    )
    var result : List<Pair<SlotName, List<IRule>>> = emptyList()

    beforeEach() {
      result = mostSpecificRules(blankTemplate, Examples(listOf(example1, example2)))
    }

    it("should ignore disabled slots") {
      val cResult = result.find{ it.first == SlotName("b") }
      cResult `should not be` null
      cResult!!.second.size shouldEqual 0
    }

    it("should get rules for all examples for a") {
      val aResult = result.find{ it.first == SlotName("a") }
      aResult `should not be` null
      val aRules = aResult!!.second

      aRules.size shouldEqual 3

      aRules shouldContain MostSpecificRule(
        preFiller = Pattern(),
        filler = patternOfItemWords("a1"),
        postFiller = patternOfItemWords("a2","b1","b2"),
        slot = example1[SlotName("a")]
      )

      aRules shouldContain MostSpecificRule(
        preFiller = patternOfItemWords("a1"),
        filler = patternOfItemWords("a2"),
        postFiller = patternOfItemWords("b1","b2"),
        slot = example1[SlotName("a")]
      )

      aRules shouldContain MostSpecificRule(
        preFiller = Pattern(),
        filler = patternOfItemWords("A1","A2"),
        postFiller = patternOfItemWords("B1","B2"),
        slot = example2[SlotName("a")]
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
      doc = wordTokens("1", "2", "3", "4")

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
          preFiller = patternOfItemWords("1"),
          filler = patternOfItemWords("2", "3"),
          postFiller = patternOfItemWords("4"),
          slot = slot
        )

        result[0] shouldEqual expected
      }
    }


    it("should create one rule") {
      1 shouldEqual result.size
    }

    it("should create a correct IRule when no prefiller") {

      val tokens = wordTokens("2", "3", "4")
      val result = mostSpecificSlotRules(slot, tokens)

      val expected = MostSpecificRule(
        preFiller = Pattern(),
        filler = patternOfItemWords("2", "3"),
        postFiller = patternOfItemWords("4"),
        slot = slot
      )

      result[0] shouldEqual expected
    }

    it("should create a correct IRule when no postfiller") {

      val tokens = wordTokens("1", "2", "3")
      val result = mostSpecificSlotRules(slot, tokens)

      val expected = MostSpecificRule(
        preFiller = patternOfItemWords("1"),
        filler = patternOfItemWords("2", "3"),
        postFiller = Pattern(),
        slot = slot
      )

      result[0] shouldEqual expected
    }

    it("should find multiple matches") {

      val tokens = wordTokens("2", "3", "2", "3")
      val result = mostSpecificSlotRules(slot, tokens)

      result.size shouldEqual 2
      result[0] shouldEqual MostSpecificRule(
        preFiller = Pattern(),
        filler = patternOfItemWords("2", "3"),
        postFiller = patternOfItemWords("2", "3"),
        slot = slot
      )
      result[1] shouldEqual MostSpecificRule(
        preFiller = patternOfItemWords("2", "3"),
        filler = patternOfItemWords("2", "3"),
        postFiller = Pattern(),
        slot = slot
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
        preFiller = patternOfItemWords("1"),
        filler = patternOfItemWords("2"),
        postFiller = patternOfItemWords("3", "4"),
        slot = slot
      )
      result shouldContain MostSpecificRule(
        preFiller = patternOfItemWords("1", "2"),
        filler = patternOfItemWords("3"),
        postFiller = patternOfItemWords("4"),
        slot = slot
      )
    }

    it("should find multiple matches in correct location") {
      slot = Slot(
        SlotName("any slot"),
        hashSetOf(SlotFiller(tokens = wordTokens("a", "a")))
      )
      doc = wordTokens("a", "a", "a", "a")
      result = mostSpecificSlotRules(slot, doc)

      result.size shouldEqual 2
      result shouldContain MostSpecificRule(
        preFiller = Pattern(),
        filler = patternOfItemWords("a","a"),
        postFiller = patternOfItemWords("a","a"),
        slot = slot
      )
      result shouldContain MostSpecificRule(
        preFiller = patternOfItemWords("a","a"),
        filler = patternOfItemWords("a","a"),
        postFiller = Pattern(),
        slot = slot
      )
    }

    it("should work when the slot doesn't repeat in document") {

      slot = Slot(
        SlotName("any slot"),
        hashSetOf(SlotFiller(tokens = wordTokens("a")))
      )
      doc = wordTokens("a", "b", "c", "d")

      result = mostSpecificSlotRules(slot, doc)

      result.size shouldEqual 1
      result.first() shouldEqual MostSpecificRule(
        preFiller = Pattern(),
        filler = patternOfItemWords("a"),
        postFiller = patternOfItemWords("b", "c", "d"),
        slot = slot
      )
    }

    it("should work when slot repeats in document") {

      slot = Slot(
        SlotName("any slot"),
        hashSetOf(SlotFiller(tokens = wordTokens("a")))
      )
      doc = wordTokens("a", "b", "a", "d")

      result = mostSpecificSlotRules(slot, doc)

      result.size shouldEqual 2
      result shouldContain MostSpecificRule(
        preFiller = Pattern(),
        filler = patternOfItemWords("a"),
        postFiller = patternOfItemWords("b", "a", "d"),
        slot = slot
      )
      result shouldContain MostSpecificRule(
        preFiller = patternOfItemWords("a", "b"),
        filler = patternOfItemWords("a"),
        postFiller = patternOfItemWords("d"),
        slot = slot
      )
    }
  }
})
