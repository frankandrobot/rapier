package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternList
import com.frankandrobot.rapier.util.None
import com.frankandrobot.rapier.util.Option
import com.frankandrobot.rapier.util.Some
import com.frankandrobot.rapier.util.combinations

/**
 * The case when the two patterns have the same length.
 *
 * Pairs up the pattern elements from first to last and compute the generalizations of
 * each pair. Then combine the generalizations of the pairs of elements in order.
 */
internal fun caseEqualSize(a : Pattern, b : Pattern) : Option<List<Pattern>?> {

  if (a().size == b().size) {

    val bIter = b().iterator()
    val generalizations = a().map { generalize(it, bIter.next()) }

    return Some(combinations(generalizations).map { Pattern(it) })
  }

  return None()
}

/**
 * The case when the shorter pattern has 0 elements.
 *
 * The pattern elements in the longer pattern are generalized into a set of pattern lists,
 * one pattern list for each alternative generalization of the constraints of the pattern elements.
 * The length of the pattern lists is the sum of the lengths of the elements of the longer pattern,
 * with pattern items having a length of one.
 */
internal fun caseEmptyPattern(a: Pattern, b : Pattern) : Option<List<Pattern>?> {

  if (a().size != b().size && (a().size == 0 || b().size == 0)) {

    val c = if (a().size == 0) a else b

    val length = c().fold(0) { total, patternElement -> total + patternElement.length }

    val generalizations = c().fold(listOf(c()[0])) { total, patternElement ->

      total.flatMap { prevPatternElement -> generalize(prevPatternElement, patternElement) }.distinct()
    }

    return Some(generalizations
      .map {
        PatternList(
          wordConstraints = it.wordConstraints,
          posTagConstraints = it.posTagConstraints,
          semanticConstraints = it.semanticConstraints,
          length = length
        )
      }
      .map { Pattern(it) })
  }

  return None()
}

/**
 * The case when the shorter pattern has exactly 1 element.
 */
internal fun caseSingleElement(a: Pattern, b: Pattern) : Option<List<Pattern>?> {

  if (a().size != b().size && (a().size == 1 || b().size == 1)) {

    val c = if (b().size == 1) a else b
    val d = if (b().size == 1) b else a

    val length = c().fold(d()[0].length) { total, patternElement -> total + patternElement.length }

    val generalizations = c().fold(listOf(d()[0])) { total, patternElement ->

      total.flatMap { prevPatternElement -> generalize(prevPatternElement, patternElement) }.distinct()
    }

    return Some(generalizations
      .map {
        PatternList(
          wordConstraints = it.wordConstraints,
          posTagConstraints = it.posTagConstraints,
          semanticConstraints = it.semanticConstraints,
          length = length
        )
      }
      .map { Pattern(it) })
  }

  return None()
}
