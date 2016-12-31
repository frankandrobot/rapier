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

import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.PatternList
import com.frankandrobot.rapier.pattern.WordConstraint
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class PatternListExpandedFormSpec : Spek({

  describe("PatternList") {

    val constraint1 = { WordConstraint("one") }
    val constraint2 = { WordConstraint("two") }

    val item1 = { PatternItem(constraint1()) }
    val itemConstraints2 = { PatternItem(constraint1(), constraint2()) }

    val listLength1 = { PatternList(constraint1()) }
    val listLength2 = { PatternList(constraint1(), length = 2) }

    val listLength2Constraints2 = { PatternList(constraint1(), constraint2(), length = 2) }


    it("should expand a list of length 1 into 2 item lists") {

      val result = listLength1().expandedForm

      assertEquals(2, result.size)
      assertEquals(ParsePatternItemList(), result[0])
      assertEquals(ParsePatternItemList(item1()), result[1])
    }

    it("should expand a list of length 2 into into 3 item lists") {

      val result = listLength2().expandedForm

      assertEquals(3, result.size)
      assertEquals(ParsePatternItemList(), result[0])
      assertEquals(ParsePatternItemList(item1()), result[1])
      assertEquals(ParsePatternItemList(item1(), item1()), result[2])
    }

    it("should expand a list of length 1 with two contraints into 4 item lists") {

      val result = listLength2Constraints2().expandedForm

      assertEquals(3, result.size)
      assertEquals(ParsePatternItemList(), result[0])
      assertEquals(ParsePatternItemList(itemConstraints2()), result[1])
      assertEquals(ParsePatternItemList(itemConstraints2(), itemConstraints2()), result[2])
    }
  }
})
