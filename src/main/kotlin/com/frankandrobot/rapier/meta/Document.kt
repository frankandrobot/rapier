package com.frankandrobot.rapier.meta

import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.nlp.tokenize
import org.funktionale.option.Option
import java.util.*


data class Document(val raw : Option<String> = Option.None) {

  /**
   * Use this to avoid running #tokenize in tests, which is expensive.
   * Yea, test code made it to prod. Kinda bad but should be safe.
   */
  internal constructor(wordTokens : ArrayList<Token>) : this() {
    _test = wordTokens
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
