package com.frankandrobot.rapier.pattern

import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.parse.PatternListExpandedForm
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

  internal constructor(vararg words : String)
  : this(ArrayList<WordConstraint>().plus(words.map { WordConstraint(it) }))


  fun test(token: Token) : Boolean {

    return (wordConstraints.size === 0 || wordConstraints.any{ it.satisfies(token) }) &&
      (posTagContraints.size === 0 || posTagContraints.any{ it.satisfies(token) }) &&
      (semanticConstraints.size === 0 || semanticConstraints.any{ it.satisfies(token) })
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

  private val expandedForm = PatternListExpandedForm(this)

  fun expandedForm() = this.expandedForm.invoke()
}
