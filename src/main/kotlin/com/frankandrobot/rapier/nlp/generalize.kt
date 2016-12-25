package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.pattern.*
import com.frankandrobot.rapier.util.combinations
import java.util.*


/**
 * -  if the two constraints are identical, then the new constraint is simply the same
 *    as the original constraints.
 * -  If either constraint is empty (the word or tag is unconstrained), then the
 *    generalized constraint is also empty.
 * -  If either constraint is a superset of the other, the new constraint will be the
 *    superset.
 * -  In all other cases, two alternative generalizations are created: one is the
 *    union of the two constraints and one is the empty constraint.
 */
internal fun <T : Constraint> generalize(a : HashSet<out T>, b : HashSet<out T>)
  : List<HashSet<out T>> {

  // is one of the contraints empty?
  if (a.size == 0 || b.size == 0) {
    return listOf(hashSetOf())
  }
  // is one a superset of the other?
  else if (a.size <= b.size && b.containsAll(a)) {
    return listOf(b)
  }
  else if (b.size <= a.size && a.containsAll(b)) {
    return listOf(a)
  }

  return listOf(hashSetOf(), (a + b) as HashSet<T>)
}

/**
 * Currently generalizes only words and POS tags so...
 * returns at most 4 pattern elements.
 */
fun generalize(a : PatternElement, b : PatternElement) : List<PatternElement> {

  var wordGeneralizations = generalize(a.wordConstraints, b.wordConstraints)
  var tagGeneralizations = generalize(a.posTagConstraints, b.posTagConstraints)

  return combinations(wordGeneralizations, tagGeneralizations) {
    wordConstraints : HashSet<out Constraint>, tagConstraints : HashSet<out Constraint> ->

      if (a is PatternItem && b is PatternItem) {

        PatternItem(
          wordConstraints as HashSet<out WordConstraint>,
          tagConstraints as HashSet<out PosTagConstraint>
        )
      }
      else {

        val aLength = if (a is PatternList) a.length else 0
        val bLength = if (b is PatternList) b.length else 0

        val length = Math.max(aLength, bLength)

        PatternList(
          wordConstraints as HashSet<out WordConstraint>,
          tagConstraints as HashSet<out PosTagConstraint>,
          length = length
        )
      }
  }
}
