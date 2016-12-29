package com.frankandrobot.rapier.rule

import com.frankandrobot.rapier.meta.SlotName
import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.patternOfWordItems
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek


class DerivedRuleWithEmptyPreAndPostFillersSpec : Spek({
  describe("derivedRuleWithEmptyPreAndPostFillers") {

    val baseRule1 = BaseRule(
      preFiller = patternOfWordItems("a"),
      filler = patternOfWordItems("b"),
      postFiller = patternOfWordItems("c"),
      slotName = SlotName("any")
    )
    val baseRule2 = BaseRule(
      preFiller = patternOfWordItems("1"),
      filler = patternOfWordItems("2"),
      postFiller = patternOfWordItems("3"),
      slotName = SlotName("any")
    )
    val pattern = patternOfWordItems("hello")
    val result = derivedRuleWithEmptyPreAndPostFillers(pattern, baseRule1, baseRule2)

    it("should create empty pre filler pattern") {
      result.preFiller shouldEqual Pattern()
    }

    it("should create empty post filler pattern") {
      result.postFiller shouldEqual Pattern()
    }

    it("should use pattern as filler") {
      result.filler shouldEqual patternOfWordItems("hello")
    }

    it("should set baseRule1") {
      result.baseRule1 shouldEqual baseRule1
    }

    it("should set baseRule2") {
      result.baseRule2 shouldEqual baseRule2
    }

    it("should disallow different slots in base rules") {
      val baseRule3 = BaseRule(
        preFiller = Pattern(),
        filler = Pattern(),
        postFiller = Pattern(),
        slotName = SlotName("another slot")
      )
      var failed = true
      try {
        derivedRuleWithEmptyPreAndPostFillers(pattern, baseRule1,baseRule3)
      }
      catch (e : AssertionError) {
        failed = false
      }
      failed shouldEqual false
    }
  }
})