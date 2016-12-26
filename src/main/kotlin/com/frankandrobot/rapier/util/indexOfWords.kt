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
