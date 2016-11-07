package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternElement
import com.frankandrobot.rapier.pattern.PatternList
import com.frankandrobot.rapier.util.combinations
import com.frankandrobot.rapier.util.fillCopy
import com.frankandrobot.rapier.util.sort
import org.funktionale.option.Option
import org.funktionale.option.toOption
import java.util.*


internal fun areEqualLengths(a : Pattern, b : Pattern) =
  a().size == b().size && a().size > 0

internal fun exactlyOneIsEmpty(a : Pattern, b : Pattern) =
  a().size != b().size && (a().size == 0 || b().size == 0)

internal fun exactlyOneHasOneElement(a : Pattern, b : Pattern) =
  (a().size > b().size && b().size == 1) || (b().size > a().size && a().size == 1)

internal fun areVeryLong(a : Pattern, b : Pattern) : Boolean {
  val patterns = sort(a, b)
  val shorter = patterns.first
  val longer = patterns.second
  val diff = longer.length() - shorter.length()

  return (longer.length() >= 3 && diff > maxDifferenceInPatternLength) ||
    (longer.length() > maxUnequalPatternLength && diff >= 2) ||
    longer.length() > maxPatternLength
}


/**
 * The case when the two patterns have the same length.
 *
 * Pairs up the pattern elements from first to last and compute the generalizations of
 * each pair. Then combine the generalizations of the pairs of elements in order.
 */
internal fun caseEqualLengthPatterns(a : Pattern, b : Pattern) : Option<List<Pattern>> {

  if (areEqualLengths(a, b)) {

    val bIter = b().iterator()
    val generalizations = a().map { generalize(it, bIter.next()) }

    return combinations(generalizations).map { Pattern(it) }.toOption()
  }

  return Option.None
}


/**
 * The case when the shorter pattern has 0 elements.
 *
 * The pattern elements in the longer pattern are generalized into a set of pattern
 * lists, one pattern list for each alternative generalization of the constraints of
 * the pattern elements. The length of the pattern lists is the sum of the lengths of
 * the elements of the longer pattern, with pattern items having a length of one.
 */
internal fun caseAnEmptyPattern(a: Pattern, b : Pattern) : Option<List<Pattern>> {

  if (exactlyOneIsEmpty(a, b)) {

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

  if (exactlyOneHasOneElement(a, b)) {

    val c = if (b().size == 1) a else b
    val d = if (b().size == 1) b else a

    val length = Math.max(
      d()[0].length,
      c().fold(0) { total, patternElement -> total + patternElement.length }
    )
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
 * -   pattern lengths difference is more than `maxDifferenceInPatternLength`
 *     for any pattern length, OR
 * -   longer pattern is more than `maxUnequalPatternLength` and patterns length difference
 *     is at least 2, OR
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

  if (areVeryLong(a, b)) {
    val longer = sort(a, b).second
    return listOf(Pattern(PatternList(length = longer.length()))).toOption()
  }

  return Option.None
}


internal fun caseGeneral(a : Pattern, b : Pattern) : Option<List<Pattern>> {

  val patterns = sort(a, b)
  val shorter = patterns.first
  val longer = patterns.second

  val newPatterns = extend(shorter().listIterator(), shorter.length(), longer.length())

  return newPatterns.flatMap{ caseEqualLengthPatterns(it, b).get() }.toOption()
}


/**
 * Extends the shorter list to longerLength by duplicating elements. However, it must
 * satisfy the following constraints:
 *
 * 1. every original element must appear in the extended lists
 * 2. elements must appear in the same order as the original list
 *
 * Example: suppose the original list is [a,b,c] and longerLength = 4.
 * Then [a,a,a,a] and [a,c,c,b] will NOT be generated. There are only three possible
 * lists:
 *
 * - [a,a,b,c]
 * - [a,b,b,c]
 * - [a,b,c,c]
 */
internal tailrec fun extend(shorter: ListIterator<PatternElement>,
                            shorterLength: Int,
                            longerLength: Int,
                            _extended: ArrayList<Pattern> = arrayListOf(Pattern()))
  : List<Pattern> {

  if (!shorter.hasNext()) { return _extended }

  val curElemIndex = shorter.nextIndex()
  val curElem = shorter.next()

  var nexExtended = _extended.fold(ArrayList<Pattern>()) { total, pattern ->

    // Here's how we satisfy the constraint that every element in the shorter list
    // must match with at least one element in the longer list:
    // we can add at most maxIndex-minIndex+1 copies of the current element.
    // And that's exactly what we do---we create new Patterns with the current elem
    // added 1 to maxIndex-minIndex+1 times.
    val prevIndex = pattern.length()
    val minIndex = Math.max(prevIndex, curElemIndex)
    val maxIndex = longerLength - shorterLength + curElemIndex
    val newPatterns = ArrayList<Pattern>()

    if (shorter.hasNext()) {
      var i = minIndex

      while (i <= maxIndex) {
        newPatterns.add(pattern + Pattern(fillCopy(i - minIndex + 1, curElem)))
        ++i
      }
    }
    else { // we reached the end so just fill the rest of the list with the last elem
      newPatterns.add(pattern + Pattern(fillCopy(maxIndex - minIndex + 1, curElem)))
    }

    total.addAll(newPatterns)
    total
  }

  return extend(shorter, shorterLength, longerLength, nexExtended)
}
