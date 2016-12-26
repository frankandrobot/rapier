package com.frankandrobot.rapier.nlp

import org.funktionale.option.Option
import org.funktionale.option.Option.None
import org.funktionale.option.Option.Some


interface IToken {
  val word: Option<String>
  val posTag: Option<String>
  val semanticClass: Option<String>
}

data class Token(override val word : Option<String>,
                 override val posTag : Option<String>,
                 override val semanticClass : Option<String>) : IToken {

  fun dropTagAndSemanticProperties() = WordToken(word)
}

data class WordToken(override val word : Option<String>) : IToken {
  override val posTag = None
  override val semanticClass = None
}

fun wordTagToken(word : String, tag : String) = Token(
  word = Some(word),
  posTag = Some(tag),
  semanticClass = None
)


