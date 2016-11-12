package com.frankandrobot.rapier.pattern

import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.parse.PatternListExpandedForm
import java.util.*


fun words(vararg words : String) = words.map { WordConstraint(it) }.toHashSet()

fun tags(vararg tags : String) = tags.map{ PosTagConstraint(it) }.toHashSet()


/**
 * Rapier allows 3 kinds of constraints:
 * - words the element can match,
 * - part-of-speech tags
 * - semenatic classes - we use WordNet synsets
 */
interface PatternElement {

  val wordConstraints: HashSet<out WordConstraint>
  val posTagConstraints: HashSet<out PosTagConstraint>
  val semanticConstraints: HashSet<out SemanticConstraint>
  val length: Int
}


data class PatternItem(override val wordConstraints: HashSet<out WordConstraint> = hashSetOf(),
                       override val posTagConstraints: HashSet<out PosTagConstraint> = hashSetOf(),
                       override val semanticConstraints: HashSet<out SemanticConstraint> = hashSetOf())
: PatternElement {

  override val length = 1

  internal constructor(vararg wordConstraint: WordConstraint)
  : this(HashSet<WordConstraint>().plus(wordConstraint) as HashSet<out WordConstraint>)

  internal constructor(words: List<String>, tags: List<String>)
  : this(words.map{WordConstraint(it)}.toHashSet(), tags.map{PosTagConstraint(it)}.toHashSet())


  /**
   * Does the token satisfy all the constraints?
   */
  fun test(token: Token) : Boolean {

    return (wordConstraints.size === 0 || wordConstraints.any{ it.satisfies(token) }) &&
      (posTagConstraints.size === 0 || posTagConstraints.any{ it.satisfies(token) }) &&
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
                       override val posTagConstraints: HashSet<out PosTagConstraint> = hashSetOf(),
                       override val semanticConstraints: HashSet<out SemanticConstraint> = hashSetOf(),
                       override val length: Int) : PatternElement {

  internal constructor(vararg wordConstraint: WordConstraint, length : Int = 1)
  : this(wordConstraint.toHashSet(), length = length)


  private val expandedForm = PatternListExpandedForm(this)

  fun expandedForm() = this.expandedForm.invoke()
}
