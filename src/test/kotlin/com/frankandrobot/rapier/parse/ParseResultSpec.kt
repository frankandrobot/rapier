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

import com.frankandrobot.rapier.parseResult
import com.frankandrobot.rapier.textTokenIterator
import org.amshove.kluent.shouldEqual
import org.funktionale.option.Option.None
import org.funktionale.option.Option.Some
import org.jetbrains.spek.api.Spek


class ParseResultSpec : Spek({
  describe("ParseResult") {
    it("should return matchFound when has an index") {
      val parseRes = parseResult(
        tokens = textTokenIterator("hi"),
        index = Some(0)
      )
      parseRes.matchFound shouldEqual true
    }

    it("should return no matchFound when doesn't have an index") {
      val parseRes = parseResult(
        tokens = textTokenIterator("hi"),
        index = None
      )
      parseRes.matchFound shouldEqual false
    }
  }
})
