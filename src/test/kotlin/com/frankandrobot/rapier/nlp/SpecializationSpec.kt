package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.dummySlot
import com.frankandrobot.rapier.emptyBaseRule
import com.frankandrobot.rapier.meta.RapierParams
import com.frankandrobot.rapier.pattern.*
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class SpecializationSpec : Spek({

  describe("specializaton") {

    describe("example") {

      val params = RapierParams()

      var baseRule1 = emptyBaseRule()
      var baseRule2 = emptyBaseRule()
      var fillerGeneralizations : List<Pattern> = emptyList()
      var fillerRules : List<com.frankandrobot.rapier.nlp.RuleWithPositionInfo> = emptyList()
      var iteration1 : List<com.frankandrobot.rapier.nlp.RuleWithPositionInfo> = emptyList()
      var iteration2 : List<com.frankandrobot.rapier.nlp.RuleWithPositionInfo> = emptyList()


      beforeEach {

        baseRule1 = BaseRule(
          preFiller = Pattern(
            PatternItem(words("located"), tags("vbn")),
            PatternItem(words("in"), tags("in"))
          ),
          filler = Pattern(
            PatternItem(words("atlanta"), tags("nnp"))
          ),
          postFiller = Pattern(
            PatternItem(words(","), tags(",")),
            PatternItem(words("georgia"), tags("nnp")),
            PatternItem(words("."), tags("."))
          ),
          slot = dummySlot("any")
        )

        baseRule2 = BaseRule(
          preFiller = Pattern(
            PatternItem(words("offices"), tags("nns")),
            PatternItem(words("in"), tags("in"))
          ),
          filler = Pattern(
            PatternItem(words("kansas"), tags("nnp")),
            PatternItem(words("city"), tags("nnp"))
          ),
          postFiller = Pattern(
            PatternItem(words(","), tags(",")),
            PatternItem(words("missouri"), tags("nnp")),
            PatternItem(words("."), tags("."))
          ),
          slot = dummySlot("any")
        )

        fillerGeneralizations = generalize(baseRule1.filler, baseRule2.filler)
        fillerRules = fillerGeneralizations
          .map { derivedRuleWithEmptyPreAndPostFillers(it, baseRule1, baseRule2) }
          .map(::RuleWithPositionInfo)

        iteration1 = fillerRules
          .flatMap { specializePrefiller(rule = it, n = 1, params = params) }
          .flatMap { specializePostFiller(rule = it, n = 1, params = params) }
        iteration2 = iteration1
          .filter { it.preFiller().any{ it is PatternItem } }
          .filter { it.postFiller().any{ it is PatternItem } }
          .flatMap { specializePrefiller(rule = it, n = 2, params = params)}
          .flatMap { specializePostFiller(rule = it, n = 2, params = params) }
      }


      describe("iteration1") {

        var result = emptyList<DerivedRule>()

        beforeEach { result = iteration1.map{ it() } }

        it("should contain rule 1") {

          assertEquals(true, result.any {
            it == DerivedRule(
              preFiller = Pattern(PatternItem(words("in"), tags("in"))),
              filler = Pattern(
                PatternList(words("atlanta", "kansas", "city"), tags("nnp"), length = 2)
              ),
              postFiller = Pattern(PatternItem(words(","), tags(","))),
              slot = dummySlot("any"),
              baseRule1 = baseRule1,
              baseRule2 = baseRule2
            )
          })
        }

        it("should contain rule 2") {

          assertEquals(true, result.any {
            it == DerivedRule(
              preFiller = Pattern(PatternItem(words("in"), tags("in"))),
              filler = Pattern(
                PatternList(posTagConstraints = tags("nnp"), length = 2)
              ),
              postFiller = Pattern(PatternItem(words(","), tags(","))),
              slot = dummySlot("any"),
              baseRule1 = baseRule1,
              baseRule2 = baseRule2
            )
          })
        }
      }

      describe("iteration2") {

        var result = emptyList<DerivedRule>()

        beforeEach { result = iteration2.map{ it() } }

        it("should contain prefiller 1") {
          val elem = PatternList(words("located"), tags("vbn"), length = 1)
          assertEquals(true, result.any{ it.preFiller().any{ it == elem }})
        }

        it("should contain prefiller 2") {
          val elem = PatternItem(words(), tags("vbn", "nns"))
          assertEquals(true, result.any{ it.preFiller().any{ it == elem }})
        }

        it("should contain prefiller 3") {
          val elem = PatternList(words("offices"), tags("nns"), length = 1)
          assertEquals(true, result.any{ it.preFiller().any{ it == elem }})
        }

        it("should contain prefiller 4") {
          val elem = PatternItem()
          assertEquals(true, result.any{ it.preFiller().any{ it == elem }})
        }

        it("should contain prefiller 5") {
          val elem = PatternItem(words("located", "offices"), tags("vbn", "nns"))
          assertEquals(true, result.any{ it.preFiller().any{ it == elem }})
        }

        it("should contain prefiller 6") {
          val elem = PatternItem(words("located", "offices"))
          assertEquals(true, result.any{ it.preFiller().any{ it == elem }})
        }
      }
    }
  }
})


