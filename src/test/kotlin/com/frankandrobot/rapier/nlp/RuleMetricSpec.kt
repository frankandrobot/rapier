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
  var anyRuleMatchingTwoFillers = Rule(slot = Slot("none"))

  var anyDocument = Document("none")
  var anyOtherDocument = Document("none")

  var anySimpleExample = Examples()
  var anyOtherExample = Examples()

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
    anyRuleMatchingTwoFillers = Rule(
      preFiller = Pattern(PatternItem("preFiller")),
      filler = Pattern(PatternItem(anyFiller.value, anyOtherFiller.value)),
      postFiller = Pattern(PatternItem("postFiller")),
      slot = anySlot
    )

    anyDocument = Document("preFiller ${anyFiller.value} postFiller")
    anyOtherDocument = Document("preFiller ${anyOtherFiller.value} postFiller")

    anySimpleExample = Examples(Template(anySlot), FilledTemplate(anyDocument, Pair(anySlot, anyFiller)))
    anyOtherExample = Examples(
      Template(anySlot),
      FilledTemplate(anyDocument, Pair(anySlot, anyFiller)),
      FilledTemplate(anyOtherDocument, Pair(anySlot, yetAnotherFiller))
    )
  }

  describe("RuleMetric") {

    describe("evaluate") {

      it("should find positive examples in simple rules") {

        val result = RuleMetric(anySimpleRule).evaluate(anySimpleExample, toTokens(anyDocument))

        assertEquals(listOf(anyFiller), result.first)
      }

      it("should find no negative examples in simple rules") {

        val result = RuleMetric(anySimpleRule).evaluate(anySimpleExample, toTokens(anyDocument))

        assertEquals(emptyList<SlotFiller>(), result.second)
      }

      it("should still find positive examples in complex rules") {

        val result = RuleMetric(anyRuleMatchingTwoFillers).evaluate(anyOtherExample, toTokens(anyDocument, anyOtherDocument))

        assertEquals(listOf(anyFiller), result.first)

      }

      it("should find negative examples in complex rules") {

        val result = RuleMetric(anyRuleMatchingTwoFillers).evaluate(anyOtherExample, toTokens(anyDocument, anyOtherDocument))

        assertEquals(listOf(anyOtherFiller), result.second)

      }
    }
  }
})

fun toTokens(vararg documents: Document) = documents.map{ it.value.split(" ").map{Token(it)} }
