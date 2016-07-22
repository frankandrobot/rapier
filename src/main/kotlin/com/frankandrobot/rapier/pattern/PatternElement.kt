package com.frankandrobot.rapier.pattern


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
  PatternElement(wordConstraints, posTagContraints, semanticConstraints)

class PatternList(override val wordConstraints: List<WordConstraint> = listOf(),
                  override val posTagContraints: List<PosTagConstraint> = listOf(),
                  override val semanticConstraints: List<SemanticConstraint> = listOf(),
                  val length: Int = 1) :
  PatternElement(wordConstraints, posTagContraints, semanticConstraints)
