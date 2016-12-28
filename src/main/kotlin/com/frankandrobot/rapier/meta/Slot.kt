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
