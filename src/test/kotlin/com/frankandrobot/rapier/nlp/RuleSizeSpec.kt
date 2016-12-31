/*
 *    Copyright 2016 Uriel Avalos
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.dummySlotName
import com.frankandrobot.rapier.pattern.*
import com.frankandrobot.rapier.rule.BaseRule
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class RuleSizeSpec : Spek({

  describe("RuleSize") {

    describe("#_unweightedRuleSize") {

      it("should equal 2 for unconstrained PatternItem") {
        val pattern = Pattern(PatternItem())
        assertEquals(2, _unweightedRuleSize(pattern))
      }

      it("should equal 3 for unconstrained PatternList") {
        val pattern = Pattern(PatternList(length = 1))
        assertEquals(3, _unweightedRuleSize(pattern))
      }

      it("should add 2 for each word constraint disjunct") {
        val pattern = Pattern(PatternItem(words("one", "two", "three")))
        assertEquals(2 + 2 * 2, _unweightedRuleSize(pattern))
      }

      it("should add 1 for each pos tag constraint disjunct") {
        val pattern = Pattern(PatternItem(posTagConstraints = hashSetOf(
          PosTagConstraint("one"), PosTagConstraint("two"), PosTagConstraint("three")
        )))
        assertEquals(2 + 1 * 2, _unweightedRuleSize(pattern))
      }

      it("should add 1 for each semantic constraint disjunct") {
        val pattern = Pattern(PatternItem(semanticConstraints = hashSetOf(
          SemanticConstraint("one"), SemanticConstraint("two"), SemanticConstraint("three")
        )))
        assertEquals(2 + 1 * 2, _unweightedRuleSize(pattern))
      }
    }


    describe("Pattern#ruleSize") {

      it("should scale the size by the weight") {
        val weight = 3.0
        val pattern = Pattern(PatternItem())
        val unScaled = _unweightedRuleSize(pattern)
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
