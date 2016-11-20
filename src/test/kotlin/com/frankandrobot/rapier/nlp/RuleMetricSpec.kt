package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.document.Document
import com.frankandrobot.rapier.pattern.BaseRule
import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.words
import com.frankandrobot.rapier.template.*
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class RuleMetricSpec : Spek({

  var anySlot : Slot
  var anyFiller  = SlotFiller("none")
  var anyOtherFiller = SlotFiller("none")
  var yetAnotherFiller = SlotFiller("none")

  var anySimpleRule = BaseRule(slot = Slot("none"))
  var anyRuleMatchingTwoFillers = BaseRule(slot = Slot("none"))

  var anyDocument = Document("none")
  var anyOtherDocument = Document("none")

  var anySimpleExample = Examples()
  var anyOtherExample = Examples()

  val anyMinCov = 1
  val anyRuleSize = 1.0


  beforeEach {

    anySlot = Slot("anySlot")

    anyFiller = SlotFiller("java")

    anyOtherFiller = SlotFiller("c#")

    yetAnotherFiller = SlotFiller("python")

    anySimpleRule = BaseRule(
      preFiller = Pattern(PatternItem(words("preFiller"))),
      filler = Pattern(PatternItem(words(anyFiller.value))),
      postFiller = Pattern(PatternItem(words("postFiller"))),
      slot = anySlot
    )

    anyRuleMatchingTwoFillers = BaseRule(
      preFiller = Pattern(PatternItem(words("preFiller"))),
      filler = Pattern(PatternItem(words(anyFiller.value, anyOtherFiller.value))),
      postFiller = Pattern(PatternItem(words("postFiller"))),
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

        val result = RuleMetric(
          anySimpleRule,
          kMinCov = anyMinCov,
          kRuleSizeWeight = anyRuleSize
        ).evaluate(anySimpleExample, toTokens(anyDocument))

        assertEquals(listOf(anyFiller), result.positives)
      }


      it("should find no negative examples in simple rules") {

        val result = RuleMetric(
          anySimpleRule,
          kMinCov = anyMinCov,
          kRuleSizeWeight = anyRuleSize
        ).evaluate(anySimpleExample, toTokens(anyDocument))

        assertEquals(emptyList<SlotFiller>(), result.negatives)
      }


      it("should still find positive examples in complex rules") {

        val result = RuleMetric(
          anyRuleMatchingTwoFillers,
          kMinCov = anyMinCov,
          kRuleSizeWeight = anyRuleSize
        ).evaluate(anyOtherExample, toTokens(anyDocument, anyOtherDocument))

        assertEquals(listOf(anyFiller), result.positives)
      }


      it("should find negative examples in complex rules") {

        val result = RuleMetric(
          anyRuleMatchingTwoFillers,
          kMinCov = anyMinCov,
          kRuleSizeWeight = anyRuleSize
        ).evaluate(anyOtherExample, toTokens(anyDocument, anyOtherDocument))

        assertEquals(listOf(anyOtherFiller), result.negatives)
      }
    }


    /**
     * Maxima code:
     *
     * log2(x) := log(x) / log(2);
     * f(p,n,ruleSize) := -1.442695*log2((p+1)/(p+n+2)) + ruleSize/p;
     */
    describe("metric") {

      it ("should use the correct formula 1") {

        val anyPvalue = 2
        val anyNvalue = 3
        val anyRuleSize = 0.4
        val kMinCov = 0
        val expected = 1.963539434299987
        val actual = metric(p = anyPvalue, n = anyNvalue, ruleSize = anyRuleSize,
          kMinCov = kMinCov)

        assert(Math.abs(expected - actual) < 0.00000000000001)
      }


      it ("should use the correct formula 2") {

        val anyPvalue = 10
        val anyNvalue = 5
        val anyRuleSize = 0.8
        val kMinCov = 0
        val expected = 0.9860575047077227
        val actual = metric(p = anyPvalue, n = anyNvalue, ruleSize = anyRuleSize,
          kMinCov = kMinCov)

        assert(Math.abs(expected - actual) < 0.00000000000001)
      }


      it ("rules that don't cover enough positive matches should evaluate to infinity") {

        val pValue = 2
        val anyNvalue = 3
        val anyRuleSize = 0.9860575047077227
        val kMinCov = 3
        val actual = metric(pValue, anyNvalue, anyRuleSize, kMinCov)

        assertEquals(Double.POSITIVE_INFINITY, actual)
      }


      it("rules that cover more positive matches should evaluate to lower values") {

        val anyRuleSize = 0.1
        val anyMinCov = 1
        val anyNvalue = 5

        val rule = metric(p = 5, n = anyNvalue, ruleSize = anyRuleSize, kMinCov = anyMinCov)
        val betterRule = metric(p = 6, n = anyNvalue, ruleSize = anyRuleSize, kMinCov = anyMinCov)

        assert(betterRule < rule)
      }

      it("rules that cover more negative matches should evaluate to higher values") {

        val anyRuleSize = 0.1
        val anyMinCov = 1
        val anyPvalue = 5

        val rule = metric(p = anyPvalue, n = 5, ruleSize = anyRuleSize, kMinCov = anyMinCov)
        val worseRule = metric(p = anyPvalue, n = 6, ruleSize = anyRuleSize, kMinCov = anyMinCov)

        assert(worseRule > rule)
      }
    }
  }
})

fun toTokens(vararg documents: Document) = documents.map{ it.value.split(" ").map{Token(it)} }
