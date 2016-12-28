package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.dummySlotName
import com.frankandrobot.rapier.pattern.*
import com.frankandrobot.rapier.rule.BaseRule
import com.frankandrobot.rapier.rule.DerivedRule
import com.frankandrobot.rapier.rule.IDerivedRule
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek


class InitialRulesSpec : Spek({
  describe("initialRules") {
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
      result = initialRules(listOf(Pair(rule1(),rule2())))
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
