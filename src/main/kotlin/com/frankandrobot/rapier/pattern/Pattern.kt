package com.frankandrobot.rapier.pattern

import com.frankandrobot.rapier.parse.PatternExpandedForm
import java.util.*

/**
 * Each pattern is a sequence of zero or more pattern elements.
 */
class Pattern(private val patternElements: List<PatternElement> = emptyList()) {

  internal constructor(vararg patternElement: PatternElement)
  : this(ArrayList<PatternElement>() + patternElement.asList())

  operator fun invoke() = patternElements

  private val expandedForm = PatternExpandedForm(this)

  fun expandedForm() = this.expandedForm.invoke()
}
