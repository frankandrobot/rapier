package com.frankandrobot.rapier

import com.frankandrobot.rapier.meta.Slot
import com.frankandrobot.rapier.meta.SlotFiller
import com.frankandrobot.rapier.meta.SlotName
import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.nlp.wordTokens
import com.frankandrobot.rapier.pattern.*
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import java.util.*
import kotlin.test.assertEquals


class MostSpecificRulesSpec : Spek({

//  data class SimplifiedRule(val preFiller : List<String>,
//                            val filler : List<String>,
//                            val postFiller : List<String>) {
//    constructor(rule : IRule) : this(
//      rule.preFiller().flatMap{ it.wordConstraints }.map{ it.value },
//      rule.filler().flatMap{ it.wordConstraints }.map{ it.value },
//      rule.postFiller().flatMap{ it.wordConstraints }.map{ it.value }
//    )
//  }

  /*describe("#mostSpecificRuleBase") {

    it("should work when the slot doesn't repeat in document") {

      val slot = Pair(Slot("salary"), SlotFiller("ten"))
      val document = Document("one ten foo")

      val result = mostSpecificRuleBase(slot, document)
      val rule = result.first()

      assertEquals(result.size, 1)
      assertEquals(
        SimplifiedRule(rule),
        SimplifiedRule(preFiller = listOf("1"), filler = listOf("ten"), postFiller = listOf("foo"))
      )
    }

    it("should work when slot repeats in document") {

      val slot = Pair(Slot("salary"), SlotFiller("ten"))
      val document = Document("one ten foo ten")

      val result = mostSpecificRuleBase(slot, document)
      val rule1 = result[0]
      val rule2 = result[1]

      assertEquals(result.size, 2)
      assertEquals(
        SimplifiedRule(rule1),
        SimplifiedRule(preFiller = listOf("1"), filler = listOf("ten"), postFiller = listOf("foo", "ten"))
      )
      assertEquals(
        SimplifiedRule(rule2),
        SimplifiedRule(preFiller = listOf("1", "ten", "foo"), filler = listOf("ten"), postFiller = listOf())
      )
    }
  }*/

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
        fillers = hashSetOf(
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
  }
})
