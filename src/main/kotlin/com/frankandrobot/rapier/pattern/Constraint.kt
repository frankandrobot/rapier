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
