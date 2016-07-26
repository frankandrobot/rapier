package com.frankandrobot.rapier.pattern

/**
 * Each pattern is a sequence of zero or more pattern elements.
 */
class Pattern(val patternElements: List<PatternElement>) {

  internal constructor(patternElement: PatternElement) : this(listOf(patternElement))
}
