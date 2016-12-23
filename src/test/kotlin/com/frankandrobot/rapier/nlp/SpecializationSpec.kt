package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.dummySlot
import com.frankandrobot.rapier.emptyBaseRule
import com.frankandrobot.rapier.meta.RapierParams
import com.frankandrobot.rapier.pattern.*
import com.frankandrobot.rapier.patternOfWordItems
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotEqual
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class SpecializationSpec : Spek({

  describe("specializeFiller") {

    val params = RapierParams()
    val tooShortBaseRule = { BaseRule(
      preFiller = patternOfWordItems("1"),
      filler = patternOfWordItems("filler"),
      postFiller = patternOfWordItems("end"),
      slot = dummySlot("any")
    ) }
    val baseRule = { BaseRule(
      preFiller = patternOfWordItems("1", "2", "3", "4", "5"),
      filler = patternOfWordItems("filler"),
      postFiller = patternOfWordItems("end"),
      slot = dummySlot("any")
    ) }


    describe("constraints") {

      it("should not generate rules if first base prefiller is too short") {
        val rule = RuleWithPositionInfo(
          baseRule1 = tooShortBaseRule(),
          baseRule2 = baseRule(),
          preFiller = Pattern(),
          filler = Pattern(),
          postFiller = Pattern(),
          slot = dummySlot("foo")
        )
        val result = specializePrefiller(rule, params, n1 = 2, n2 = 0)

        result shouldEqual emptyList()
      }

      it("should not generate rules if second base prefiller is too short") {
        val rule = RuleWithPositionInfo(
          baseRule1 = baseRule(),
          baseRule2 = tooShortBaseRule(),
          preFiller = Pattern(),
          filler = Pattern(),
          postFiller = Pattern(),
          slot = dummySlot("foo")
        )
        val result = specializePrefiller(rule, params, n1 = 0, n2 = 2)
        result shouldEqual emptyList()
      }

      it("should not generate rules if first base rule has used more than n1 pattern" +
        " items") {
        val rule = RuleWithPositionInfo(
          preFillerInfo = FillerIndexInfo(numUsed1 = 3, numUsed2 = 0),
          baseRule1 = baseRule(),
          baseRule2 = baseRule(),
          preFiller = Pattern(),
          filler = Pattern(),
          postFiller = Pattern(),
          slot = dummySlot("foo")
        )
        val result = specializePrefiller(rule, params, n1 = 3, n2 = 3)
        result shouldEqual emptyList()
      }

      it("should not generate rules if second base rule has used more than n2 pattern" +
        " items") {
        val rule = RuleWithPositionInfo(
          preFillerInfo = FillerIndexInfo(numUsed1 = 0, numUsed2 = 3),
          baseRule1 = baseRule(),
          baseRule2 = baseRule(),
          preFiller = Pattern(),
          filler = Pattern(),
          postFiller = Pattern(),
          slot = dummySlot("foo")
        )
        val result = specializePrefiller(rule, params, n1 = 3, n2 = 3)
        result shouldEqual emptyList()
      }

      it("should not generate rules if first base rule will generalize over too many " +
        "pattern items") {
        val params = RapierParams(k_MaxNoGainSearch = 4)
        val rule = RuleWithPositionInfo(
          preFillerInfo = FillerIndexInfo(numUsed1 = 0, numUsed2 = 0),
          baseRule1 = baseRule(),
          baseRule2 = baseRule(),
          preFiller = Pattern(),
          filler = Pattern(),
          postFiller = Pattern(),
          slot = dummySlot("foo")
        )
        val result = specializePrefiller(rule, params, n1 = 5, n2 = 1)
        result shouldEqual emptyList()
      }

      it("should not generate rules if second base rule will generalize over too many " +
        "pattern items") {
        val params = RapierParams(k_MaxNoGainSearch = 4)
        val rule = RuleWithPositionInfo(
          preFillerInfo = FillerIndexInfo(numUsed1 = 0, numUsed2 = 0),
          baseRule1 = baseRule(),
          baseRule2 = baseRule(),
          preFiller = Pattern(),
          filler = Pattern(),
          postFiller = Pattern(),
          slot = dummySlot("foo")
        )
        val result = specializePrefiller(rule, params, n1 = 1, n2 = 5)
        result shouldEqual emptyList()
      }

      it("should generate rules when constraints satisfied") {
        val params = RapierParams(k_MaxNoGainSearch = 4)
        val rule = RuleWithPositionInfo(
          preFillerInfo = FillerIndexInfo(numUsed1 = 1, numUsed2 = 0),
          baseRule1 = baseRule(),
          baseRule2 = baseRule(),
          preFiller = Pattern(),
          filler = Pattern(),
          postFiller = Pattern(),
          slot = dummySlot("foo")
        )
        val result = specializePrefiller(rule, params, n1 = 2, n2 = 4)
        result shouldNotEqual emptyList<RuleWithPositionInfo>()
      }
    }

    describe("example") {

      val params = RapierParams()

      var baseRule1 = emptyBaseRule()
      var baseRule2 = emptyBaseRule()
      var fillerGeneralizations : List<Pattern> = emptyList()
      var fillerRules : List<RuleWithPositionInfo> = emptyList()
      var iteration1 : List<RuleWithPositionInfo> = emptyList()
      var iteration2 : List<RuleWithPositionInfo> = emptyList()


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


