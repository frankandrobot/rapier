package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.*
import com.frankandrobot.rapier.meta.*
import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.rule.BaseRule
import com.frankandrobot.rapier.rule.ComparableRule
import com.frankandrobot.rapier.rule.DerivedRule
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class RuleMetricSpec : Spek({

  var anySlot : Slot

  var aSimpleRule = emptyRule
  var aRuleWithTwoPatternItemsInFiller = emptyRule
  var aRuleWithTwoConstraintsInFiller = emptyRule
  var anyRuleWithNegativeMatches = emptyRule

  var aSimpleExample = emptyExample
  var anExampleWithTwoPatternItemsInFiller = emptyExample
  var anExampleWithTwoConstraintsInFiller = emptyExample
  var anyExampleWithNegativeMatches = emptyExample

  val anyMinCov = 1
  val anyRuleSize = 1.0
  val params = RapierParams(k_MinCov = anyMinCov, k_SizeWeight = anyRuleSize)


  beforeEach {

    anySlot = Slot(
      name = SlotName("language"),
      slotFillers = hashSetOf(
        wordSlotFiller("java"),
        wordSlotFiller("c#"),
        wordSlotFiller("go", "lang")
      )
    )

    aSimpleRule = BaseRule(
      preFiller = patternOfWordItems("A"),
      filler = patternOfWordItems("java"),
      postFiller = patternOfWordItems("Z"),
      slotName = anySlot.name
    )
    aSimpleExample = Example(
      BlankTemplate(name = "test", slots = slotNames("language")),
      Document(tokens = textTokenList("A java Z")),
      FilledTemplate(
        slots = slots(
          Slot(
            name = SlotName("language"),
            slotFillers = hashSetOf(
              wordSlotFiller("java")
            )
          )
        )
      )
    )

    aRuleWithTwoPatternItemsInFiller = BaseRule(
      preFiller = patternOfWordItems("A"),
      filler = Pattern(patternItemOfWords("java", "c#")),
      postFiller = patternOfWordItems("Z"),
      slotName = anySlot.name
    )
    anExampleWithTwoPatternItemsInFiller = Example(
      BlankTemplate(name = "test", slots = slotNames("language")),
      Document(tokens = textTokenList("A java Z xxxxxxx A c# Z")),
      FilledTemplate(
        slots = slots(
          Slot(
            name = SlotName("language"),
            slotFillers = hashSetOf(
              wordSlotFiller("java"),
              wordSlotFiller("c#")
            )
          )
        )
      )
    )

    aRuleWithTwoConstraintsInFiller = BaseRule(
      preFiller = patternOfWordItems("A"),
      filler = patternOfWordItems("go", "lang"),
      postFiller = patternOfWordItems("Z"),
      slotName = anySlot.name
    )
    anExampleWithTwoConstraintsInFiller = Example(
      BlankTemplate(name = "test", slots = slotNames("language")),
      Document(tokens = textTokenList("A go lang Z")),
      FilledTemplate(
        slots = slots(
          Slot(
            name = SlotName("language"),
            slotFillers = hashSetOf(
              wordSlotFiller("go", "lang")
            )
          )
        )
      )
    )

    anyRuleWithNegativeMatches = BaseRule(
      preFiller = patternOfWordItems("A"),
      filler = Pattern(patternItemOfWords("ruby", "rust")),
      postFiller = patternOfWordItems("Z"),
      slotName = anySlot.name
    )
    anyExampleWithNegativeMatches = Example(
      BlankTemplate(name = "test", slots = slotNames("language")),
      Document(tokens = textTokenList("A ruby Z xxxxx A rust Z")),
      FilledTemplate(
        slots = slots(
          Slot(
            name = SlotName("language"),
            slotFillers = hashSetOf(
              wordSlotFiller("ruby")
            )
          )
        )
      )
    )
  }


  describe("RuleMetric") {

    describe("metricResults") {

      it("should find positive matches in simple rules") {
        val result =
          RuleMetric(aSimpleRule, params, Examples(listOf(aSimpleExample))).metricResults
        result.positives.size shouldEqual 1
        result.positives shouldContain wordSlotFiller("java")
      }

      it("should find no negative matches in simple rules") {
        val result =
          RuleMetric(aSimpleRule, params, Examples(listOf(aSimpleExample))).metricResults
        result.negatives.size shouldEqual 0
      }

      it("should find two positive matches in example with two pattern items in filler") {
        val result =
          RuleMetric(aRuleWithTwoPatternItemsInFiller, params,
            Examples(listOf(anExampleWithTwoPatternItemsInFiller))).metricResults
        result.positives shouldEqual listOf(
          wordSlotFiller("java"),
          wordSlotFiller("c#")
        )
      }

      it("should find no negative matches in example with two pattern items in filler") {
        val result =
          RuleMetric(aRuleWithTwoPatternItemsInFiller, params,
            Examples(listOf(anExampleWithTwoPatternItemsInFiller))).metricResults
        result.negatives.size shouldEqual 0
      }

      it("should find positive matches in example with two constraints in filler") {
        val result =
          RuleMetric(aRuleWithTwoConstraintsInFiller, params,
            Examples(listOf(anExampleWithTwoConstraintsInFiller))).metricResults
        result.positives shouldEqual listOf(
          wordSlotFiller("go", "lang")
        )
      }

      it("should find no negative matches in example with two constraints in filler") {
        val result =
          RuleMetric(aRuleWithTwoConstraintsInFiller, params,
            Examples(listOf(anExampleWithTwoConstraintsInFiller))).metricResults
        result.negatives.size shouldEqual 0
      }

      it("should find negative matches in example with negative matches") {
        val result =
          RuleMetric(anyRuleWithNegativeMatches, params,
            Examples(listOf(anyExampleWithNegativeMatches))).metricResults
        result.negatives.size shouldEqual 1
        result.negatives shouldEqual listOf(wordSlotFiller("rust"))
      }
    }


    /**
     * Maxima code:
     *
     * log2(x) := log(x) / log(2);
     * f(p,n,ruleSize) := -1.442695*log2((p+1)/(p+n+2)) + ruleSize/p;
     */

    describe("metricResults") {

      it ("should use the correct formula 1") {

        val anyPvalue = 2
        val anyNvalue = 3
        val anyRuleSize = 0.4
        val kMinCov = 0
        val expected = 1.963539434299987
        val actual = metricResults(p = anyPvalue, n = anyNvalue, ruleSize = anyRuleSize,
          kMinCov = kMinCov)

        assert(Math.abs(expected - actual) < 0.00000000000001)
      }


      it ("should use the correct formula 2") {

        val anyPvalue = 10
        val anyNvalue = 5
        val anyRuleSize = 0.8
        val kMinCov = 0
        val expected = 0.9860575047077227
        val actual = metricResults(p = anyPvalue, n = anyNvalue, ruleSize = anyRuleSize,
          kMinCov = kMinCov)

        assert(Math.abs(expected - actual) < 0.00000000000001)
      }


      it ("rules that don't cover enough positive matches should evaluate to infinity") {

        val pValue = 2
        val anyNvalue = 3
        val anyRuleSize = 0.9860575047077227
        val kMinCov = 3
        val actual = metricResults(pValue, anyNvalue, anyRuleSize, kMinCov)

        assertEquals(Double.POSITIVE_INFINITY, actual)
      }


      it("rules that cover more positive matches should evaluate to lower values") {

        val anyRuleSize = 0.1
        val anyMinCov = 1
        val anyNvalue = 5

        val rule = metricResults(p = 5, n = anyNvalue, ruleSize = anyRuleSize, kMinCov = anyMinCov)
        val betterRule = metricResults(p = 6, n = anyNvalue, ruleSize = anyRuleSize, kMinCov = anyMinCov)

        assert(betterRule < rule)
      }

      it("rules that cover more negative matches should evaluate to higher values") {

        val anyRuleSize = 0.1
        val anyMinCov = 1
        val anyPvalue = 5

        val rule = metricResults(p = anyPvalue, n = 5, ruleSize = anyRuleSize, kMinCov = anyMinCov)
        val worseRule = metricResults(p = anyPvalue, n = 6, ruleSize = anyRuleSize, kMinCov = anyMinCov)

        assert(worseRule > rule)
      }
    }


    describe("ComparableRule") {

      val example = Example(
        BlankTemplate(name = "test", slots = slotNames("slot")),
        document = Document(tokens = textTokenList("a b c d e f g h i j")),
        filledTemplate = FilledTemplate(
          slots = slots(Slot(
            name = SlotName("slot"),
            slotFillers = hashSetOf(
              wordSlotFiller("a"),
              wordSlotFiller("b"),
              wordSlotFiller("c")
            )
          ))
        )
      )

      it("should evaluate a rule with more positive matches as smaller") {
        val rule1 = DerivedRule(
          preFiller = Pattern(),
          filler = Pattern(patternItemOfWords("a", "b", "c")),
          postFiller = Pattern(),
          baseRule1 = emptyRule,
          baseRule2 = emptyRule,
          slotName = SlotName("slot")
        )
        val rule2 = DerivedRule(
          preFiller = Pattern(),
          filler = Pattern(patternItemOfWords("a")),
          postFiller = Pattern(),
          baseRule1 = emptyRule,
          baseRule2 = emptyRule,
          slotName = SlotName("slot")
        )
        val a1 = ComparableRule(Examples(listOf(example)), params, rule1)
        val a2= ComparableRule(Examples(listOf(example)), params, rule2)
        (a1 < a2) shouldEqual true
      }

      it("should only count filler matches") {
        val rule1 = DerivedRule(
          preFiller = Pattern(),
          filler = Pattern(patternItemOfWords("a", "b", "c")),
          postFiller = Pattern(),
          baseRule1 = emptyRule,
          baseRule2 = emptyRule,
          slotName = SlotName("slot")
        )
        val rule2 = DerivedRule(
          preFiller = Pattern(patternItemOfWords("a", "b", "c", "d")),
          filler = Pattern(),
          postFiller = Pattern(),
          baseRule1 = emptyRule,
          baseRule2 = emptyRule,
          slotName = SlotName("slot")
        )
        val a1 = ComparableRule(Examples(listOf(example)), params, rule1)
        val a2= ComparableRule(Examples(listOf(example)), params, rule2)
        (a1 < a2) shouldEqual true
      }
    }
  }
})
