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

  val wordConstraints: HashSet<out WordConstraint>
  val posTagContraints: HashSet<out PosTagConstraint>
  val semanticConstraints: HashSet<out SemanticConstraint>
}


data class PatternItem(override val wordConstraints: HashSet<out WordConstraint> = hashSetOf(),
                       override val posTagContraints: HashSet<out PosTagConstraint> = hashSetOf(),
                       override val semanticConstraints: HashSet<out SemanticConstraint> = hashSetOf())
: PatternElement {

  internal constructor(vararg wordConstraint: WordConstraint)
  : this(HashSet<WordConstraint>().plus(wordConstraint) as HashSet<out WordConstraint>)

  internal constructor(vararg words : String)
  : this(HashSet<WordConstraint>().plus(words.map { WordConstraint(it) }) as HashSet<out WordConstraint>)

  /**
   * Does the token satisfy all the constraints?
   */
  fun test(token: Token) : Boolean {

    return (wordConstraints.size === 0 || wordConstraints.any{ it.satisfies(token) }) &&
      (posTagContraints.size === 0 || posTagContraints.any{ it.satisfies(token) }) &&
      (semanticConstraints.size === 0 || semanticConstraints.any{ it.satisfies(token) })
  }
}

/**
 * A PatternList is a PatternItem that can repeat 0 or more times.
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
 *
 * Unfortunately, the code doesn't make this relationship explicit
 * due to the fact that it actually doesn't make sense to share code with PatternItem
 *
 */
data class PatternList(override val wordConstraints: HashSet<out WordConstraint> = hashSetOf(),
                  override val posTagContraints: HashSet<out PosTagConstraint> = hashSetOf(),
                  override val semanticConstraints: HashSet<out SemanticConstraint> = hashSetOf(),
                  val length: Int = 1) : PatternElement {

  internal constructor(vararg wordConstraint: WordConstraint, length : Int = 1)
  : this(wordConstraint.toHashSet(), length = length)

  internal constructor(vararg words: String, length : Int = 1)
  : this(words.map{WordConstraint(it)}.toHashSet(), length = length)

  private val expandedForm = PatternListExpandedForm(this)

  fun expandedForm() = this.expandedForm.invoke()
}
