package com.frankandrobot.rapier.pattern

import com.frankandrobot.rapier.parse.ParsePatternItemList
import com.frankandrobot.rapier.parse.PatternExpandedForm
import java.util.*


/**
 * Each pattern is a sequence of zero or more pattern elements.
 */
data class Pattern(private val patternElements: List<out PatternElement> = emptyList()) {

  internal constructor(vararg patternElement: PatternElement)
  : this(ArrayList<PatternElement>() + patternElement.asList())

  internal constructor(vararg patternItems : Int)

  : this(patternItems.map{ PatternItem(words(it.toString())) })


  operator fun invoke() = patternElements

  operator fun plus(pat : Pattern) = Pattern(patternElements + pat.patternElements)


  private val _expandedForm = PatternExpandedForm(this)

  val expandedForm : ArrayList<ParsePatternItemList>
    get() = _expandedForm.invoke()

  val length : Int
    get() = invoke().size


  override fun toString() = patternElements.joinToString("\n")
}
