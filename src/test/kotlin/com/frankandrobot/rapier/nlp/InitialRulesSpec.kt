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
import com.frankandrobot.rapier.meta.RapierParams
import com.frankandrobot.rapier.pattern.*
import com.frankandrobot.rapier.rule.BaseRule
import com.frankandrobot.rapier.rule.DerivedRule
import com.frankandrobot.rapier.rule.IDerivedRule
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek


class InitialRulesSpec : Spek({
  describe("initialRules") {
    val params = RapierParams()
    val rule1 = {
      BaseRule(
        preFiller = Pattern(
          PatternItem(words("located"), tags("vbn")),
          PatternItem(words("in"), tags("in"))
        ),
        filler = Pattern(
          PatternItem(words("atlanta"), tags("nnp"))
        ),
        postFiller = Pattern(),
        slotName = dummySlotName("any")
      )
    }
    val rule2 = {
      BaseRule(
        preFiller = Pattern(
          PatternItem(words("offices"), tags("nns")),
          PatternItem(words("in"), tags("in"))
        ),
        filler = Pattern(
          PatternItem(words("kansas"), tags("nnp")),
          PatternItem(words("city"), tags("nnp"))
        ),
        postFiller = Pattern(),
        slotName = dummySlotName("any")
      )
    }
    var result = emptyList<IDerivedRule>()

    beforeEach {
      result = initialRules(listOf(Pair(rule1(),rule2())),params = params)
    }


    it("should create rules with empty prefillers") {
      result.forEach { rule -> rule.preFiller shouldEqual Pattern() }
    }

    it("should create rules with empty postfillers") {
      result.forEach { rule -> rule.postFiller shouldEqual Pattern() }
    }

    it("should generalize the fillers") {
      result shouldContain DerivedRule(
        preFiller = Pattern(),
        filler = Pattern(
          PatternList(words("atlanta", "kansas", "city"), tags("nnp"), length = 2)
        ),
        postFiller = Pattern(),
        slotName = dummySlotName("any"),
        baseRule1 = rule1(),
        baseRule2 = rule2()
      )
      result shouldContain DerivedRule(
        preFiller = Pattern(),
        filler = Pattern(
          PatternList(posTagConstraints = tags("nnp"), length = 2)
        ),
        postFiller = Pattern(),
        slotName = dummySlotName("any"),
        baseRule1 = rule1(),
        baseRule2 = rule2()
      )
      result.size shouldEqual 2
    }
  }
})
