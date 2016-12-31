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

package com.frankandrobot.rapier.meta

import com.frankandrobot.rapier.wordTokens
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.jetbrains.spek.api.Spek


class SlotSpec : Spek({
  describe("SlotFiller") {

    describe("token list") {
      it("should return token list when set") {
        val filler = SlotFiller(tokens = wordTokens("a", "b", "c"))
        filler() shouldEqual wordTokens("a", "b", "c")
      }

      it("should throw an exception when nothing set") {
        val filler = SlotFiller()
        val tokens = {filler()}
        tokens shouldThrow Exception::class
      }
    }

    describe("hashset") {
      it("should add distinct slotFillers") {
        val filler1 = SlotFiller(tokens = wordTokens("1"))
        val filler2 = SlotFiller(tokens = wordTokens("2"))
        val result = hashSetOf(filler1, filler2)
        result shouldContain filler1
        result shouldContain filler2
        result.size shouldEqual 2
      }

      it("should add distinct filler, part 2") {
        val filler1 = SlotFiller(tokens = wordTokens("two"))
        val filler2 = SlotFiller(tokens = wordTokens("three"))
        val result = hashSetOf(filler1, filler2)
        result shouldContain SlotFiller(tokens = wordTokens("two"))
        result shouldContain SlotFiller(tokens = wordTokens("three"))
        result.size shouldEqual 2
      }
    }
  }
})
