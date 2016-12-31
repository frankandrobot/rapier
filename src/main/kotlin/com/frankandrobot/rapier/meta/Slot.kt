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

import com.frankandrobot.rapier.nlp.WordToken
import com.frankandrobot.rapier.nlp.tokenizeWords
import org.funktionale.option.Option
import org.funktionale.option.Option.None
import java.util.*


data class Slot(val name: SlotName,
                val slotFillers: HashSet<SlotFiller>,
                val enabled : Boolean = true)


data class SlotName(private val name : String) {
  operator fun invoke() = name
}


data class SlotFiller(val raw : Option<String> = None,
                      private val tokens : ArrayList<WordToken> = ArrayList<WordToken>()) {

  operator fun invoke() : ArrayList<WordToken> {
    if (raw.isDefined() && tokens.isEmpty()) {
      tokens.addAll(tokenizeWords(raw.get()))
    }
    else if (raw.isEmpty() && tokens.isEmpty()) {
      throw Exception("You forgot to set the raw value or test token values")
    }

    return tokens
  }
}
