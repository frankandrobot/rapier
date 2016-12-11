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
data class SlotFiller(val raw : Option<String> = Option.None) {

  internal constructor(raw : String) : this(Option.Some(raw))

  /**
   * Use this to avoid running #tokenize in tests, which is expensive.
   * Yea, test code made it to prod. Kinda bad but should be safe.
   */
  internal constructor(wordTokens : ArrayList<Token>) : this() {
    _test = (ArrayList<Token>() + wordTokens) as ArrayList<Token>
  }

  private var _test = ArrayList<Token>()

  private val tokens : ArrayList<Token> by lazy {

    if (_test.isEmpty() && raw.isDefined()) { tokenize(raw.get()) }
    else if (_test.isNotEmpty() && raw.isEmpty()) { _test }
    else {
      throw Exception("You forgot to set the raw value or test token values")
    }
  }

  operator fun invoke() = tokens
}

//TODO throughly test hashsets
//TODO test wordconstraints and tokens
