package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.PatternList
import com.frankandrobot.rapier.pattern.WordConstraint
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class PatternExpandedFormTest : Spek ({

  describe("ExpandedPattern") {

    val anItem = {PatternItem(WordConstraint("one"))}
    val anotherItem = {PatternItem(WordConstraint("two"))}

    val aList = { PatternList(WordConstraint("one")) }
    val anotherList = { PatternList(WordConstraint("two")) }

    val patternSingleItem = {Pattern(anItem())}
    val patternMultiItem = {Pattern(anItem(), anotherItem())}

    val patternSingleList = {Pattern(aList())}
    val patternItemList = {Pattern(anItem(), anotherList())}

    it("should expand a pattern with a single item into itself") {

      val result = PatternExpandedForm(patternSingleItem())

      assertEquals(1, result().size)
      assertEquals(PatternItemList(anItem()), result[0])
    }

    it("should expand a pattern with multiple items into itself") {

      val result = PatternExpandedForm(patternMultiItem())

      assertEquals(1, result().size)
      assertEquals(PatternItemList(anItem(), anotherItem()), result[0])
    }

    it("should expand a pattern with a list") {

      val result = PatternExpandedForm(patternSingleList())

      assertEquals(2, result().size)
      assertEquals(PatternItemList(), result[0])
      assertEquals(PatternItemList(anItem()), result[1])
    }

    it("should expand a pattern with an item and a list") {

      val result = PatternExpandedForm(patternItemList())

      assertEquals(2, result().size)
      assertEquals(PatternItemList(anItem()), result[0])
      assertEquals(PatternItemList(anItem(), anotherItem()), result[1])

    }
  }

})
