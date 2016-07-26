package com.frankandrobot.rapier.pattern

import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.parse.PatternItemList
import java.util.*


/**
 * Rapier allows 3 kinds of constraints:
 * - words the element can match,
 * - part-of-speech tags
 * - semenatic classes - we use WordNet synsets
 */
interface PatternElement {

  val wordConstraints: List<WordConstraint>
  val posTagContraints: List<PosTagConstraint>
  val semanticConstraints: List<SemanticConstraint>
}


data class PatternItem(override val wordConstraints: List<WordConstraint> = listOf(),
                       override val posTagContraints: List<PosTagConstraint> = listOf(),
                       override val semanticConstraints: List<SemanticConstraint> = listOf()) : PatternElement {

  internal constructor(vararg wordConstraint: WordConstraint)
  : this(ArrayList<WordConstraint>().plus(wordConstraint))

  fun test(token: Token) : Boolean {

    return wordConstraints.any{ it.satisfies(token) } &&
      posTagContraints.any{ it.satisfies(token) } &&
      semanticConstraints.any{ it.satisfies(token) }
  }
}

/**
 * A PatternList is a PatternItem that can repeat 0 or more times.
 *
 * Unfortunately, the code doesn't make this relationship explicit
 * due to the fact that it actually doesn't make sense to share code with PatternItem
 *
 * For example, the pattern list {word:[foo, bar], length: 2} matches:
 *
 * - `` (empty)
 * - `foo`
 * - `foo foo`
 * - `foo bar`
 * - `bar`
 * - `bar foo`
 * - `bar bar
 */
class PatternList(override val wordConstraints: List<WordConstraint> = listOf(),
                  override val posTagContraints: List<PosTagConstraint> = listOf(),
                  override val semanticConstraints: List<SemanticConstraint> = listOf(),
                  val length: Int = 1) : PatternElement {

  internal constructor(vararg wordConstraint: WordConstraint, length : Int = 1)
  : this(wordConstraint.asList(), length = length)

  /**
   * Converts the PatternList into a collection of PatternItemLists i.e., it expands the pattern list into lists of pattern items.
   *
   * Ex: {word: foo, length: 2} => [], [{word: foo}], [{word: foo}, {word: foo}]
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
