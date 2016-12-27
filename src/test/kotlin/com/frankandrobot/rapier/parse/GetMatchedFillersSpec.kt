package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.*
import com.frankandrobot.rapier.meta.*
import com.frankandrobot.rapier.pattern.BaseRule
import com.frankandrobot.rapier.pattern.Pattern
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek


class GetMatchedFillersSpec : Spek ({

  describe("#getMatchedFillers") {

    var anySlot : Slot

    var aSimpleRule = emptyRule
    var aRuleWithTwoPatternItemsInFiller = emptyRule
    var aRuleWithTwoConstraintsInFiller = emptyRule
    var anyRuleWithNegativeMatches = emptyRule

    var aSimpleExample = emptyExample
    var anExampleWithTwoPatternItemsInFiller = emptyExample
    var anExampleWithTwoConstraintsInFiller = emptyExample
    var anyExampleWithNegativeMatches = emptyExample


    beforeEach {

      anySlot = Slot(
        name = SlotName("language"),
        slotFillers = hashSetOf(
          wordSlotFiller("java"),
          wordSlotFiller("c#"),
          wordSlotFiller("go", "lang")
        )
      )

      aSimpleRule = BaseRule(
        preFiller = patternOfWordItems("A"),
        filler = patternOfWordItems("java"),
        postFiller = patternOfWordItems("Z"),
        slot = anySlot
      )
      aSimpleExample = Example(
        BlankTemplate(name = "test", slots = slotNames("language")),
        Document(tokens = textTokenList("A java Z")),
        FilledTemplate(
          slots = slots(
            Slot(
              name = SlotName("language"),
              slotFillers = hashSetOf(
                wordSlotFiller("java")
              )
            )
          )
        )
      )

      aRuleWithTwoPatternItemsInFiller = BaseRule(
        preFiller = patternOfWordItems("A"),
        filler = Pattern(patternItemOfWords("java", "c#")),
        postFiller = patternOfWordItems("Z"),
        slot = anySlot
      )
      anExampleWithTwoPatternItemsInFiller = Example(
        BlankTemplate(name = "test", slots = slotNames("language")),
        Document(tokens = textTokenList("A java Z xxxxxxx A c# Z")),
        FilledTemplate(
          slots = slots(
            Slot(
              name = SlotName("language"),
              slotFillers = hashSetOf(
                wordSlotFiller("java"),
                wordSlotFiller("c#")
              )
            )
          )
        )
      )

      aRuleWithTwoConstraintsInFiller = BaseRule(
        preFiller = patternOfWordItems("A"),
        filler = patternOfWordItems("go", "lang"),
        postFiller = patternOfWordItems("Z"),
        slot = anySlot
      )
      anExampleWithTwoConstraintsInFiller = Example(
        BlankTemplate(name = "test", slots = slotNames("language")),
        Document(tokens = textTokenList("A go lang Z")),
        FilledTemplate(
          slots = slots(
            Slot(
              name = SlotName("language"),
              slotFillers = hashSetOf(
                wordSlotFiller("go", "lang")
              )
            )
          )
        )
      )

      anyRuleWithNegativeMatches = BaseRule(
        preFiller = patternOfWordItems("A"),
        filler = Pattern(patternItemOfWords("ruby", "rust")),
        postFiller = patternOfWordItems("Z"),
        slot = anySlot
      )
      anyExampleWithNegativeMatches = Example(
        BlankTemplate(name = "test", slots = slotNames("language")),
        Document(tokens = textTokenList("A ruby Z xxxxx A rust Z")),
        FilledTemplate(
          slots = slots(
            Slot(
              name = SlotName("language"),
              slotFillers = hashSetOf(
                wordSlotFiller("ruby")
              )
            )
          )
        )
      )
    }


    describe("RuleMetric") {

      it("should find positive matches in simple rules") {
        val result = getMatchedFillers(aSimpleRule, aSimpleExample)
        result.positives.size shouldEqual 1
        result.positives shouldContain wordSlotFiller("java")
      }

      it("should find no negative matches in simple rules") {
        val result = getMatchedFillers(aSimpleRule, aSimpleExample)
        result.negatives.size shouldEqual 0
      }

      it("should find two positive matches in example with two pattern items in filler") {
        val result = getMatchedFillers(
          aRuleWithTwoPatternItemsInFiller,
          anExampleWithTwoPatternItemsInFiller
        )
        result.positives shouldEqual listOf(
          wordSlotFiller("java"),
          wordSlotFiller("c#")
        )
      }

      it("should find no negative matches in example with two pattern items in filler") {
        val result = getMatchedFillers(
          aRuleWithTwoPatternItemsInFiller,
          anExampleWithTwoPatternItemsInFiller
        )
        result.negatives.size shouldEqual 0
      }

      it("should find positive matches in example with two constraints in filler") {
        val result = getMatchedFillers(
          aRuleWithTwoConstraintsInFiller,
          anExampleWithTwoConstraintsInFiller
        )
        result.positives shouldEqual listOf(
          wordSlotFiller("go", "lang")
        )
      }

      it("should find no negative matches in example with two constraints in filler") {
        val result = getMatchedFillers(
          aRuleWithTwoConstraintsInFiller,
          anExampleWithTwoConstraintsInFiller
        )
        result.negatives.size shouldEqual 0
      }

      it("should find negative matches in example with negative matches") {
        val result = getMatchedFillers(
          anyRuleWithNegativeMatches,
          anyExampleWithNegativeMatches
        )
        result.negatives.size shouldEqual 1
        result.negatives shouldEqual listOf(wordSlotFiller("rust"))
      }
    }
  }


  describe("IRule#getMatchedFillers") {

    val slot = Slot(
      name = SlotName("language"),
      slotFillers = hashSetOf(
        wordSlotFiller("java"),
        wordSlotFiller("c#"),
        wordSlotFiller("go", "lang")
      )
    )
    val rule = BaseRule(
      preFiller = patternOfWordItems("A"),
      filler = Pattern(patternItemOfWords("java","c#","go")),
      postFiller = patternOfWordItems("Z"),
      slot = slot
    )
    val example1 = Example(
      BlankTemplate(name = "test", slots = slotNames("language")),
      Document(tokens = textTokenList("A java Z")),
      FilledTemplate(
        slots = slots(
          Slot(
            name = SlotName("language"),
            slotFillers = hashSetOf(
              wordSlotFiller("java")
            )
          )
        )
      )
    )
    val example2 = Example(
      BlankTemplate(name = "test", slots = slotNames("language")),
      Document(tokens = textTokenList("A java Z   A c# Z   A go Z")),
      FilledTemplate(
        slots = slots(
          Slot(
            name = SlotName("language"),
            slotFillers = hashSetOf(
              wordSlotFiller("java"),
              wordSlotFiller("c#")
            )
          )
        )
      )
    )
    val result = rule.getMatchedFillers(Examples(listOf(example1,example2)))

    it("should get total positive matches") {
      result.positives shouldEqual listOf(
        wordSlotFiller("java"),
        wordSlotFiller("java"),
        wordSlotFiller("c#")
      )
    }

    it("should get total negative matches") {
      result.negatives shouldEqual listOf(wordSlotFiller("go"))
    }

  }
})
