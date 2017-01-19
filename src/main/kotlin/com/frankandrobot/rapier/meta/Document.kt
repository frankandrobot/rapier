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

package com.frankandrobot.rapier.meta

import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.nlp.tokenize
import org.funktionale.option.Option
import org.funktionale.option.Option.Some
import java.util.*


data class Document(val raw : Option<String> = Option.None,
                    private val tokens : ArrayList<Token> = ArrayList<Token>()) {

  constructor(raw : String) : this(Some(raw))

  operator fun invoke() : ArrayList<Token> {
    if (tokens.isEmpty() && raw.isDefined()) { tokens.addAll(tokenize(raw.get())) }
    else if (tokens.isEmpty() && raw.isEmpty()) {
      throw Exception("You forgot to set the raw value or test token values")
    }
    return tokens
  }
}
