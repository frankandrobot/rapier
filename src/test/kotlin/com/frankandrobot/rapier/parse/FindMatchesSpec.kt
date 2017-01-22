package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.meta.Document
import com.frankandrobot.rapier.meta.SlotName
import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.patternOfWordItems
import com.frankandrobot.rapier.rule.BaseRule
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotEqual
import org.jetbrains.spek.api.Spek


class FindMatchesSpec : Spek({

  val doc = Document(raw = "breaking news: president speaks")
  val rule = BaseRule(
    preFiller = Pattern(),
    filler = patternOfWordItems("breaking", "news"),
    postFiller = Pattern(),
    slotName = SlotName("test")
  )
  val noMatchRule = BaseRule(
    preFiller = Pattern(),
    filler = patternOfWordItems("not", "found"),
    postFiller = Pattern(),
    slotName = SlotName("test")
  )
  val rule3 = BaseRule(
    preFiller = Pattern(),
    filler = patternOfWordItems("president"),
    postFiller = Pattern(),
    slotName = SlotName("test")
  )

  describe("IRule#findMatches") {
    it("should work when there is a match") {
      val result = rule.findMatches(doc)
      result.first shouldEqual SlotName("test")
      result.second shouldEqual listOf("breaking news")
    }

    it("should work when there is no match") {
      val result = noMatchRule.findMatches(doc)
      result.first shouldEqual SlotName("test")
      result.second shouldEqual listOf()
    }
  }

  describe("list#findMatches") {
    it("should return a slot") {
      val result = listOf(rule, noMatchRule, rule3).findMatches(doc)
      result[SlotName("test")] shouldNotEqual null
      result[SlotName("another")] shouldEqual null
    }

    it("should return a slot with matches") {
      val result = listOf(rule, noMatchRule, rule3).findMatches(doc)
      result[SlotName("test")] shouldEqual listOf("breaking news", "president")
    }
  }
})
