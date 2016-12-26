package com.frankandrobot.rapier.nlp

import org.funktionale.option.Option
import org.funktionale.option.Option.None
import org.funktionale.option.Option.Some


data class Token(val word : Option<String>,
                 val posTag : Option<String>,
                 val semanticClass : Option<String>) {

  fun dropTagAndSemanticProperties() = WordToken(word)
}

data class WordToken(val word : Option<String>)

fun wordTagToken(word : String, tag : String) = Token(
  word = Some(word),
  posTag = Some(tag),
  semanticClass = None
)


