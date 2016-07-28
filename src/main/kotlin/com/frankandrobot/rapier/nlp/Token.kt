package com.frankandrobot.rapier.nlp

data class Token(val word : String, val posTag : String, val semanticClass : String) {

  internal constructor(word : String) : this(word, "", "")
}

val EmptyToken = Token(word = "", posTag = "", semanticClass = "")
