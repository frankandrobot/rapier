package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.WordConstraint
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class PatternExpandedFormTest : Spek ({

  describe("ExpandedPattern") {

    val item1 = {PatternItem(WordConstraint("one"))}
    val item2 = {PatternItem(WordConstraint("two"))}

    val patternSingleItem = {Pattern(item1())}
    val patternMultiItem = {Pattern(item1(), item2())}


    it("should expand a pattern with a single item into itself") {

      val result = PatternExpandedForm(patternSingleItem())

      assertEquals(1, result().size)
      assertEquals(PatternItemList(item1()), result[0])
    }

    it("should expand a pattern with multiple items into itself") {

      val result = PatternExpandedForm(patternMultiItem())

      assertEquals(1, result().size)
      assertEquals(PatternItemList(item1(), item2()), result[0])
    }

    it("should expand a pattern with a list") {


    }
  }

})
