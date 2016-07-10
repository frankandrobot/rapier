package com.frankandrobot.rapier

import com.frankandrobot.rapier.document.Document
import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.nlp.tokenize
import com.frankandrobot.rapier.pattern.*
import com.frankandrobot.rapier.template.Slot


/**
 * Create a rule list for the Document for the given slot.
 */
fun initialRuleBase(slot: Slot, document: Document): List<Rule> {

  val doc = document.value
  val filler = slot.value

  var startIndex = 0
  var _index = { doc.indexOf(filler, startIndex) }
  var index = _index()

  val rules = mutableListOf<Rule>()

  while (index >= 0) {

    val preFiller = doc.substring(0, index)
    val postFiller = doc.substring(index + filler.length)

    rules.add(_initialRule(preFiller, filler, postFiller))

    startIndex = index + 1
    index = _index()
  }

  return rules
}

/**
 * Initial rule list has no semantic constraints and no pattern lists.
 */
internal fun _initialRule(preFiller: String, filler: String, postFiller: String): Rule {

  val preFillerTokens = tokenize(preFiller)
  val fillerTokens = tokenize(filler)
  val postFillerTokens = tokenize(postFiller)

  val preFillerPatterns = preFillerTokens.map { _pattern(it) }
  val fillerPatterns = fillerTokens.map { _pattern(it) }
  val postFillerPatterns = postFillerTokens.map { _pattern(it) }

  return Rule(preFiller = Pattern(preFillerPatterns),
    filler = Pattern(fillerPatterns),
    postFiller = Pattern(postFillerPatterns))
}

internal fun _pattern(token: Token) = PatternItem(
  listOf(WordConstraint(token.word)), listOf(SyntacticConstraint(token.posTag))
)
