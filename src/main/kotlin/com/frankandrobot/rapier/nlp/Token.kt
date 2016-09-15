package com.frankandrobot.rapier.nlp


data class Token(val word : String, val posTag : String, val semanticClass : String) {

  internal constructor(word : String) : this(word, "", "")
  internal constructor(word : String, tag : String) : this(word, tag, "")
}

val EmptyToken = Token(word = "", posTag = "", semanticClass = "")
