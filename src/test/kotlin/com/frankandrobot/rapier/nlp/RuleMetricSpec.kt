package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.document.Document
import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.Rule
import com.frankandrobot.rapier.template.*
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class RuleMetricSpec : Spek({

  var anySimpleRule : Rule = Rule(slot = Slot("none"))
  var anySlot : Slot
  var anySlotFiller : SlotFiller = SlotFiller("none")
  var anyDocument : Document
  var anyExamples : Examples = Examples()

  var anyRule : Rule = Rule(slot = Slot("none"))


  beforeEach {

    anySlot = Slot("anySlot")
    anySlotFiller = SlotFiller("anySlotFiller")

    anySimpleRule = Rule(
      preFiller = Pattern(PatternItem("preFiller")),
      filler = Pattern(PatternItem(anySlotFiller.value)),
      postFiller = Pattern(PatternItem("postFiller")),
      slot = anySlot
    )
    anyDocument = Document("preFiller anySlotFiller postFiller")
    anyExamples = Examples(
      template = Template(anySlot),
      filledTemplates = listOf(FilledTemplate(Pair(anySlot, anySlotFiller), document = anyDocument)),
      documents = listOf(anyDocument)
    )
  }

  describe("RuleMetric") {

    describe("evaluate") {

      it("should find positive examples in simple rules") {

        val result = RuleMetric(anySimpleRule).evaluate(anyExamples)

        assertEquals(listOf(anySlotFiller), result.first)
      }

      it("should find no negative examples in simple rules") {

        val result = RuleMetric(anySimpleRule).evaluate(anyExamples)

        assertEquals(emptyList<SlotFiller>(), result.second)
      }
    }
  }
})
