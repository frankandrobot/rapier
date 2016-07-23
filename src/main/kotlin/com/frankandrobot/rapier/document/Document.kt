package com.frankandrobot.rapier.document

import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.nlp.tokenize


class Document(val value: String) {

  val tokens : List<Token> by lazy {

    tokenize(value)
  }
}
