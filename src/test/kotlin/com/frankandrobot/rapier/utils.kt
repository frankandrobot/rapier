package com.frankandrobot.rapier

import com.frankandrobot.rapier.meta.Slot
import com.frankandrobot.rapier.meta.SlotFiller
import com.frankandrobot.rapier.meta.SlotName
import com.frankandrobot.rapier.nlp.wordToken
import com.frankandrobot.rapier.nlp.wordTokens
import com.frankandrobot.rapier.pattern.*
import com.frankandrobot.rapier.util.BetterIterator
import java.util.*


/**
 * @deprecated use wordTokens
 */
fun textToTokenList(vararg text : String) = text.flatMap{it.split(" ")}.map(::wordToken)

fun textToTokenIterator(text : String, start : Int = 0) =
  BetterIterator(textToTokenList(text) as ArrayList, start)


fun dummySlot(name : String) = Slot(SlotName(name), slotFillers = HashSet<SlotFiller>())

fun wordSlotFiller(vararg filler : String) = SlotFiller(tokens = wordTokens(*filler))


fun patternItemOfWords(vararg words : String) =
  PatternItem(wordConstraints = words.map(::WordConstraint).toHashSet())


fun emptyBaseRule() = BaseRule(
  preFiller = Pattern(),
  filler = Pattern(),
  postFiller = Pattern(),
  slot = Slot(SlotName("none"), slotFillers = HashSet<SlotFiller>())
)


fun patternOfWordItems(vararg words : String) =
  Pattern(words.map { PatternItem(words(it)) })

fun patternOfWordsList(length : Int = 1, vararg word : String) =
  Pattern(PatternList(length = length, wordConstraints = words(*word)))
