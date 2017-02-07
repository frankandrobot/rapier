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

import com.frankandrobot.rapier.wordSlotFiller
import com.frankandrobot.rapier.wordTokens
import org.amshove.kluent.shouldEqual
import org.funktionale.option.Option
import org.funktionale.option.Option.*
import org.jetbrains.spek.api.Spek
import java.util.List
import java.util.Map


class FilledTemplateSpec : Spek({
  describe("FilledTemplate") {
    it("should get a slot") {
      val template = FilledTemplate(
        slots(SlotName("slot") to slotFillers(wordTokens("filler")))
      )
      template[SlotName("slot")] shouldEqual Slot(
        SlotName("slot"),
        slotFillers = hashSetOf(wordSlotFiller("filler"))
      )
    }

    describe("alternate constructor") {
      it("should work") {
        val template = FilledTemplate(
          hashMapOf(
            "1" to arrayListOf("one two", "three four"),
            "2" to arrayListOf("second")
          ) as Map<String, List<String>>
        )
        val defaultCons = FilledTemplate(Slots(
          hashMapOf(
            SlotName("1") to Slot(
              SlotName("1"),
              hashSetOf(
                SlotFiller(Some("one two")),
                SlotFiller(Some("three four"))
              )
            ),
            SlotName("2") to Slot(
              SlotName("2"),
              hashSetOf(
                SlotFiller(Some("second"))
              )
            )
          )
        ))
        template shouldEqual defaultCons
      }
    }
  }
})
