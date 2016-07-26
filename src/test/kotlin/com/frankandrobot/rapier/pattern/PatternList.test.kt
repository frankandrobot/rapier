package com.frankandrobot.rapier.pattern

import com.frankandrobot.rapier.parse.PatternItemList
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class PatternListTest : Spek({

  describe("PatternList") {

    val item1 = {WordConstraint("one")}
    val item2 = {WordConstraint("two")}

    val list1 = {PatternList(item1())}
    val list2 = {PatternList(item1(), item2())}

    it("should expand a list of length 1 into 2 item lists") {

      val result = list1().expandedForm

      assertEquals(2, result.size)
      assertEquals(PatternItemList(), result[0])
      assertEquals(PatternItemList(PatternItem(item1())), result[1])
    }

    it("should expand a list of length 2 into into 3 item lists") {

      val result = list2().expandedForm

      assertEquals(3, result.size)
      assertEquals(PatternItemList(), result[0])
      assertEquals(PatternItemList(PatternItem(item1())), result[1])
      assertEquals(PatternItemList(PatternItem(item1()), PatternItem(item2())), result[2])
    }
  }
})
