package com.frankandrobot.rapier.pattern


/**
 * Rapier allows 3 kinds of constraints:
 * - words the element can match,
 * - part-of-speech tags
 * - semenatic classes - we use WordNet synsets
 */
abstract class PatternElement(open val word : List<WordConstraint> = listOf(),
                              open val syntactic : List<SyntacticConstraint> = listOf(),
                              open val semantic : List<SemanticConstraint> = listOf())

class PatternItem(override val word : List<WordConstraint> = listOf(),
                  override val syntactic : List<SyntacticConstraint> = listOf(),
                  override val semantic : List<SemanticConstraint> = listOf()) :
        PatternElement(word, syntactic, semantic)

class PatternList(override val word : List<WordConstraint> = listOf(),
                  override val syntactic : List<SyntacticConstraint> = listOf(),
                  override val semantic : List<SemanticConstraint> = listOf(),
                  val length : Int = 1) :
        PatternElement(word, syntactic, semantic)
