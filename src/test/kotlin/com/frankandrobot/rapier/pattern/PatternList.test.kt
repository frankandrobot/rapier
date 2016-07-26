package com.frankandrobot.rapier.pattern

import com.frankandrobot.rapier.parse.PatternItemList
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class PatternListTest : Spek({

  describe("PatternList") {

    val constraint1 = {WordConstraint("one")}
    val constraint2 = {WordConstraint("two")}

    val item1 = {PatternItem(constraint1())}
    val itemConstraints2 = {PatternItem(constraint1(), constraint2())}

    val listLength1 = {PatternList(constraint1())}
    val listLength2 = {PatternList(constraint1(), length = 2)}

    val listLength2Constraints2 = {PatternList(constraint1(), constraint2(), length = 2)}

    it("should expand a list of length 1 into 2 item lists") {

      val result = listLength1().expandedForm

      assertEquals(2, result.size)
      assertEquals(PatternItemList(), result[0])
      assertEquals(PatternItemList(item1()), result[1])
    }

    it("should expand a list of length 2 into into 3 item lists") {

      val result = listLength2().expandedForm

      assertEquals(3, result.size)
      assertEquals(PatternItemList(), result[0])
      assertEquals(PatternItemList(item1()), result[1])
      assertEquals(PatternItemList(item1(), item1()), result[2])
    }

    it("should expand a list of length 1 with two contraints into 4 item lists") {

      val result = listLength2Constraints2().expandedForm

      assertEquals(3, result.size)
      assertEquals(PatternItemList(), result[0])
      assertEquals(PatternItemList(itemConstraints2()), result[1])
      assertEquals(PatternItemList(itemConstraints2(), itemConstraints2()), result[2])
    }
  }
})
