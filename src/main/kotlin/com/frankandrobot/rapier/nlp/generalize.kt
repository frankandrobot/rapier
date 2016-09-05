package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.pattern.*
import java.util.*


/**
 * If the constraints are the same, returns the same constraints.
 * Otherwise it returns two constraints: an empty constraint and the union of both constraints.
 */
internal fun <T : Constraint> generalize(a : HashSet<out T>, b : HashSet<out T>)
  : List<HashSet<out T>> {

  //first check if the constraints are the same
  if (a.size == 0 && b.size == 0 || (a.size == b.size && a.containsAll(b) && b.containsAll(a))) {

    return listOf(a)
  }

  return listOf(hashSetOf(), (a + b) as HashSet<T>)
}

/**
 * Currently generalizes only words and POS tags so...
 * returns at most 4 pattern elements.
 */
internal fun generalize(a : PatternElement, b : PatternElement) : List<PatternElement> {

  var wordGeneralizations = generalize(a.wordConstraints, b.wordConstraints)
  var tagGeneralizations = generalize(a.posTagContraints, b.posTagContraints)

  return wordGeneralizations.flatMap { wordConstraints -> tagGeneralizations.map{ tagConstraints ->

    if (a is PatternItem && b is PatternItem) {

      PatternItem(wordConstraints, tagConstraints)
    }

    val aLength = if (a is PatternList) a.length else 0
    val bLength = if (b is PatternList) b.length else 0

    val length = Math.max(aLength, bLength)

    PatternList(wordConstraints, tagConstraints, length = length)
  }}
}

//fun generalize(a : Pattern, b : Pattern) : List<Pattern> {
//
//  if (a().size == b().size) {
//
//    a().withIndex().map
//  }
//}
