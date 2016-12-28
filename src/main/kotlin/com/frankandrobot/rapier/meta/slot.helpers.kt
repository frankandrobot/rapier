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
