package com.frankandrobot.rapier.meta

import java.util.*


data class Slots(private val slots : HashMap<SlotName, Slot>) {

  operator fun get(slotName : SlotName) : Slot =
    slots.getOrDefault(
      slotName,
      Slot(
        name = slotName,
        slotFillers = HashSet(),
        enabled = false
      )
    )

  val enabledSlotFillers : HashSet<SlotFiller> by lazy {
    slots.filter { it.value.enabled }
      .map { it.value.slotFillers }
      .fold(HashSet<SlotFiller>()) { total, cur -> total.addAll(cur); total }
  }
}
