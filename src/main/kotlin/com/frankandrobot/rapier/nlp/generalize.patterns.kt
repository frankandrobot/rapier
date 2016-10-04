package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternElement
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.PatternList
import com.frankandrobot.rapier.util.combinations
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

/**
 * Pairs up the pattern elements from first to last and compute the generalizations of
 * each pair. Then combine the generalizations of the pairs of elements in order.
 */
internal fun caseEqualSize(a : Pattern, b : Pattern) : List<Pattern> {

  assert(a().size == b().size)

  val bIter = b().iterator()
  val generalizations = a().map{ generalize(it, bIter.next()) }

  return combinations(generalizations).map{ Pattern(it) }
}

/**
 * Case shorter pattern has 0 elements.
 *
 * The pattern elements in the longer pattern are generalized into a set of pattern lists,
 * one pattern list for each alternative generalization of the constraints of the pattern elements.
 * The length of the pattern lists is the sum of the lengths of the elements of the longer pattern,
 * with pattern items having a length of one.
 */
internal fun caseZeroSize(a : Pattern) : List<Pattern> {

  val length = a().fold(0){ total, patternElement ->

    var newTotal = 0

    if (patternElement is PatternItem) { newTotal = total + 1 }
    if (patternElement is PatternList) { newTotal = total + patternElement.length }

    newTotal
  }

  val generalizations = a().fold(listOf(a()[0])){ total, patternElement ->

    total.flatMap { prevPatternElement -> generalize(prevPatternElement, patternElement) }.distinct()
  }

  return generalizations
    .map{PatternList(
      wordConstraints = it.wordConstraints,
      posTagContraints = it.posTagContraints,
      semanticConstraints = it.semanticConstraints,
      length = length
    )}
    .map{Pattern(it)}
}
