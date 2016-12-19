package com.frankandrobot.rapier.meta

import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.nlp.tokenize
import com.frankandrobot.rapier.nlp.wordToken
import org.funktionale.option.Option
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

data class SlotFiller(val raw : Option<String> = Option.None,
                      private val tokens : ArrayList<Token> = ArrayList<Token>()) {

  operator fun invoke() : ArrayList<Token> {
    if (raw.isDefined() && tokens.isEmpty()) {
      tokens.addAll(tokenize(raw.get()))
    }
    else if (raw.isEmpty() && tokens.isEmpty()) {
      throw Exception("You forgot to set the raw value or test token values")
    }

    return tokens
  }

  fun dropTagAndSemanticProperties() =
    SlotFiller(
      raw = raw,
      tokens = tokens
        .filter{it.word.isDefined()}
        .map{wordToken(it.word.get())} as ArrayList<Token>
    )
}

fun slotNames(vararg names : String) = names.map(::SlotName).toHashSet()

internal fun slots(vararg slots : Pair<SlotName, SlotFillerInfo>) =
  slots.map{ slot -> Slot(
    name = slot.first,
    slotFillers = slot.second.slotFillers,
    enabled = slot.second.enabled
  )}.fold(HashMap<SlotName,Slot>()) { total, slot -> total[slot.name] = slot; total }

internal fun slotFillers(vararg slotFillers: ArrayList<Token>) =
  SlotFillerInfo(
    enabled = true,
    slotFillers = slotFillers
      .map{ tokens -> SlotFiller(tokens = tokens) }
      .fold(HashSet<SlotFiller>()){ total, slotFiller -> total.add(slotFiller); total }
  )

internal fun disabledSlotFillers(vararg slotFillers : ArrayList<Token>) =
  SlotFillerInfo(
   enabled = false,
    slotFillers = slotFillers
      .map{ tokens -> SlotFiller(tokens = tokens) }
      .fold(HashSet<SlotFiller>()){ total, slotFiller -> total.add(slotFiller); total }
  )
