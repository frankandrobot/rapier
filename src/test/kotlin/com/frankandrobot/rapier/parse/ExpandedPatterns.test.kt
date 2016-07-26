package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.WordConstraint
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class ExpandedPatternTest : Spek ({

  describe("ExpandedPattern") {

    val item1 = {PatternItem(WordConstraint("one"))}
    val item2 = {PatternItem(WordConstraint("two"))}

    val patternSingleItem = {Pattern(item1())}
    val patternMultiItem = {Pattern(item2())}


    it("should expand a pattern with a single item into itself") {

      val result = ExpandedPatterns(patternSingleItem())

      assertEquals(1, result.patterns.size)
      assertEquals(PatternItemList(item1()), result.patterns[0])
    }

    it("should expand a pattern item list into itself") {

      val result = ExpandedPatterns(patternMultiItem())

      assertEquals(1, result.patterns.size)
      assertEquals(PatternItemList(item2()), result.patterns[0])
    }
  }

})
