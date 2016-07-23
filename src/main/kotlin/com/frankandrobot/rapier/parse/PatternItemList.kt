package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.Token
import com.frankandrobot.rapier.util.BetterIterator
import java.util.*


/**
 * Note how this class is not a PatternElement.
 *
 * It's mainly for parsing, not Rule creation.
 */
class PatternItemList(val patternItemList : ArrayList<PatternItem> = ArrayList<PatternItem>()) : IParseable<Token> {

  override fun parse(tokens : BetterIterator<Token>) : BetterIterator<Token> {

    val _tokens = tokens.clone()

    patternItemList.all{ patternItem -> tokens.hasNext() && patternItem.test(tokens.next()) }

    return _tokens
  }
}
