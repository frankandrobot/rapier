package com.frankandrobot.rapier.pattern


/**
 * Rapier allows 3 kinds of constraints:
 * - words the element can match,
 * - part-of-speech tags
 * - semenatic classes - we use WordNet synsets
 */
abstract class PatternElement(open val wordConstraints: List<WordConstraint> = listOf(),
                              open val syntacticContraints: List<SyntacticConstraint> = listOf(),
                              open val semanticConstraints: List<SemanticConstraint> = listOf())

class PatternItem(override val wordConstraints: List<WordConstraint> = listOf(),
                  override val syntacticContraints: List<SyntacticConstraint> = listOf(),
                  override val semanticConstraints: List<SemanticConstraint> = listOf()) :
  PatternElement(wordConstraints, syntacticContraints, semanticConstraints)

class PatternList(override val wordConstraints: List<WordConstraint> = listOf(),
                  override val syntacticContraints: List<SyntacticConstraint> = listOf(),
                  override val semanticConstraints: List<SemanticConstraint> = listOf(),
                  val length: Int = 1) :
  PatternElement(wordConstraints, syntacticContraints, semanticConstraints)
