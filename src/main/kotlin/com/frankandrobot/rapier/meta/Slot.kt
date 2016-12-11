package com.frankandrobot.rapier.meta

import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.nlp.tokenize
import org.funktionale.option.Option
import java.util.*


data class Slot(val name: SlotName,
                val fillers : HashSet<SlotFiller>,
                val enabled : Boolean = true)

data class SlotName(private val name : String) {

  operator fun invoke() = name
}

/**
 * Even though you have the option of having no "raw" value, that will throw an error
 * in production. So you should always expect this to be set.
 */
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
}

//TODO throughly test hashsets
//TODO test wordconstraints and tokens
