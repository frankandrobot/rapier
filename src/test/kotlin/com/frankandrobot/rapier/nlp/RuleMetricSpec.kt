package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.*
import com.frankandrobot.rapier.meta.*
import com.frankandrobot.rapier.pattern.BaseRule
import com.frankandrobot.rapier.pattern.Pattern
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek


class RuleMetricSpec : Spek({

  var anyFiller  = wordSlotFiller("none")
  var anyOtherFiller = wordSlotFiller("none")
  var yetAnotherFiller = wordSlotFiller("none")
  var anySlot : Slot

  var anySimpleRule = BaseRule(slot = dummySlot("none"))
  var anyRuleMatchingTwoFillers = BaseRule(slot = dummySlot("none"))

  var anyDocument = Document(tokens = wordTokens("none"))
  var anyOtherDocument = Document(tokens = wordTokens("none"))
  var yetAnotherDocument = Document(tokens = wordTokens("none"))

  var anySimpleExample = emptyExample
  var anyExampleWithTwoMatches = emptyExample
  var yetAnotherExample = emptyExample

  val anyMinCov = 1
  val anyRuleSize = 1.0
  val params = RapierParams(k_MinCov = anyMinCov, k_SizeWeight = anyRuleSize)


  beforeEach {

    anyFiller = wordSlotFiller("java")
    anyOtherFiller = wordSlotFiller("c#")
    yetAnotherFiller = wordSlotFiller("go lang")
    anySlot = Slot(
      name = SlotName("language"),
      slotFillers = hashSetOf(
        wordSlotFiller("java"),
        wordSlotFiller("c#"),
        wordSlotFiller("go", "lang")
      )
    )

    anySimpleRule = BaseRule(
      preFiller = patternOfWordItems("A"),
      filler = patternOfWordItems("java"),
      postFiller = patternOfWordItems("Z"),
      slot = anySlot
    )

    anyRuleMatchingTwoFillers = BaseRule(
      preFiller = patternOfWordItems("A"),
      filler = Pattern(patternItemOfWords("java", "c#")),
      postFiller = patternOfWordItems("Z"),
      slot = anySlot
    )

    anyDocument = Document(tokens = wordTokens("A", "java", "Z"))
    anyOtherDocument = Document(tokens = wordTokens("A", "c#", "Z"))
    yetAnotherDocument = Document(
      tokens = textTokenList("A java Z xxxxxxx A c# Z")
    )

    anySimpleExample = Example(
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
    anyExampleWithTwoMatches = Example(
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
    yetAnotherExample = Example(
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
  }


  describe("RuleMetric") {

    describe("_evaluate") {

      it("should find positive matches in simple rules") {
        val result =
          RuleMetric(anySimpleRule, params)._evaluate(Examples(listOf(anySimpleExample)))
        result.positives.size shouldEqual 1
        result.positives shouldContain wordSlotFiller("java")
      }

      it("should find no negative matches in simple rules") {
        val result =
          RuleMetric(anySimpleRule, params)._evaluate(Examples(listOf(anySimpleExample)))
        result.negatives.size shouldEqual 0
      }

      it("should find two positive matches in example with two matches") {
        val result =
          RuleMetric(anyRuleMatchingTwoFillers, params)
            ._evaluate(Examples(listOf(anyExampleWithTwoMatches)))
        result.positives shouldEqual listOf(
          wordSlotFiller("java"),
          wordSlotFiller("c#")
        )
      }

      it("should find no negative matches in example with two matches") {
        val result =
          RuleMetric(anyRuleMatchingTwoFillers, params)
            ._evaluate(Examples(listOf(anyExampleWithTwoMatches)))
        result.negatives.size shouldEqual 0
      }
    }


    /**
     * Maxima code:
     *
     * log2(x) := log(x) / log(2);
     * f(p,n,ruleSize) := -1.442695*log2((p+1)/(p+n+2)) + ruleSize/p;
     */

/*    describe("metric") {

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
    }*/
  }
})

//fun toTokens(vararg documents: Document) = documents.map{ it.raw.split(" ").map{Token
//  (it)} }
