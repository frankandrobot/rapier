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

package com.frankandrobot.rapier.pattern

import com.frankandrobot.rapier.wordTagToken
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class PatternElementSpec : Spek({

  describe("PatternElement") {

    val anyToken = wordTagToken("any word", "any tag")

    describe("PatternItem") {

      it("should work on unconstrained patterns") {
        val elem = PatternItem()
        assertEquals(true, elem.test(anyToken))
      }
    }

    describe("PatternList") {

    }

    describe("PatternItem equality") {
      it("should work when equal constrains are out of order") {
        val a = PatternItem(
          listOf("a", "b").map(::WordConstraint).toHashSet(),
          listOf("tag1", "tag2").map(::PosTagConstraint).toHashSet()
        )
        val b = PatternItem(
          listOf("b", "a").map(::WordConstraint).toHashSet(),
          listOf("tag2", "tag1").map(::PosTagConstraint).toHashSet()
        )
        assertEquals(true, a == b)
      }
    }

    describe("PatternList equality") {
      it("should work when equal constrains are out of order") {
        val a = PatternList(
          listOf("a", "b").map(::WordConstraint).toHashSet(),
          listOf("tag1", "tag2").map(::PosTagConstraint).toHashSet(),
          length = 1
        )
        val b = PatternList(
          listOf("b", "a").map{WordConstraint(it)}.toHashSet(),
          listOf("tag2", "tag1").map{PosTagConstraint(it)}.toHashSet(),
          length = 1
        )
        assertEquals(true, a == b)
      }
    }
  }
})
