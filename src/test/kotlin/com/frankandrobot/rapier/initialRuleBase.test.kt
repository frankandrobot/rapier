package com.frankandrobot.rapier

import com.frankandrobot.rapier.document.Document
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.Rule
import com.frankandrobot.rapier.template.Slot
import com.frankandrobot.rapier.template.SlotFiller
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class InitialRuleBaseTest : Spek({

  data class SimplifiedRule(val preFiller : List<String>, val filler : List<String>, val postFiller : List<String>) {
    constructor(rule : Rule) : this(
      rule.preFiller().flatMap{ it.wordConstraints }.map{ it.value },
      rule.filler().flatMap{ it.wordConstraints }.map{ it.value },
      rule.postFiller().flatMap{ it.wordConstraints }.map{ it.value }
    )
  }

  describe("#initialRuleBase") {

    it("should work when the slot doesn't repeat in document") {

      val slot = Pair(Slot("salary"), SlotFiller("ten"))
      val document = Document("one ten foo")

      val result = initialRuleBase(slot, document)
      val rule = result.first()

      assertEquals(result.size, 1)
      assertEquals(
        SimplifiedRule(rule),
        SimplifiedRule(preFiller = listOf("one"), filler = listOf("ten"), postFiller = listOf("foo"))
      )
    }

    it("should work when slot repeats in document") {

      val slot = Pair(Slot("salary"), SlotFiller("ten"))
      val document = Document("one ten foo ten")

      val result = initialRuleBase(slot, document)
      val rule1 = result[0]
      val rule2 = result[1]

      assertEquals(result.size, 2)
      assertEquals(
        SimplifiedRule(rule1),
        SimplifiedRule(preFiller = listOf("one"), filler = listOf("ten"), postFiller = listOf("foo", "ten"))
      )
      assertEquals(
        SimplifiedRule(rule2),
        SimplifiedRule(preFiller = listOf("one", "ten", "foo"), filler = listOf("ten"), postFiller = listOf())
      )
    }
  }

  describe("#_initialRule") {

    val anySlot = {Slot("any slot")}

    describe("create initial Rule from prefiller, filler, postfiller strings") {

      val prefiller = "one"
      val filler = "two three"
      val postfiller = "four"

      val result = _initialRule(prefiller, filler, postfiller, anySlot())

      val patternElements = result.preFiller() + result.filler() + result.postFiller()

      it("should create PatternElements with *no* semantic constraints") {

        patternElements.forEach{ assertEquals(it.semanticConstraints.size, 0) }
      }

      it("should create PatternElements that are PatternItems only") {

        patternElements.forEach{ assert(it is PatternItem) }
      }

      it("should create a correct Rule") {

        assertEquals(
          SimplifiedRule(result),
          SimplifiedRule(preFiller = listOf(prefiller), filler = filler.split(" "), postFiller = listOf(postfiller))
        )
      }

      it("should create a correct Rule when no prefiller") {

        val result = _initialRule("", "one", "two", anySlot())

        assertEquals(
          SimplifiedRule(result),
          SimplifiedRule(preFiller = listOf<String>(), filler = listOf("one"), postFiller = listOf("two"))
        )
      }

      it("should create a correct Rule when no postfiller") {

        val result = _initialRule("one", "two", "", anySlot())

        assertEquals(
          SimplifiedRule(result),
          SimplifiedRule(preFiller = listOf("one"), filler = listOf("two"), postFiller = listOf<String>())
        )
      }
    }
  }
})
