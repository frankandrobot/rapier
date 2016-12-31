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

package com.frankandrobot.rapier.util

import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.nlp.WordToken
import java.util.*


/**
 * Finds a sublist, matching words only
 */
fun ArrayList<Token>.indexOfWords(subList: ArrayList<WordToken>, start : Int = 0) : Int {

  if (subList.size > 0) {

    var i = start

    while (i <= this.lastIndex) {

      if (this[i].word == subList.first().word && subList.size + i - 1 <= this.lastIndex) {
        val match = subList.foldIndexed(true) { j, total, word ->
          this[j + i].word == word.word && total
        }

        if (match) {
          return i
        }
      }

      i++
    }
  }

  return -1
}
