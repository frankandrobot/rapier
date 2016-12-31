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

import com.frankandrobot.rapier.tokens
import org.amshove.kluent.shouldThrow
import org.funktionale.option.Option
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals

class DocumentSpec : Spek({
  describe("Document") {
    describe("token list") {
      it("should return token list when set") {
        val doc = Document(tokens = tokens("a", "b", "c"))
        assertEquals(tokens("a", "b", "c"), doc())
      }

      it("should throw an exception when nothing set") {
        val doc = Document(raw = Option.None)
        val tokens = {doc()}
        tokens shouldThrow Exception::class
      }
    }
  }
})
