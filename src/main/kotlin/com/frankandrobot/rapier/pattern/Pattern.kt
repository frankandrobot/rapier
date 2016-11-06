package com.frankandrobot.rapier.pattern

import com.frankandrobot.rapier.parse.PatternExpandedForm
import java.util.*


/**
 * Each pattern is a sequence of zero or more pattern elements.
 */
data class Pattern(private val patternElements: List<out PatternElement> = emptyList()) {

  internal constructor(vararg patternElement: PatternElement)
  : this(ArrayList<PatternElement>() + patternElement.asList())

  internal constructor(vararg patternItems : String)

  : this(patternItems.map{ PatternItem(it) })

  internal constructor(vararg patternItems : Int)

  : this(patternItems.map{ PatternItem(it.toString()) })


  operator fun invoke() = patternElements

  operator fun plus(pat : Pattern) = Pattern(patternElements + pat.patternElements)


  private val expandedForm = PatternExpandedForm(this)


  fun expandedForm() = this.expandedForm.invoke()

  fun length() = invoke().size
}
