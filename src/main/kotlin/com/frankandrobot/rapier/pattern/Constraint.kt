package com.frankandrobot.rapier.pattern

import com.frankandrobot.rapier.nlp.Token


interface Constraint {

  val value : String

  fun satisfies(token: Token) : Boolean
}

data class PosTagConstraint(override val value: String = "") : Constraint {

  override fun satisfies(token: Token) = token.posTag.equals(value)
}

data class WordConstraint(override val value: String = "") : Constraint {

  override fun satisfies(token: Token) = token.word.equals(value)
}

data class SemanticConstraint(override val value: String = "") : Constraint {

  override fun satisfies(token: Token) = token.semanticClass.equals(value)
}
