package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.dummySlotName
import com.frankandrobot.rapier.pattern.*
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class RuleSizeSpec : Spek({

  describe("RuleSize") {

    describe("#_ruleSize") {

      it("should equal 2 for unconstrained PatternItem") {
        val pattern = Pattern(PatternItem())
        assertEquals(2, _ruleSize(pattern))
      }

      it("should equal 3 for unconstrained PatternList") {
        val pattern = Pattern(PatternList(length = 1))
        assertEquals(3, _ruleSize(pattern))
      }

      it("should add 2 for each word constraint disjunct") {
        val pattern = Pattern(PatternItem(words("one", "two", "three")))
        assertEquals(2 + 2 * 2, _ruleSize(pattern))
      }

      it("should add 1 for each pos tag constraint disjunct") {
        val pattern = Pattern(PatternItem(posTagConstraints = hashSetOf(
          PosTagConstraint("one"), PosTagConstraint("two"), PosTagConstraint("three")
        )))
        assertEquals(2 + 1 * 2, _ruleSize(pattern))
      }

      it("should add 1 for each semantic constraint disjunct") {
        val pattern = Pattern(PatternItem(semanticConstraints = hashSetOf(
          SemanticConstraint("one"), SemanticConstraint("two"), SemanticConstraint("three")
        )))
        assertEquals(2 + 1 * 2, _ruleSize(pattern))
      }
    }


    describe("Pattern#ruleSize") {

      it("should scale the size by the weight") {
        val weight = 3.0
        val pattern = Pattern(PatternItem())
        val unScaled = _ruleSize(pattern)
        val scaled = pattern.ruleSize(weight)
        assertEquals(unScaled * weight, scaled)
      }
    }


    describe("IRule#ruleSize") {

      it("should return the sum of the scaled values of the *fillers") {
        val weight = 3.0
        val rule = BaseRule(
          preFiller = Pattern(PatternItem()),
          filler = Pattern(PatternList(length = 1)),
          postFiller = Pattern(PatternItem(words("one", "two", "three"))),
          slotName = dummySlotName("any")
        )
        val expected = weight * (2 + 3 + (2 + 2 * 2))

        assertEquals(expected, rule.ruleSize(weight))
      }
    }
  }
})
