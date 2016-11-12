package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.pattern.*
import com.frankandrobot.rapier.template.Slot
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class SpecializationSpec : Spek({

  describe("specializaton") {

    describe("example") {

      var baseRule1 = emptyBaseRule
      var baseRule2 = emptyBaseRule
      var fillerGeneralizations: List<Pattern> = emptyList()
      var fillerRules: List<DerivedRule> = emptyList()
      var result : List<DerivedRule> = emptyList()

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
          slot = Slot("any")
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
          slot = Slot("any")
        )

        fillerGeneralizations = generalize(baseRule1.filler, baseRule2.filler)
        fillerRules = fillerGeneralizations.map { initialRule(it, baseRule1, baseRule2) }

        result = fillerRules.flatMap {
          specializePrefiller(RuleWithPositionInfo(it), n = 1)
            .flatMap { specializePostFiller(it, n = 1) }
            .map{ it() }
        }
      }

      it("should contain rule 1") {

        assertEquals(true, result.any {
          it == DerivedRule(
            preFiller = Pattern(PatternItem(words("in"), tags("in"))),
            filler = Pattern(
              PatternList(words("atlanta", "kansas", "city"), tags("nnp"), length = 2)
            ),
            postFiller = Pattern(PatternItem(words(","), tags(","))),
            slot = Slot("any"),
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
            slot = Slot("any"),
            baseRule1 = baseRule1,
            baseRule2 = baseRule2
          )
        })
      }
    }
  }
})

val emptyBaseRule = BaseRule(
  preFiller = Pattern(),
  filler = Pattern(),
  postFiller = Pattern(),
  slot = Slot("")
)

