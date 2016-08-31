package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.document.Document
import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.Rule
import com.frankandrobot.rapier.template.*
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class RuleMetricSpec : Spek({

  var anySlot : Slot
  var anyFiller  = SlotFiller("none")
  var anyOtherFiller = SlotFiller("none")
  var yetAnotherFiller = SlotFiller("none")

  var anySimpleRule = Rule(slot = Slot("none"))
  var anyRuleWithDisjunction = Rule(slot = Slot("none"))

  var anyDocument = emptyList<Token>()
  var anyOtherDocument = emptyList<Token>()


  beforeEach {

    anySlot = Slot("anySlot")
    anyFiller = SlotFiller("java")
    anyOtherFiller = SlotFiller("c#")
    yetAnotherFiller = SlotFiller("python")

    anySimpleRule = Rule(
      preFiller = Pattern(PatternItem("preFiller")),
      filler = Pattern(PatternItem(anyFiller.value)),
      postFiller = Pattern(PatternItem("postFiller")),
      slot = anySlot
    )
    anyRuleWithDisjunction = Rule(
      preFiller = Pattern(PatternItem("preFiller")),
      filler = Pattern(PatternItem(anyFiller.value, anyOtherFiller.value)),
      postFiller = Pattern(PatternItem("postFiller")),
      slot = anySlot
    )

    anyDocument = toTokens("preFiller ${anyFiller.value} postFiller")
    anyOtherDocument = toTokens("preFiller ${anyOtherFiller.value} postFiller")
  }

  describe("RuleMetric") {

    describe("evaluate") {

      it("should find positive examples in simple rules") {

        val result = RuleMetric(anySimpleRule).evaluate(hashSetOf(anyFiller), listOf(anyDocument))

        assertEquals(listOf(anyFiller), result.first)
      }

      it("should find no negative examples in simple rules") {

        val result = RuleMetric(anySimpleRule).evaluate(hashSetOf(anyFiller), listOf(anyDocument))

        assertEquals(emptyList<SlotFiller>(), result.second)
      }

      it("should still find positive examples in complex rules") {

        val result = RuleMetric(anyRuleWithDisjunction).evaluate(hashSetOf(anyFiller, yetAnotherFiller), listOf(anyDocument))

        assertEquals(listOf(anyFiller), result.first)

      }

      it("should find negative examples in complex rules") {

        val result = RuleMetric(anyRuleWithDisjunction).evaluate(hashSetOf(anyFiller, yetAnotherFiller), listOf(anyDocument, anyOtherDocument))

        assertEquals(listOf(anyOtherFiller), result.second)

      }
    }
  }
})

fun toTokens(words : String) = words.split(" ").map{Token(it)}
