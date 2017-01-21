package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.nlp.Token
import org.funktionale.option.Option
import java.util.*

data class MatchInfo(val index : Option<Int>,
                     val matches : Option<ArrayList<Token>>) {

  constructor(index : Option<Int>, matches : ArrayList<Token>)
  : this(index, matches._toOption())

  operator fun invoke() = matches
}
