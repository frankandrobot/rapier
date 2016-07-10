package com.frankandrobot.rapier.pattern


open class Constraint(open val value: String)

class SyntacticConstraint(override val value: String = "") : Constraint(value)
class WordConstraint(override val value: String = "") : Constraint(value)
class SemanticConstraint(override val value: String = "") : Constraint(value)
