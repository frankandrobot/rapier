package com.frankandrobot.rapier.pattern


abstract class Constraint(open val value: String) {

  abstract fun satisfies(token: Token) : Boolean
}

class PosTagConstraint(override val value: String = "") : Constraint(value) {

  override fun satisfies(token: Token) = token.posTag === value
}

class WordConstraint(override val value: String = "") : Constraint(value) {

  override fun satisfies(token: Token) = token.word === value
}

class SemanticConstraint(override val value: String = "") : Constraint(value) {

  override fun satisfies(token: Token) = token.semanticClass === value
}
