package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.pattern.Pattern
import java.util.*


internal data class MatchIndeces(val leftIndex: Int, val rightIndex: Int)

internal fun sort(a : Pattern, b : Pattern) : Pair<Pattern, Pattern> {

  val left = if (a().size <= b().size) a else b
  val right = if (a().size <= b().size) b else a

  return Pair(left, right)
}

internal fun findMatches(a : Pattern, b : Pattern) : ArrayList<MatchIndeces> {

  val patterns = sort(a, b)
  val left = patterns.first
  val right = patterns.second

  val matches = ArrayList<MatchIndeces>(left().size + 2)

  left().withIndex().forEach { patternElement ->

    val prevIndex : Int = if (matches.isNotEmpty()) matches[matches.lastIndex].rightIndex else -1

    val searchRangeStart = Math.max(patternElement.index, prevIndex + 1)
    val searchRangeEnd = right().size - left().size + patternElement.index

    var i = searchRangeStart

    while (i <= searchRangeEnd) {

      if (patternElement.value == right()[i]) {

        matches.add(MatchIndeces(patternElement.index, i))
      }

      ++i
    }
  }

  return matches
}

fun findMatchingPatternSegments(a : Pattern, b : Pattern) : List<Pair<Pattern,Pattern>> {

  val patterns = sort(a, b)
  val left = patterns.first
  val right = patterns.second
  val matches = arrayListOf(MatchIndeces(-1, -1)) +
    findMatches(a, b) + arrayListOf(MatchIndeces(left().lastIndex, right().lastIndex))

  if (matches.size > 2) {

    return matches.foldIndexed(emptyList<Pair<Pattern,Pattern>>()){ i, total, match ->

      if (i > 0) {

        val prevLeftIndex = matches[i - 1].leftIndex + 1
        val prevRightIndex = matches[i - 1].rightIndex + 1

        val leftPatterns = left().subList(prevLeftIndex, match.leftIndex)
        val rightPatterns = right().subList(prevRightIndex, match.rightIndex)

        total +
          Pair(Pattern(leftPatterns), Pattern(rightPatterns)) +
          Pair(Pattern(left()[match.leftIndex]), Pattern(right()[match.rightIndex]))
      }
      else {

        total
      }
    }.filter{ it.first().size > 0 && it.second().size > 0 }
  }

  return listOf(Pair(a, b))
}

