package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.util.BetterIterator
import java.util.*

fun textToTokenList(vararg text : String) = text.flatMap{it.split(" ")}.map{ Token(it) }
fun textToTokenIterator(text : String, start : Int = 0) = BetterIterator(textToTokenList(text) as ArrayList, start)
