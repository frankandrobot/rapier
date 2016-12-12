package com.frankandrobot.rapier.meta

import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.nlp.tokenize
import org.funktionale.option.Option
import java.util.*


data class Document(val raw : Option<String> = Option.None,
                    private val tokens : ArrayList<Token> = ArrayList<Token>()) {

  operator fun invoke() : ArrayList<Token> {
    if (tokens.isEmpty() && raw.isDefined()) { tokens.addAll(tokenize(raw.get())) }
    else if (tokens.isEmpty() && raw.isEmpty()) {
      throw Exception("You forgot to set the raw value or test token values")
    }
    return tokens
  }
}
