package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternList
import com.frankandrobot.rapier.util.combinations
import com.frankandrobot.rapier.util.sort
import org.funktionale.option.Option
import org.funktionale.option.toOption

/**
 * The case when the two patterns have the same length.
 *
 * Pairs up the pattern elements from first to last and compute the generalizations of
 * each pair. Then combine the generalizations of the pairs of elements in order.
 */
internal fun caseEqualSizePatterns(a : Pattern, b : Pattern) : Option<List<Pattern>> {

  if (a().size == b().size && a().size > 0) {

    val bIter = b().iterator()
    val generalizations = a().map { generalize(it, bIter.next()) }

    return combinations(generalizations).map { Pattern(it) }.toOption()
  }

  return Option.None
}


/**
 * The case when the shorter pattern has 0 elements.
 *
 * The pattern elements in the longer pattern are generalized into a set of pattern lists,
 * one pattern list for each alternative generalization of the constraints of the pattern elements.
 * The length of the pattern lists is the sum of the lengths of the elements of the longer pattern,
 * with pattern items having a length of one.
 */
internal fun caseAnEmptyPattern(a: Pattern, b : Pattern) : Option<List<Pattern>> {

  if (a().size != b().size && (a().size == 0 || b().size == 0)) {

    val nonEmpty = if (a().size == 0) b else a
    val length = nonEmpty().fold(0) { total, patternElement -> total + patternElement.length }
    val generalizations = nonEmpty().fold(listOf(nonEmpty()[0])) { total, patternElement ->
      total.flatMap { prevPatternElement -> generalize(prevPatternElement, patternElement) }.distinct()
    }

    return generalizations
      .map {
        PatternList(
          wordConstraints = it.wordConstraints,
          posTagConstraints = it.posTagConstraints,
          semanticConstraints = it.semanticConstraints,
          length = length
        )
      }
      .map { Pattern(it) }
      .toOption()
  }

  return Option.None
}


/**
 * The case when the shorter pattern has exactly 1 element.
 */
internal fun casePatternHasSingleElement(a: Pattern, b: Pattern) : Option<List<Pattern>> {

  if (a().size != b().size && a().size > 0 && b().size > 0 && (a().size == 1 || b().size == 1)) {

    val c = if (b().size == 1) a else b
    val d = if (b().size == 1) b else a

    val length = c().fold(d()[0].length) { total, patternElement -> total + patternElement.length }

    val generalizations = c().fold(listOf(d()[0])) { total, patternElement ->

      total.flatMap { prevPatternElement -> generalize(prevPatternElement, patternElement) }.distinct()
    }

    return generalizations
      .map {
        PatternList(
          wordConstraints = it.wordConstraints,
          posTagConstraints = it.posTagConstraints,
          semanticConstraints = it.semanticConstraints,
          length = length
        )
      }
      .map { Pattern(it) }
      .toOption()
  }

  return Option.None
}

internal val maxPatternLength = 15
internal val maxUnequalPatternLength = 10
internal val maxDifferenceInPatternLength = 5


/**
 * Handle the case when
 *
 * -   difference in pattern lengths is more than `maxDifferenceInPatternLength`.
 *     Any pattern length
 * -   longer pattern is more than `maxUnequalPatternLength` and patterns have different
 *     lengths
 * -   longer pattern is more than `maxPatternLength`
 *
 * With these settings
 *
 *     maxPatternLength = 15
 *     maxUnequalPatternLength = 10
 *     maxDifferenceInPatternLength = 5
 *
 * this will NOT handle the following scenarios:
 *
 * -   the longest pattern is less than 10 and the difference in lengths is less than 5
 * -   the two patterns have the same length and are less than 15
 */
internal fun caseVeryLongPatterns(a : Pattern, b : Pattern) : Option<List<Pattern>> {

  val patterns = sort(a, b)
  val shorter = patterns.first
  val longer = patterns.second
  val diff = longer.length() - shorter.length()

  // original constraints
  // if (((longer > 2) && (diff > 2)) || ((longer > 5) && (diff > 1)) || (longer > 6)) {
  if ((longer.length() > 2 && diff > maxDifferenceInPatternLength) ||
    (longer.length() > maxUnequalPatternLength && diff > 1) ||
    longer.length() > maxPatternLength) {

    return listOf(Pattern(PatternList(length = longer.length()))).toOption()
  }

  return Option.None
}
