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

import com.frankandrobot.rapier.nlp.WordToken
import org.funktionale.option.Option.Some
import java.util.*


data class BlankTemplate(@JvmField val name : String,
                         private val slots: HashSet<SlotName>) {
  /**
   * Used by Java
   */
  constructor(name : String, slots : java.util.List<String>)
    : this(name, slots.map(::SlotName).toHashSet())
  operator fun invoke() = slots
}

data class FilledTemplate(private val slots : Slots) {
  /**
   * Used by Java
   */
  constructor(slots : java.util.Map<String,java.util.List<String>>)
  : this (
    Slots(
      // is there a cleaner way of transforming maps in kotlin?
      slots = slots.entrySet().map {Slot(
        name = SlotName(it.key),
        slotFillers = it.value.map{ slotFiller ->
          SlotFiller(
            tokens = slotFiller
              .replace("  ", " ")
              .split(" ")
              .map{WordToken(Some(it))} as ArrayList<WordToken>
          )
        }.toHashSet()
      )}.fold(HashMap<SlotName, Slot>()){ total,cur -> total[cur.name] = cur; total }
    )
  )
  operator fun get(slotName : SlotName) = slots[slotName]
}
