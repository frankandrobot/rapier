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
import com.frankandrobot.rapier.emptyBaseRule
import com.frankandrobot.rapier.meta.RapierParams
import com.frankandrobot.rapier.pattern.*
import com.frankandrobot.rapier.patternOfWordItems
import com.frankandrobot.rapier.rule.BaseRule
import com.frankandrobot.rapier.rule.DerivedRule
import com.frankandrobot.rapier.rule.FillerIndexInfo
import com.frankandrobot.rapier.rule.RuleWithPositionInfo
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotEqual
import org.funktionale.option.Option.None
import org.funktionale.option.Option.Some
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class SpecializationSpec : Spek({

  describe("subPattern") {

    val pattern = patternOfWordItems("0", "1", "2")

    it("should return None if fromIndex < 0") {
      val result = pattern.subPattern(fromIndex =-1, toIndex =1, maxElements = 10)
      result shouldEqual None
    }

    it("should return None if toIndex > pattern.length") {
      val result = pattern.subPattern(fromIndex =0, toIndex =4, maxElements = 10)
      result shouldEqual None
    }

    it("should get elements [fromIndex,toIndex-1]") {
      val result = pattern.subPattern(fromIndex = 1, toIndex = 3, maxElements = 10)
      result shouldEqual Some(patternOfWordItems("1","2"))
    }

    it("should return empty Pattern when from == to") {
      val result = pattern.subPattern(fromIndex = 2, toIndex = 2, maxElements = 10)
      result shouldEqual Some(Pattern())
    }

    it("should allow maxElements") {
      val result = pattern.subPattern(fromIndex = 1, toIndex = 3, maxElements = 2)
      result.get().length shouldEqual 2
    }

    it("should disallow more than maxElements") {
      val result = pattern.subPattern(fromIndex = 1, toIndex = 3, maxElements = 1)
      result shouldEqual None
    }

    it("should be able to return back entire pattern") {
      val result = pattern.subPattern(fromIndex = 0, toIndex = 3, maxElements =10)
      result shouldEqual Some(patternOfWordItems("0", "1", "2"))
    }
  }


  describe("specializeFiller") {

    describe("constraints") {

      val params = RapierParams()
      val tooShortBaseRule = { BaseRule(
        preFiller = patternOfWordItems("1"),
        filler = patternOfWordItems("filler"),
        postFiller = patternOfWordItems("end"),
        slotName = dummySlotName("any")
      ) }
      val baseRule = { BaseRule(
        preFiller = patternOfWordItems("1", "2", "3", "4", "5"),
        filler = patternOfWordItems("filler"),
        postFiller = patternOfWordItems("end"),
        slotName = dummySlotName("any")
      ) }

      it("should not generate rules if first base prefiller is too short") {
        val rule = RuleWithPositionInfo(
          baseRule1 = tooShortBaseRule(),
          baseRule2 = baseRule(),
          preFiller = Pattern(),
          filler = Pattern(),
          postFiller = Pattern(),
          slotName = dummySlotName("foo")
        )
        val result = specializePreFiller(rule, params, n1 = 2, n2 = 0)
        result shouldEqual emptyList()
      }

      it("should not generate rules if second base prefiller is too short") {
        val rule = RuleWithPositionInfo(
          baseRule1 = baseRule(),
          baseRule2 = tooShortBaseRule(),
          preFiller = Pattern(),
          filler = Pattern(),
          postFiller = Pattern(),
          slotName = dummySlotName("foo")
        )
        val result = specializePreFiller(rule, params, n1 = 0, n2 = 2)
        result shouldEqual emptyList()
      }

      it("should not generate rules if first base rule has used more than n1 pattern" +
        " items") {
        val rule = RuleWithPositionInfo(
          preFillerInfo = FillerIndexInfo(numUsed1 = 4, numUsed2 = 0),
          baseRule1 = baseRule(),
          baseRule2 = baseRule(),
          preFiller = Pattern(),
          filler = Pattern(),
          postFiller = Pattern(),
          slotName = dummySlotName("foo")
        )
        val result = specializePreFiller(rule, params, n1 = 3, n2 = 3)
        result shouldEqual emptyList()
      }

      it("should not generate rules if second base rule has used more than n2 pattern" +
        " items") {
        val rule = RuleWithPositionInfo(
          preFillerInfo = FillerIndexInfo(numUsed1 = 0, numUsed2 = 4),
          baseRule1 = baseRule(),
          baseRule2 = baseRule(),
          preFiller = Pattern(),
          filler = Pattern(),
          postFiller = Pattern(),
          slotName = dummySlotName("foo")
        )
        val result = specializePreFiller(rule, params, n1 = 3, n2 = 3)
        result shouldEqual emptyList()
      }

      it("should not generate rules if first base rule will generalize over too many " +
        "pattern items") {
        val params = RapierParams(maxElementsToSpecialize = 4)
        val rule = RuleWithPositionInfo(
          preFillerInfo = FillerIndexInfo(numUsed1 = 0, numUsed2 = 0),
          baseRule1 = baseRule(),
          baseRule2 = baseRule(),
          preFiller = Pattern(),
          filler = Pattern(),
          postFiller = Pattern(),
          slotName = dummySlotName("foo")
        )
        val result = specializePreFiller(rule, params, n1 = 5, n2 = 1)
        result shouldEqual emptyList()
      }

      it("should not generate rules if second base rule will generalize over too many " +
        "pattern items") {
        val params = RapierParams(maxElementsToSpecialize = 4)
        val rule = RuleWithPositionInfo(
          preFillerInfo = FillerIndexInfo(numUsed1 = 0, numUsed2 = 0),
          baseRule1 = baseRule(),
          baseRule2 = baseRule(),
          preFiller = Pattern(),
          filler = Pattern(),
          postFiller = Pattern(),
          slotName = dummySlotName("foo")
        )
        val result = specializePreFiller(rule, params, n1 = 1, n2 = 5)
        result shouldEqual emptyList()
      }

      it("should generate rules when constraints satisfied") {
        val params = RapierParams(maxElementsToSpecialize = 4)
        val rule = RuleWithPositionInfo(
          preFillerInfo = FillerIndexInfo(numUsed1 = 1, numUsed2 = 0),
          baseRule1 = baseRule(),
          baseRule2 = baseRule(),
          preFiller = Pattern(),
          filler = Pattern(),
          postFiller = Pattern(),
          slotName = dummySlotName("foo")
        )
        val result = specializePreFiller(rule, params, n1 = 2, n2 = 4)
        result shouldNotEqual emptyList<RuleWithPositionInfo>()
      }
    }

    describe("example") {

      val params = RapierParams(compressionPriorityQueueSize = 2)

      var baseRule1 = emptyBaseRule()
      var baseRule2 = emptyBaseRule()
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
          postFiller = Pattern(),
          slotName = dummySlotName("any")
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
          postFiller = Pattern(),
          slotName = dummySlotName("any")
        )

        fillerRules = initialRules(listOf(Pair(baseRule1, baseRule2)), params=params)
          .map(::RuleWithPositionInfo)
        iteration1 = fillerRules
          .flatMap { specializePreFiller(rule = it, n = 1, params = params) }
        iteration2 = iteration1
          .flatMap { specializePreFiller(rule = it, n = 2, params = params)}
      }


      describe("iteration1") {

        var result = emptyList<DerivedRule>()

        beforeEach { result = iteration1.map { it() } }

        it("should contain rule 1") {

          result shouldContain DerivedRule(
            preFiller = Pattern(
              PatternItem(words("in"), tags("in"))
            ),
            filler = Pattern(
              PatternList(words("atlanta", "kansas", "city"), tags("nnp"), length = 2)
            ),
            postFiller = Pattern(),
            slotName = dummySlotName("any"),
            baseRule1 = baseRule1,
            baseRule2 = baseRule2
          )
        }

        it("should contain rule 2") {

          result shouldContain DerivedRule(
            preFiller = Pattern(
              PatternItem(words("in"), tags("in"))
            ),
            filler = Pattern(
              PatternList(posTagConstraints = tags("nnp"), length = 2)
            ),
            postFiller = Pattern(),
            slotName = dummySlotName("any"),
            baseRule1 = baseRule1,
            baseRule2 = baseRule2
          )
        }
      }

      describe("iteration2") {

        var result = emptyList<DerivedRule>()

        beforeEach { result = iteration2.map{ it() } }

        it("should contain prefiller 1") {
          result shouldContain DerivedRule(
            preFiller = Pattern(
              PatternList(words("located"), tags("vbn"), length = 1),
              PatternItem(words("in"), tags("in"))
            ),
            filler = Pattern(
              PatternList(posTagConstraints = tags("nnp"), length = 2)
            ),
            postFiller = Pattern(),
            slotName = dummySlotName("any"),
            baseRule1 = baseRule1,
            baseRule2 = baseRule2
          )
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


  describe("specializePostFiller") {

    describe("constraints") {

      val params = RapierParams()
      val tooShortBaseRule = { BaseRule(
        preFiller = patternOfWordItems("start"),
        filler = patternOfWordItems("filler"),
        postFiller = patternOfWordItems("1"),
        slotName = dummySlotName("any")
      ) }
      val baseRule = { BaseRule(
        preFiller = patternOfWordItems("start"),
        filler = patternOfWordItems("filler"),
        postFiller = patternOfWordItems("0", "1", "2", "3", "4"),
        slotName = dummySlotName("any")
      ) }

      it("should not generate rules if first base postfiller is too short") {
        val rule = RuleWithPositionInfo(
          postFillerInfo = FillerIndexInfo(numUsed1 = 0, numUsed2 = 0),
          baseRule1 = tooShortBaseRule(),
          baseRule2 = baseRule(),
          preFiller = Pattern(),
          filler = Pattern(),
          postFiller = Pattern(),
          slotName = dummySlotName("foo")
        )
        val result = specializePostFiller(rule, params, n1 = 2, n2 = 0)
        result shouldEqual emptyList()
      }

      it("should not generate rules if second base postfiller is too short") {
        val rule = RuleWithPositionInfo(
          postFillerInfo = FillerIndexInfo(numUsed1 = 0, numUsed2 = 0),
          baseRule1 = baseRule(),
          baseRule2 = tooShortBaseRule(),
          preFiller = Pattern(),
          filler = Pattern(),
          postFiller = Pattern(),
          slotName = dummySlotName("foo")
        )
        val result = specializePostFiller(rule, params, n1 = 0, n2 = 2)
        result shouldEqual emptyList()
      }

      it("should not generate rules if first base rule has used more than n1 pattern" +
        " items") {
        val rule = RuleWithPositionInfo(
          postFillerInfo = FillerIndexInfo(numUsed1 = 4, numUsed2 = 0),
          baseRule1 = baseRule(),
          baseRule2 = baseRule(),
          preFiller = Pattern(),
          filler = Pattern(),
          postFiller = Pattern(),
          slotName = dummySlotName("foo")
        )
        val result = specializePostFiller(rule, params, n1 = 3, n2 = 3)
        result shouldEqual emptyList()
      }

      it("should not generate rules if second base rule has used more than n2 pattern" +
        " items") {
        val rule = RuleWithPositionInfo(
          postFillerInfo = FillerIndexInfo(numUsed1 = 0, numUsed2 = 4),
          baseRule1 = baseRule(),
          baseRule2 = baseRule(),
          preFiller = Pattern(),
          filler = Pattern(),
          postFiller = Pattern(),
          slotName = dummySlotName("foo")
        )
        val result = specializePostFiller(rule, params, n1 = 3, n2 = 3)
        result shouldEqual emptyList()
      }

      it("should not generate rules if first base rule will generalize over too many " +
        "pattern items") {
        val params = RapierParams(maxElementsToSpecialize = 4)
        val rule = RuleWithPositionInfo(
          postFillerInfo = FillerIndexInfo(numUsed1 = 0, numUsed2 = 0),
          baseRule1 = baseRule(),
          baseRule2 = baseRule(),
          preFiller = Pattern(),
          filler = Pattern(),
          postFiller = Pattern(),
          slotName = dummySlotName("foo")
        )
        val result = specializePostFiller(rule, params, n1 = 5, n2 = 1)
        result shouldEqual emptyList()
      }

      it("should not generate rules if second base rule will generalize over too many " +
        "pattern items") {
        val params = RapierParams(maxElementsToSpecialize = 4)
        val rule = RuleWithPositionInfo(
          postFillerInfo = FillerIndexInfo(numUsed1 = 0, numUsed2 = 0),
          baseRule1 = baseRule(),
          baseRule2 = baseRule(),
          preFiller = Pattern(),
          filler = Pattern(),
          postFiller = Pattern(),
          slotName = dummySlotName("foo")
        )
        val result = specializePostFiller(rule, params, n1 = 1, n2 = 5)
        result shouldEqual emptyList()
      }

      it("should generate rules when constraints satisfied") {
        val params = RapierParams(maxElementsToSpecialize = 4)
        val rule = RuleWithPositionInfo(
          postFillerInfo = FillerIndexInfo(numUsed1 = 1, numUsed2 = 0),
          baseRule1 = baseRule(),
          baseRule2 = baseRule(),
          preFiller = Pattern(),
          filler = Pattern(),
          postFiller = Pattern(),
          slotName = dummySlotName("foo")
        )
        val result = specializePostFiller(rule, params, n1 = 2, n2 = 4)
        result shouldNotEqual emptyList<RuleWithPositionInfo>()
      }
    }

    describe("example") {

      val params = RapierParams(compressionPriorityQueueSize = 2)

      var baseRule1 = emptyBaseRule()
      var baseRule2 = emptyBaseRule()
      var fillerRules : List<RuleWithPositionInfo> = emptyList()
      var iteration1 : List<RuleWithPositionInfo> = emptyList()
      var iteration2 : List<RuleWithPositionInfo> = emptyList()

      beforeEach {

        baseRule1 = BaseRule(
          preFiller = Pattern(),
          filler = Pattern(
            PatternItem(words("atlanta"), tags("nnp"))
          ),
          postFiller = Pattern(
            PatternItem(words(","), tags(",")),
            PatternItem(words("georgia"), tags("nnp")),
            PatternItem(words("."), tags("."))
          ),
          slotName = dummySlotName("any")
        )

        baseRule2 = BaseRule(
          preFiller = Pattern(),
          filler = Pattern(
            PatternItem(words("kansas"), tags("nnp")),
            PatternItem(words("city"), tags("nnp"))
          ),
          postFiller = Pattern(
            PatternItem(words(","), tags(",")),
            PatternItem(words("missouri"), tags("nnp")),
            PatternItem(words("."), tags("."))
          ),
          slotName = dummySlotName("any")
        )

        fillerRules = initialRules(listOf(Pair(baseRule1, baseRule2)), params=params)
          .map(::RuleWithPositionInfo)
        iteration1 = fillerRules
          .flatMap { specializePostFiller(rule = it, n = 1, params = params) }
        iteration2 = iteration1
          .filter{ rule -> rule.postFiller().all { it is PatternItem }}
          .flatMap { specializePostFiller(rule = it, n = 2, params = params)}
      }


      describe("iteration1") {

        var result = emptyList<DerivedRule>()

        beforeEach { result = iteration1.map{ it() } }

        it("should contain rule 1") {

          result shouldContain DerivedRule(
            preFiller = Pattern(),
            filler = Pattern(
              PatternList(words("atlanta", "kansas", "city"), tags("nnp"), length = 2)
            ),
            postFiller = Pattern(
              PatternItem(words(","), tags(","))
            ),
            slotName = dummySlotName("any"),
            baseRule1 = baseRule1,
            baseRule2 = baseRule2
          )
        }

        it("should contain rule 2") {

          result shouldContain DerivedRule(
            preFiller = Pattern(),
            filler = Pattern(
              PatternList(posTagConstraints = tags("nnp"), length = 2)
            ),
            postFiller = Pattern(
              PatternItem(words(","), tags(","))
            ),
            slotName = dummySlotName("any"),
            baseRule1 = baseRule1,
            baseRule2 = baseRule2
          )
        }
      }

      describe("iteration2") {

        var result = emptyList<DerivedRule>()

        beforeEach { result = iteration2.map{ it() } }

        it("should contain postfiller 1") {
          val rule = DerivedRule(
            preFiller = Pattern(),
            filler = Pattern(
              PatternList(posTagConstraints = tags("nnp"), length = 2)
            ),
            postFiller = Pattern(
              PatternItem(words(","), tags(",")),
              PatternList(words("georgia"), tags("nnp"), length = 1)
            ),
            slotName = dummySlotName("any"),
            baseRule1 = baseRule1,
            baseRule2 = baseRule2
          )
          result shouldContain rule
        }

        it("should contain postfiller 2") {
          val elem = PatternItem(words(), tags("nnp"))
          assertEquals(true, result.any{ it.postFiller().any{ it == elem }})
        }

        it("should contain postfiller 3") {
          val elem = PatternList(words("missouri"), tags("nnp"), length = 1)
          assertEquals(true, result.any{ it.postFiller().any{ it == elem }})
        }

        it("should contain postfiller 4") {
          val elem = PatternItem(words("georgia", "missouri"), tags("nnp"))
          assertEquals(true, result.any{ it.postFiller().any{ it == elem }})
        }
      }
    }
  }

  describe("example") {

    val params = RapierParams(compressionPriorityQueueSize = 2)

    var baseRule1 = emptyBaseRule()
    var baseRule2 = emptyBaseRule()
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
        slotName = dummySlotName("any")
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
        slotName = dummySlotName("any")
      )

      fillerRules = initialRules(listOf(Pair(baseRule1, baseRule2)), params=params)
        .map(::RuleWithPositionInfo)

      iteration1 = fillerRules
        .flatMap { specializePreFiller(rule = it, n = 1, params = params) }
        .flatMap { specializePostFiller(rule = it, n = 1, params = params) }
      iteration2 = iteration1
        .filter { it.preFiller().any{ it is PatternItem } }
        .filter { it.postFiller().any{ it is PatternItem } }
        .flatMap { specializePreFiller(rule = it, n = 2, params = params)}
        .flatMap { specializePostFiller(rule = it, n = 2, params = params) }
    }


    describe("iteration1") {

      var result = emptyList<DerivedRule>()

      beforeEach { result = iteration1.map{ it() } }

      it("should contain rule 1") {

        result shouldContain DerivedRule(
          preFiller = Pattern(
            PatternItem(words("in"), tags("in"))
          ),
          filler = Pattern(
            PatternList(words("atlanta", "kansas", "city"), tags("nnp"), length = 2)
          ),
          postFiller = Pattern(
            PatternItem(words(","), tags(","))
          ),
          slotName = dummySlotName("any"),
          baseRule1 = baseRule1,
          baseRule2 = baseRule2
        )
      }

      it("should contain rule 2") {

        assertEquals(true, result.any {
          it == DerivedRule(
            preFiller = Pattern(
              PatternItem(words("in"), tags("in"))
            ),
            filler = Pattern(
              PatternList(posTagConstraints = tags("nnp"), length = 2)
            ),
            postFiller = Pattern(
              PatternItem(words(","), tags(","))
            ),
            slotName = dummySlotName("any"),
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
})


