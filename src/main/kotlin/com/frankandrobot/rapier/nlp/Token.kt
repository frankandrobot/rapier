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

package com.frankandrobot.rapier.nlp

import org.funktionale.option.Option
import org.funktionale.option.Option.None
import org.funktionale.option.Option.Some


data class Token(val word : Option<String>,
                 val posTag : Option<String>,
                 val semanticClass : Option<String>,
                 val startIndex : Option<Int>,
                 val endIndex : Option<Int>,
                 val lemma : Option<String>) {

  fun dropTagAndSemanticProperties() = WordToken(word)
}

fun wordTagToken(word : String, tag : String, start : Int, end : Int, lemma : String) =
  Token(
    word = Some(word),
    posTag = Some(tag),
    semanticClass = None,
    startIndex = Some(start),
    endIndex = Some(end),
    lemma = Some(lemma)
  )


