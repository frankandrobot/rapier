package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.util.combinations
import com.frankandrobot.rapier.util.sort
import org.funktionale.option.Option
import java.util.*


internal data class MatchIndices(val leftIndex: Int, val rightIndex: Int)


/**
 * Find identical pattern elements in the two patterns subject to constraints:
 *
 * 1. matches must be in order i.e., in the patterns a:[x,y] and b:[y,x], pattern elements
 *    x and y cannot both match.
 * 2. every pattern element in the shorter pattern must be allowed to match one element
 *    in the longer pattern.
 */
internal fun findExactMatchIndices(a : Pattern, b : Pattern) : ArrayList<MatchIndices> {

  val patterns = sort(a, b)
  val left = patterns.first
  val right = patterns.second
  val matches = arrayListOf(MatchIndices(-1, -1))

  left().withIndex().forEach { patternElement ->

    val prevMatchIndex = matches[matches.lastIndex].rightIndex
    val searchRangeStart = Math.max(patternElement.index, prevMatchIndex + 1)
    val searchRangeEnd = right().size - left().size + patternElement.index

    var i = searchRangeStart

    while (i <= searchRangeEnd) {
      if (patternElement.value == right()[i]) {
        matches.add(MatchIndices(patternElement.index, i))
        break
      }
      ++i
    }
  }

  return (matches + arrayListOf(MatchIndices(left().lastIndex+1, right().lastIndex+1)))
    as ArrayList<MatchIndices>
}


/**
 * Splits the patterns by exact matches (if any). For example, given patterns
 *
 *     a:[x,y,z] and b:[1,x,2,3,z,4],
 *
 * it matches "x" and "z", so returns
 *
 *     [
 *       Pair([], [1]),
 *       Pair([x], [x]),
 *       Pair([y], [2,3]),
 *       Pair([z], [z]),
 *       Pair([], [4])
 *     ]
 */
internal fun partitionByExactMatches(a : Pattern, b : Pattern) : List<Pair<Pattern,Pattern>> {

  val patterns = sort(a, b)
  val left = patterns.first
  val right = patterns.second
  val matches = findExactMatchIndices(a, b)

  if (matches.size > 2) {

    return matches.foldIndexed(emptyList<Pair<Pattern,Pattern>>()){ i, total, match ->

      if (i > 0) {

        val prevLeftIndex = matches[i - 1].leftIndex + 1
        val prevRightIndex = matches[i - 1].rightIndex + 1

        val leftPatterns = left().subList(prevLeftIndex, match.leftIndex)
        val rightPatterns = right().subList(prevRightIndex, match.rightIndex)

        if (i < matches.lastIndex) {

          total +
            Pair(Pattern(leftPatterns), Pattern(rightPatterns)) +
            Pair(Pattern(left()[match.leftIndex]), Pattern(right()[match.rightIndex]))
        }
        else {

          total +
            Pair(Pattern(leftPatterns), Pattern(rightPatterns))
        }
      }
      else {

        total
      }
    }.filter{ it.first().size > 0 || it.second().size > 0 }
  }

  return listOf(Pair(a, b))
}


fun generalize(a : Pattern, b : Pattern) : List<Pattern> {

  val segments : List<Pair<Pattern,Pattern>> = partitionByExactMatches(a, b)
  // find the generalized patterns for the partitions
  val partitionPatterns : List<List<Pattern>> = segments.map{ patterns ->

    val veryLongPatterns = caseVeryLongPatterns(patterns.first, patterns.second)
    val equalSizePatterns = caseEqualLengthPatterns(patterns.first, patterns.second)
    val hasSingleElementPattern = casePatternHasSingleElement(patterns.first, patterns.second)
    val hasEmptyPattern = caseAnEmptyPattern(patterns.first, patterns.second)

    return when(veryLongPatterns) {
      is Option.Some<List<Pattern>> -> veryLongPatterns.get()
      else -> when (equalSizePatterns) {
        is Option.Some<List<Pattern>> -> equalSizePatterns.get()
        else -> when (hasSingleElementPattern) {
          is Option.Some<List<Pattern>> -> hasSingleElementPattern.get()
          else -> when (hasEmptyPattern) {
            is Option.Some<List<Pattern>> -> hasEmptyPattern.get()
            else -> emptyList()
          }

        }
      }
    }
  }
  //now combine the partition patterns
  return partitionPatterns.foldIndexed(emptyList()){ i, total, curPatternListForSegment ->
    if (i == 0) {
      curPatternListForSegment
    }
    else {
      combinations(total, curPatternListForSegment) { a : Pattern, b : Pattern -> a + b }
    }
  }
}
