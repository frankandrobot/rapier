package com.frankandrobot.rapier.pattern

import com.frankandrobot.rapier.parse.IParseable
import com.frankandrobot.rapier.parse.PatternItemList
import com.frankandrobot.rapier.util.BetterIterator
import java.util.*


/**
 * Rapier allows 3 kinds of constraints:
 * - words the element can match,
 * - part-of-speech tags
 * - semenatic classes - we use WordNet synsets
 */
abstract class PatternElement(open val wordConstraints: List<WordConstraint> = listOf(),
                              open val posTagContraints: List<PosTagConstraint> = listOf(),
                              open val semanticConstraints: List<SemanticConstraint> = listOf())


class PatternItem(override val wordConstraints: List<WordConstraint> = listOf(),
                  override val posTagContraints: List<PosTagConstraint> = listOf(),
                  override val semanticConstraints: List<SemanticConstraint> = listOf()) :
  PatternElement(), IParseable<Token> {

  fun test(token: Token) : Boolean {

    return wordConstraints.any{ it.satisfies(token) } &&
      posTagContraints.any{ it.satisfies(token) } &&
      semanticConstraints.any{ it.satisfies(token) }
  }

  override fun parse(tokens : BetterIterator<Token>) : BetterIterator<Token> {

    val _tokens = tokens.clone()

    if (_tokens.hasNext() && test(_tokens.peek())) _tokens.next()

    return _tokens
  }
}

/**
 * There's no #test because it's not needed (due to expandedForm)
 */
class PatternList(override val wordConstraints: List<WordConstraint> = listOf(),
                  override val posTagContraints: List<PosTagConstraint> = listOf(),
                  override val semanticConstraints: List<SemanticConstraint> = listOf(),
                  val length: Int = 1) : PatternElement() {

  /**
   * Converts the pattern list into a list of pattern item lists.
   *
   * Ex: {word: foo, length: 2} => [], [foo], [foo, foo]
   */
  val expandedForm : ArrayList<PatternItemList> by lazy {

    val patternItem = PatternItem(wordConstraints, posTagContraints, semanticConstraints)

    (0..length).fold(ArrayList<PatternItemList>(), { total, count ->

      if (count === 0) { total.add(PatternItemList()) }
      else { total.add(PatternItemList((1..count).map { patternItem } as ArrayList<PatternItem>)) }

      total
    })
  }

  inline fun expand() = expandedForm

}

