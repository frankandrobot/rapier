package com.frankandrobot.rapier

import com.frankandrobot.rapier.document.Document
import com.frankandrobot.rapier.nlp.tokenize
import com.frankandrobot.rapier.pattern.*
import com.frankandrobot.rapier.template.Slot
import com.frankandrobot.rapier.template.SlotFiller


/**
 * Create a rule list for the Document for the given slot.
 */
fun initialRuleBase(slot: Pair<Slot, SlotFiller>, document: Document): List<IRule> {

  val doc = document.value
  val slotName = slot.first
  val slotFiller = slot.second

  var startIndex = 0
  var nextSlotFillerIndex = { doc.indexOf(slotFiller.value, startIndex) }
  var index = nextSlotFillerIndex()

  val rules = mutableListOf<IRule>()

  while (index >= 0) {

    val preFiller = doc.substring(0, index)
    val postFiller = doc.substring(index + slotFiller.value.length)

    rules.add(mostSpecificRule(preFiller, slotFiller.value, postFiller, slotName))

    startIndex = index + 1
    index = nextSlotFillerIndex()
  }

  return rules
}


/**
 * Initial rule list has no semantic constraints and no pattern lists.
 */
internal fun mostSpecificRule(preFiller: String, filler: String, postFiller: String, slot : Slot): IRule {

  val preFillerTokens = tokenize(preFiller)
  val fillerTokens = tokenize(filler)
  val postFillerTokens = tokenize(postFiller)

  val preFillerPatterns = preFillerTokens.map {
    PatternItem(words(it.word), tags(it.posTag))
  }
  val fillerPatterns = fillerTokens.map {
    PatternItem(words(it.word), tags(it.posTag))
  }
  val postFillerPatterns = postFillerTokens.map {
    PatternItem(words(it.word), tags(it.posTag))
  }

  return MostSpecificRule(
    preFiller = Pattern(preFillerPatterns),
    filler = Pattern(fillerPatterns),
    postFiller = Pattern(postFillerPatterns),
    slot = slot
  )
}
