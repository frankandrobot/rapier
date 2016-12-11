package com.frankandrobot.rapier.util

import com.frankandrobot.rapier.nlp.Token
import java.util.*


fun ArrayList<Token>.indexOfWords(words : ArrayList<Token>, start : Int = 0) : Int {

  if (words.size > 0) {

    var i = start

    while (i <= this.lastIndex) {

      if (this[i].word == words.first().word && words.size + i - 1 <= this.lastIndex) {
        val match = words.foldIndexed(true) { j, total, word ->
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
