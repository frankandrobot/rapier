package com.frankandrobot.rapier.meta

import com.frankandrobot.rapier.nlp.WordToken
import com.frankandrobot.rapier.nlp.tokenizeWords
import org.funktionale.option.Option
import org.funktionale.option.Option.None
import java.util.*


data class Slot(val name: SlotName,
                val slotFillers: HashSet<SlotFiller>,
                val enabled : Boolean = true)

data class SlotName(private val name : String) {

  operator fun invoke() = name
}

/**
 * @deprecated convenience class to help create slots. Don't use
 */
data class SlotFillerInfo(val slotFillers : HashSet<SlotFiller>,
                          val enabled : Boolean)

data class SlotFiller(val raw : Option<String> = None,
                      private val tokens : ArrayList<WordToken> = ArrayList<WordToken>()) {

  operator fun invoke() : ArrayList<WordToken> {
    if (raw.isDefined() && tokens.isEmpty()) {
      tokens.addAll(tokenizeWords(raw.get()))
    }
    else if (raw.isEmpty() && tokens.isEmpty()) {
      throw Exception("You forgot to set the raw value or test token values")
    }

    return tokens
  }
}

fun slotNames(vararg names : String) = names.map(::SlotName).toHashSet()

internal fun slots(vararg slots : Pair<SlotName, SlotFillerInfo>) =
  slots.map{ slot -> Slot(
    name = slot.first,
    slotFillers = slot.second.slotFillers,
    enabled = slot.second.enabled
  )}.fold(HashMap<SlotName,Slot>()) { total, slot -> total[slot.name] = slot; total }

internal fun slotFillers(vararg slotFillers: ArrayList<WordToken>) =
  SlotFillerInfo(
    enabled = true,
    slotFillers = slotFillers
      .map{ tokens -> SlotFiller(tokens = tokens) }
      .fold(HashSet<SlotFiller>()){ total, slotFiller -> total.add(slotFiller); total }
  )

internal fun disabledSlotFillers(vararg slotFillers : ArrayList<WordToken>) =
  SlotFillerInfo(
   enabled = false,
    slotFillers = slotFillers
      .map{ tokens -> SlotFiller(tokens = tokens) }
      .fold(HashSet<SlotFiller>()){ total, slotFiller -> total.add(slotFiller); total }
  )
