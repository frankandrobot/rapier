package com.frankandrobot.rapier

import com.frankandrobot.rapier.meta.Slot
import com.frankandrobot.rapier.meta.SlotFiller
import com.frankandrobot.rapier.meta.SlotName
import com.frankandrobot.rapier.nlp.wordToken
import com.frankandrobot.rapier.pattern.BaseRule
import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.util.BetterIterator
import java.util.*


fun dummySlot(name : String) = Slot(SlotName(name), slotFillers = HashSet<SlotFiller>())

fun emptyBaseRule() = BaseRule(
  preFiller = Pattern(),
  filler = Pattern(),
  postFiller = Pattern(),
  slot = Slot(SlotName("none"), slotFillers = HashSet<SlotFiller>())
)

fun textToTokenList(vararg text : String) = text.flatMap{it.split(" ")}.map(::wordToken)

fun textToTokenIterator(text : String, start : Int = 0) =
  BetterIterator(textToTokenList(text) as ArrayList, start)
