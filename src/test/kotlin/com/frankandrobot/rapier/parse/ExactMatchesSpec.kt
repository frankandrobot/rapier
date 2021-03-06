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

package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.*
import com.frankandrobot.rapier.meta.Slot
import com.frankandrobot.rapier.meta.SlotFiller
import com.frankandrobot.rapier.meta.SlotName
import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.rule.BaseRule
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.funktionale.option.Option.None
import org.funktionale.option.Option.Some
import org.jetbrains.spek.api.Spek
import java.util.*


class ExactMatchesSpec : Spek({

  val anyText = { textTokenIterator("A a b C D e f Z") }
  val anySlot = { Slot(SlotName("any slot"), slotFillers = HashSet<SlotFiller>()) }

  describe("match") {

    describe("pattern items") {

      val patternItemRule = { BaseRule(
        preFiller = patternOfWordItems("a", "b"),
        filler = patternOfWordItems("C", "D"),
        postFiller = patternOfWordItems("e", "f"),
        slotName = anySlot().name
      ) }


      it("should match a simple rule") {
        val result = patternItemRule().exactMatch(anyText())
        result.size shouldEqual 1
        result shouldContain MatchResult(
          preFillerMatch = MatchInfo(
            index = Some(1),
            matches = tokens("a", "b")
          ),
          fillerMatch = MatchInfo(
            index = Some(3),
            matches = tokens("C", "D")
          ),
          postFillerMatch = MatchInfo(
            index = Some(5),
            matches = tokens("e", "f")
          )
        )
      }

      it("should repeatedly match a simple rule") {
        val text = textTokenIterator(
          "start a b C D e f x x x a b C D e f end"
        )
        val result = patternItemRule().exactMatch(text)
        result.size shouldEqual 2
        result shouldContain MatchResult(
          preFillerMatch = MatchInfo(
            index = Some(1),
            matches = tokens("a", "b")
          ),
          fillerMatch = MatchInfo(
            index = Some(3),
            matches = tokens("C", "D")
          ),
          postFillerMatch = MatchInfo(
            index = Some(5),
            matches = tokens("e", "f")
          )
        )
        result shouldContain MatchResult(
          preFillerMatch = MatchInfo(
            index = Some(10),
            matches = tokens("a", "b")
          ),
          fillerMatch = MatchInfo(
            index = Some(12),
            matches = tokens("C", "D")
          ),
          postFillerMatch = MatchInfo(
            index = Some(14),
            matches = tokens("e", "f")
          )
        )
      }

      it("should NOT find matches when prefiller doesn't match") {
        val text = textTokenIterator("x C D e f")
        val result = patternItemRule().exactMatch(text)
        result.size shouldEqual 0
      }

      it("should NOT find matches when filler doesn't match") {
        val text = textTokenIterator("a b X e f")
        val result = patternItemRule().exactMatch(text)
        result.size shouldEqual 0
      }

      it("should NOT find matches when postfiller doesn't match") {
        val text = textTokenIterator("a b C D x")
        val result = patternItemRule().exactMatch(text)
        result.size shouldEqual 0
      }

      it("should find a match when preFiller patterns have more than one constraint") {
        val rule = BaseRule(
          preFiller = Pattern(patternElement = patternItemOfWords("a", "b")),
          filler = patternOfWordItems("C", "D"),
          postFiller = patternOfWordItems("e", "f"),
          slotName = anySlot().name
        )
        val result = rule.exactMatch(textTokenIterator("a C D e f xxxxx b C D e f"))
        result.size shouldEqual 2
        result shouldContain MatchResult(
          preFillerMatch = MatchInfo(
            index = Some(0),
            matches = tokens("a")
          ),
          fillerMatch = MatchInfo(
            index = Some(1),
            matches = tokens("C", "D")
          ),
          postFillerMatch = MatchInfo(
            index = Some(3),
            matches = tokens("e", "f")
          )
        )
        result shouldContain MatchResult(
          preFillerMatch = MatchInfo(
            index = Some(6),
            matches = tokens("b")
          ),
          fillerMatch = MatchInfo(
            index = Some(7),
            matches = tokens("C", "D")
          ),
          postFillerMatch = MatchInfo(
            index = Some(9),
            matches = tokens("e", "f")
          )
        )
      }

      it("should find a match when postFiller patterns have more than one constraint") {
        val rule = BaseRule(
          preFiller = patternOfWordItems("a", "b"),
          filler = patternOfWordItems("C", "D"),
          postFiller = Pattern(patternElement = patternItemOfWords("e", "f")),
          slotName = anySlot().name
        )
        val result = rule.exactMatch(textTokenIterator("a b C D e xxxxx a b C D f"))
        result.size shouldEqual 2
        result.first() shouldEqual MatchResult(
          preFillerMatch = MatchInfo(
            index = Some(0),
            matches = tokens("a", "b")
          ),
          fillerMatch = MatchInfo(
            index = Some(2),
            matches = tokens("C", "D")
          ),
          postFillerMatch = MatchInfo(
            index = Some(4),
            matches = tokens("e")
          )
        )
        result.last() shouldEqual MatchResult(
          preFillerMatch = MatchInfo(
            index = Some(6),
            matches = tokens("a", "b")
          ),
          fillerMatch = MatchInfo(
            index = Some(8),
            matches = tokens("C", "D")
          ),
          postFillerMatch = MatchInfo(
            index = Some(10),
            matches = tokens("f")
          )
        )
      }
    }

    describe("pattern list") {

      /**
       * Expanded patterns:
       *
       * - a B c
       * - a B
       * - B c
       * - B
       */
      val patternListRule = { BaseRule(
        preFiller = patternOfWordsList(length = 1, word = "a"),
        filler = patternOfWordItems("B"),
        postFiller = patternOfWordsList(length = 1, word = "c"),
        slotName = anySlot().name
      )
      }
      val text = { textTokenIterator("a B c") }

      it("should match all expanded patterns") {
        val result = patternListRule().exactMatch(text())
        result.size shouldEqual 4
      }

      it("should match full expansion: a B c") {
        val result = patternListRule().exactMatch(text())
        result shouldContain MatchResult(
          preFillerMatch = MatchInfo(
            index = Some(0),
            matches = tokens("a")
          ),
          fillerMatch = MatchInfo(
            index = Some(1),
            matches = tokens("B")
          ),
          postFillerMatch = MatchInfo(
            index = Some(2),
            matches = tokens("c")
          )
        )
      }

      it("should match the empty expansion") {
        val result = patternListRule().exactMatch(text())
        result shouldContain MatchResult(
          preFillerMatch = MatchInfo(
            index = Some(1),
            matches = None
          ),
          fillerMatch = MatchInfo(
            index = Some(1),
            matches = tokens("B")
          ),
          postFillerMatch = MatchInfo(
            index = Some(2),
            matches = None
          )
        )
      }

      it("should match the expansions with no prefiller: B c and B") {
        val text = textTokenIterator("start B c end")
        val result = patternListRule().exactMatch(text)
        result.size shouldEqual 2
        result shouldContain MatchResult(
          preFillerMatch = MatchInfo(
            index = Some(1),
            matches = None
          ),
          fillerMatch = MatchInfo(
            index = Some(1),
            matches = tokens("B")
          ),
          postFillerMatch = MatchInfo(
            index = Some(2),
            matches = tokens("c")
          )
        )
        result shouldContain MatchResult(
          preFillerMatch = MatchInfo(
            index = Some(1),
            matches = None
          ),
          fillerMatch = MatchInfo(
            index = Some(1),
            matches = tokens("B")
          ),
          postFillerMatch = MatchInfo(
            index = Some(2),
            matches = None
          )
        )
      }

      it("should match the expansions with no postfiller: a B and B") {
        val text = textTokenIterator("start a B end")
        val result = patternListRule().exactMatch(text)
        result.size shouldEqual 2
        result shouldContain MatchResult(
          preFillerMatch = MatchInfo(
            index = Some(1),
            matches = tokens("a")
          ),
          fillerMatch = MatchInfo(
            index = Some(2),
            matches = tokens("B")
          ),
          postFillerMatch = MatchInfo(
            index = Some(3),
            matches = None
          )
        )
        result shouldContain MatchResult(
          preFillerMatch = MatchInfo(
            index = Some(2),
            matches = None
          ),
          fillerMatch = MatchInfo(
            index = Some(2),
            matches = tokens("B")
          ),
          postFillerMatch = MatchInfo(
            index = Some(3),
            matches = None
          )
        )
      }

      it("should match the expansion with no prefiller/postfiller: B") {
        val text = textTokenIterator("start B end")
        val result = patternListRule().exactMatch(text)
        result.size shouldEqual 1
        result shouldContain MatchResult(
          preFillerMatch = MatchInfo(
            index = Some(1),
            matches = None
          ),
          fillerMatch = MatchInfo(
            index = Some(1),
            matches = tokens("B")
          ),
          postFillerMatch = MatchInfo(
            index = Some(2),
            matches = None
          )
        )
      }

      it("should NOT match when filler is missing") {
        val text = textTokenIterator("start end")
        val result = patternListRule().exactMatch(text)
        result.size shouldEqual 0
      }

      it("should NOT match when filler is missing") {
        val text = textTokenIterator("start end")
        val patternListRule = BaseRule(
          preFiller = patternOfWordsList(length = 1, word = "a"),
          filler = patternOfWordsList(length = 1, word = "B"),
          postFiller = patternOfWordsList(length = 1, word = "c"),
          slotName = anySlot().name
        )

        val result = patternListRule.exactMatch(text)
        result.size shouldEqual 0
      }
    }

    describe("Example 1") {
      it ("should find matches in a rule with two fillers") {
        val text = textTokenList("A java Z xxxxxxx A c# Z")
        val slot = Slot(
          name = SlotName("language"),
          slotFillers = hashSetOf(
            wordSlotFiller("java"),
            wordSlotFiller("c#"),
            wordSlotFiller("go", "lang")
          )
        )
        val ruleMatchingTwoFillers = BaseRule(
          preFiller = patternOfWordItems("A"),
          filler = Pattern(patternItemOfWords("java", "c#")),
          postFiller = patternOfWordItems("Z"),
          slotName = slot.name
        )
        val result = ruleMatchingTwoFillers.exactMatch(text)
        result.size shouldEqual 2
        result shouldContain MatchResult(
          preFillerMatch = MatchInfo(
            index = Some(0),
            matches = tokens("A")
          ),
          fillerMatch = MatchInfo(
            index = Some(1),
            matches = tokens("java")
          ),
          postFillerMatch = MatchInfo(
            index = Some(2),
            matches = Some(tokens("Z"))
          )
        )
        result shouldContain MatchResult(
          preFillerMatch = MatchInfo(
            index = Some(4),
            matches = tokens("A")
          ),
          fillerMatch = MatchInfo(
            index = Some(5),
            matches = tokens("c#")
          ),
          postFillerMatch = MatchInfo(
            index = Some(6),
            matches = Some(tokens("Z"))
          )
        )
      }
    }
  }
})

