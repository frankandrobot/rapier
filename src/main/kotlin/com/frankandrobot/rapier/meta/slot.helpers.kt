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
import java.util.*


/**
 * Helper used to create sample data
 */
fun slotFillers(vararg slotFillers: ArrayList<WordToken>) =
  SlotFillerInfo(
    enabled = true,
    slotFillers = slotFillers
      .map { tokens -> SlotFiller(tokens = tokens) }
      .fold(HashSet<SlotFiller>()) { total, slotFiller -> total.add(slotFiller); total }
  )

/**
 * Helper used to create sample data
 */
fun disabledSlotFillers(vararg slotFillers : ArrayList<WordToken>) =
  SlotFillerInfo(
    enabled = false,
    slotFillers = slotFillers
      .map { tokens -> SlotFiller(tokens = tokens) }
      .fold(HashSet<SlotFiller>()) { total, slotFiller -> total.add(slotFiller); total }
  )

/**
 * Helper used to create sample data
 */
fun slots(vararg slots : Pair<SlotName, SlotFillerInfo>) =
  Slots(
    slots.map{ slot ->
      Slot(
        name = slot.first,
        slotFillers = slot.second.slotFillers,
        enabled = slot.second.enabled
      )
    }.fold(HashMap<SlotName, Slot>()) { total, slot -> total[slot.name] = slot; total }
  )

/**
 * Helper used to create sample data
 */
data class SlotFillerInfo(val slotFillers : HashSet<SlotFiller>,
                          val enabled : Boolean)

/**
 * Helper used to create sample data
 */
fun slotNames(vararg names : String) = names.map(::SlotName).toHashSet()
