package com.frankandrobot.rapier.pattern

import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.parse.ParsePatternItemList
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
  val posTagConstraints: HashSet<out PosTagConstraint>
  val semanticConstraints: HashSet<out SemanticConstraint>
  val length: Int
}


data class PatternItem(override val wordConstraints: HashSet<out WordConstraint> = hashSetOf(),
                       override val posTagConstraints: HashSet<out PosTagConstraint> = hashSetOf(),
                       override val semanticConstraints: HashSet<out SemanticConstraint> = hashSetOf())
: PatternElement {

  override val length = 1

  constructor(token : Token)
  : this(
    wordConstraints = hashSetOf(WordConstraint(token.word))
      .filter{ it.value.isDefined() }
      .toHashSet(),
    posTagConstraints = hashSetOf(PosTagConstraint(token.posTag))
      .filter{ it.value.isDefined() }
      .toHashSet(),
    semanticConstraints = hashSetOf(SemanticConstraint(token.semanticClass))
      .filter{ it.value.isDefined() }
      .toHashSet()
  )

  internal constructor(vararg wordConstraint: WordConstraint)
  : this(HashSet<WordConstraint>().plus(wordConstraint) as HashSet<out WordConstraint>)

  internal constructor(words: List<String>, tags: List<String>)
  : this(
    words.map(::WordConstraint).toHashSet(),
    tags.map(::PosTagConstraint).toHashSet()
  )


  /**
   * Does the token satisfy all the constraints?
   *
   * Note that PatternList has no #test method because it gets turned into a
   * ParsePatternItemList (i.e., a list of PatternItems), so that it uses this method.
   */
  fun test(token: Token) : Boolean {

    return (wordConstraints.size === 0 || wordConstraints.any{ it.satisfies(token) }) &&
      (posTagConstraints.size === 0 || posTagConstraints.any{ it.satisfies(token) }) &&
      (semanticConstraints.size === 0 || semanticConstraints.any{ it.satisfies(token) })
  }

  override fun toString() = "word: $wordConstraints, tag: $posTagConstraints, " +
    "semantic: $semanticConstraints"
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


  private val _expandedForm = PatternListExpandedForm(this)

  val expandedForm : ArrayList<ParsePatternItemList>
    get() = _expandedForm.invoke()

  override fun toString() = "list: max length: $length, word: $wordConstraints, tag: " +
    "$posTagConstraints, semantic: $semanticConstraints"
}


internal fun patternItemOfWords(vararg words : String) =
  PatternItem(wordConstraints = words.map(::WordConstraint).toHashSet())
