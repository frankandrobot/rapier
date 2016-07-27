package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.pattern.PatternItem
import java.util.*


fun PatternItem.parse(glob : Glob) : Glob {

  val tokens = glob.tokens()

  if (tokens.hasNext() && this.test(tokens.peek())) {

    return Glob(tokens, matchFound = true, matches = glob.matches.plus(tokens.next()) as ArrayList<Token>)
  }

  return Glob(tokens, matchFound = false)
}

fun PatternItemList.parse(glob : Glob) : Glob {

  val tokens = glob.tokens()

  val consumed = this.items.all{ patternItem -> tokens.hasNext() && patternItem.test(tokens.next()) }

  if (consumed) {

    return Glob(tokens, matchFound = true, matches = tokens.next(this.items.size))
  }

  return Glob(tokens, matchFound = false)
}
