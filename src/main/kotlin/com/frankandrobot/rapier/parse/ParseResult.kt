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

package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.util.BetterIterator
import org.funktionale.option.Option
import java.util.*


/**
 * Monad-inspired data structure.
 *
 * - tracks the location of the token iterator
 * - as well as the current matches
 * - if matchFound, continuation function will execute if called
 */
data class ParseResult(private val _tokens : BetterIterator<Token>,
                       val matchFound : Boolean = true,
                       val matches : ArrayList<Option<Token>>
                       = ArrayList<Option<Token>>()) {

  val tokens : BetterIterator<Token>
    get() = _tokens.clone()

  fun <T> then(next : (ParseResult) -> List<T>) : List<T> {

    if (matchFound) { return next(this) }

    return ArrayList()
  }
}
