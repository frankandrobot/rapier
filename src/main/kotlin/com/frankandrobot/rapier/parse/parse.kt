package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.nlp.EmptyToken
import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.pattern.PatternItem
import java.util.*


fun PatternItem.parse(glob : Glob) : Glob {

  val tokens = glob.tokens()

  if (tokens.hasNext() && this.test(tokens.peek())) {

    return Glob(tokens, matchFound = true, matches = glob.matches.plus(tokens.next()) as ArrayList<Token>)
  }

  return Glob(glob.tokens(), matchFound = false)
}

fun ParsePatternItemList.parse(glob : Glob) : Glob {

  val tokens = glob.tokens()

  val consumed =
    this.items.size === 0 ||
    this.items.all{ patternItem -> tokens.hasNext() && patternItem.test(tokens.next()) }

  if (consumed) {

    var matches : List<Token>

    if (this.items.size === 0) { matches = arrayListOf(EmptyToken) }
    else { matches = glob.tokens().peek(this.items.size) }

    return Glob(tokens, matchFound = true, matches = glob.matches.plus(matches) as ArrayList)
  }

  return Glob(glob.tokens(), matchFound = false)
}
