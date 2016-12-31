/*
 *    Copyright 2016 Uriel Avalos
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

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
