package com.frankandrobot.rapier.util

import com.frankandrobot.rapier.pattern.Pattern


/**
 * First is the shorter pattern, second is the longer patern
 */
fun sort(a : Pattern, b : Pattern) : Pair<Pattern, Pattern> {

  val shorter = if (a.length() <= b.length()) a else b
  val longer = if (a.length() <= b.length()) b else a

  return Pair(shorter, longer)
}

