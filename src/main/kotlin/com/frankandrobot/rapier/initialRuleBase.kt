package com.frankandrobot.rapier

import com.frankandrobot.rapier.document.Document
import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.nlp.tokenize
import com.frankandrobot.rapier.pattern.*
import com.frankandrobot.rapier.template.Slot
import com.frankandrobot.rapier.template.SlotFiller


/**
 * Create a rule list for the Document for the given slot.
 */
fun initialRuleBase(slot: Pair<Slot, SlotFiller>, document: Document): List<Rule> {

  val doc = document.value
  val slotName = slot.first
  val filler = slot.second

  var startIndex = 0
  var _index = { doc.indexOf(filler.value, startIndex) }
  var index = _index()

  val rules = mutableListOf<Rule>()

  while (index >= 0) {

    val preFiller = doc.substring(0, index)
    val postFiller = doc.substring(index + filler.value.length)

    rules.add(_initialRule(preFiller, filler.value, postFiller, slotName))

    startIndex = index + 1
    index = _index()
  }

  return rules
}

/**
 * Initial rule list has no semantic constraints and no pattern lists.
 */
internal fun _initialRule(preFiller: String, filler: String, postFiller: String, slot : Slot): Rule {

  val preFillerTokens = tokenize(preFiller)
  val fillerTokens = tokenize(filler)
  val postFillerTokens = tokenize(postFiller)

  val preFillerPatterns = preFillerTokens.map { _pattern(it) }
  val fillerPatterns = fillerTokens.map { _pattern(it) }
  val postFillerPatterns = postFillerTokens.map { _pattern(it) }

  return Rule(
    preFiller = Pattern(preFillerPatterns),
    filler = Pattern(fillerPatterns),
    postFiller = Pattern(postFillerPatterns),
    slot = slot
  )
}

internal fun _pattern(token: Token) = PatternItem(
  listOf(WordConstraint(token.word)), listOf(PosTagConstraint(token.posTag))
)
