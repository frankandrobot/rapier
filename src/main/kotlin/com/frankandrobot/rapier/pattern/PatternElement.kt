package com.frankandrobot.rapier.pattern


/**
 * Rapier allows 3 kinds of constraints:
 * - words the element can match,
 * - part-of-speech tags
 * - semenatic classes - we use WordNet synsets
 */
abstract class PatternElement

class PatternItem(val wordConstraints: List<WordConstraint> = listOf(),
                  val posTagContraints: List<PosTagConstraint> = listOf(),
                  val semanticConstraints: List<SemanticConstraint> = listOf()) : PatternElement() {

  fun test(token: Token) : Boolean {

    return wordConstraints.any{ it.satisfies(token) } &&
      posTagContraints.any{ it.satisfies(token) } &&
      semanticConstraints.any{ it.satisfies(token) }
  }
}

class PatternList(val patternItem: PatternItem,
                  val length: Int = 1) : PatternElement() {

  fun test(token: List<Token>) : Boolean {

    return token.size <= length && token.all{ patternItem.test(it) }
  }
}
