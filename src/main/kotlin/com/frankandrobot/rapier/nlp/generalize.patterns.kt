package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.pattern.Pattern
import java.util.*


internal data class Match(val matchIndexSmall : Int, val matchIndexLarge : Int)

internal fun findMatches(a : Pattern, b : Pattern) : ArrayList<Match> {

  val smaller = if (a().size <= b().size) a else b
  val larger = if (a().size <= b().size) b else a

  val matches = ArrayList<Match>(smaller().size)

  smaller().withIndex().forEach { patternElement ->

    val prevIndex : Int = if (matches.isNotEmpty()) matches[matches.lastIndex].matchIndexLarge else -1

    val searchRangeStart = Math.max(patternElement.index, prevIndex + 1)
    val searchRangeEnd = larger().size - smaller().size + patternElement.index

    var i = searchRangeStart

    while (i <= searchRangeEnd) {

      if (patternElement.value == larger()[i]) {

        matches.add(Match(patternElement.index, i))
      }

      ++i
    }
  }

  return matches
}

fun generalize(a : Pattern, b : Pattern) {

  val matches = findMatches(a, b)
}
