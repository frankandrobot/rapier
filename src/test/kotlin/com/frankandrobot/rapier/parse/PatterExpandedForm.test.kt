package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.PatternList
import com.frankandrobot.rapier.pattern.WordConstraint
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class PatternExpandedFormTest : Spek ({

  describe("ExpandedPattern") {

    val anyItem = { PatternItem("one") }
    val anyOtherItem = { PatternItem("two") }

    val anyList = { PatternList(WordConstraint("one")) }
    val anyOtherList = { PatternList(WordConstraint("two")) }

    val patternSingleItem = {Pattern(anyItem())}
    val patternMultiItem = {Pattern(anyItem(), anyOtherItem())}

    val patternSingleList = {Pattern(anyList())}
    val patternItemList = {Pattern(anyItem(), anyOtherList())}

    it("should expand a pattern with a single item into itself") {

      val result = PatternExpandedForm(patternSingleItem())

      assertEquals(1, result().size)
      assertEquals(PatternItemList(anyItem()), result[0])
    }

    it("should expand a pattern with multiple items into itself") {

      val result = PatternExpandedForm(patternMultiItem())

      assertEquals(1, result().size)
      assertEquals(PatternItemList(anyItem(), anyOtherItem()), result[0])
    }

    it("should expand a pattern with a list") {

      val result = PatternExpandedForm(patternSingleList())

      assertEquals(2, result().size)
      assertEquals(PatternItemList(), result[0])
      assertEquals(PatternItemList(anyItem()), result[1])
    }

    it("should expand a pattern with an item and a list") {

      val result = PatternExpandedForm(patternItemList())

      assertEquals(2, result().size)
      assertEquals(PatternItemList(anyItem()), result[0])
      assertEquals(PatternItemList(anyItem(), anyOtherItem()), result[1])

    }
  }

})
