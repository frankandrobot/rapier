package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.pattern.PatternItem
import java.util.*


/**
 * These might actually make sense as extension functions
 */
fun parse(patternItem: PatternItem, glob : Glob) : Glob {

  val tokens = glob.tokens()

  if (tokens.hasNext() && patternItem.test(tokens.peek())) {

    return Glob(tokens, matchFound = true, matches = glob.matches.plus(tokens.next()) as ArrayList<Token>)
  }

  return Glob(tokens, matchFound = false)
}

fun parse(patternItemList: PatternItemList, glob : Glob) : Glob {

  val tokens = glob.tokens()

  val consumed = patternItemList.patternItemList.all{ patternItem -> tokens.hasNext() && patternItem.test(tokens.next()) }

  if (consumed) {

    return Glob(tokens, matchFound = true, matches = tokens.next(patternItemList.patternItemList.size))
  }

  return Glob(tokens, matchFound = false)
}
