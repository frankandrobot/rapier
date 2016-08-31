package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.pattern.*
import org.jetbrains.spek.api.Spek
import java.util.*
import kotlin.test.assertEquals


class RuleSizeSpec : Spek({

  describe("RuleSize") {

    it("should equal 2 for empty PatternItem") {

      val pattern = Pattern(PatternItem())

      assertEquals(2, _ruleSize(pattern))
    }

    it("should equal 3 for empty PatternList") {

      val pattern = Pattern(PatternList())

      assertEquals(3, _ruleSize(pattern))
    }

    it("should add 2 for each word constraint disjunct") {

      val pattern = Pattern(PatternItem("one", "two", "three"))

      assertEquals(2 + 2 * 2, _ruleSize(pattern))
    }

    it("should add 1 for each pos tag constraint disjunct") {

      val pattern = Pattern(PatternItem(posTagContraints = listOf(
        PosTagConstraint("one"), PosTagConstraint("two"), PosTagConstraint("three")
      )))

      assertEquals(2 + 1 * 2, _ruleSize(pattern))
    }

    it("should add 1 for each semantic constraint disjunct") {

      val pattern = Pattern(PatternItem(semanticConstraints = listOf(
        SemanticConstraint("one"), SemanticConstraint("two"), SemanticConstraint("three")
      )))

      assertEquals(2 + 1 * 2, _ruleSize(pattern))
    }
  }
})