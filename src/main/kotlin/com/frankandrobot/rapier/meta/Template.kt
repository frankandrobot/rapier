package com.frankandrobot.rapier.meta

import java.util.*


data class BlankTemplate(val name : String, private val slots: HashSet<SlotName>) {
  operator fun invoke() = slots
}

data class FilledTemplate(private val slots : Slots) {
  operator fun get(slotName : SlotName) = slots[slotName]
  val enabledSlotFillers : HashSet<SlotFiller>
    get() { return slots.enabledSlotFillers }
}
