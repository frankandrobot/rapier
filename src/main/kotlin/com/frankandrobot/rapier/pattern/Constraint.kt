package com.frankandrobot.rapier.pattern

import com.frankandrobot.rapier.nlp.Token
import org.funktionale.option.Option


interface Constraint {

  val value : Option<String>

  fun satisfies(token: Token) : Boolean
}


data class PosTagConstraint(override val value : Option<String> = Option.None) :
  Constraint {

  internal constructor(value : String) : this(Option.Some(value)) {}

  override fun satisfies(token: Token) = token.posTag.equals(value)

  override fun toString() = value.toString()
}


data class WordConstraint(override val value : Option<String> = Option.None) :
  Constraint {

  internal constructor(value : String) : this(Option.Some(value)) {}

  override fun satisfies(token: Token) = token.word.equals(value)

  override fun toString() = value.toString()
}


data class SemanticConstraint(override val value : Option<String> = Option.None) :
  Constraint {

  internal constructor(value : String) : this(Option.Some(value)) {}

  override fun satisfies(token: Token) = token.semanticClass.equals(value)

  override fun toString() = value.toString()
}


fun words(vararg words : String) =
  words.map { WordConstraint(Option.Some(it)) }.toHashSet()

fun tags(vararg tags : String) =
  tags.map{ PosTagConstraint(Option.Some(it)) }.toHashSet()
