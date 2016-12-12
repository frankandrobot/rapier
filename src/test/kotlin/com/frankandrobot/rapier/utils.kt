package com.frankandrobot.rapier

import com.frankandrobot.rapier.meta.Slot
import com.frankandrobot.rapier.meta.SlotFiller
import com.frankandrobot.rapier.meta.SlotName
import com.frankandrobot.rapier.pattern.BaseRule
import com.frankandrobot.rapier.pattern.Pattern
import java.util.*


fun dummySlot(name : String) = Slot(SlotName(name), slotFillers = HashSet<SlotFiller>())

fun emptyBaseRule() = BaseRule(
  preFiller = Pattern(),
  filler = Pattern(),
  postFiller = Pattern(),
  slot = Slot(SlotName("none"), slotFillers = HashSet<SlotFiller>())
)
