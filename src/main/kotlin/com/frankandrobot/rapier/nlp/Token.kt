package com.frankandrobot.rapier.nlp

import org.funktionale.option.Option
import java.util.*


data class Token(val word : Option<String>,
                 val posTag : Option<String>,
                 val semanticClass : Option<String>)

val EmptyToken = Token(
  word = Option.None,
  posTag = Option.None,
  semanticClass = Option.None
)

fun wordToken(word : String) = Token(
  word = Option.Some(word),
  posTag = Option.None,
  semanticClass = Option.None
)

fun wordTagToken(word : String, tag : String) = Token(
  word = Option.Some(word),
  posTag = Option.Some(tag),
  semanticClass = Option.None
)

fun wordTokens(vararg words : String) = words.map(::wordToken) as ArrayList<Token>
