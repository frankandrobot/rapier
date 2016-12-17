package com.frankandrobot.rapier

import com.frankandrobot.rapier.meta.*
import com.frankandrobot.rapier.nlp.wordToken
import com.frankandrobot.rapier.nlp.wordTokens
import com.frankandrobot.rapier.pattern.*
import com.frankandrobot.rapier.util.BetterIterator
import java.util.*


/**
 * @deprecated use wordTokens
 */
fun textTokenList(vararg text : String) = text.flatMap{it.split(" ")}.map(::wordToken)
  as ArrayList

fun textTokenIterator(text : String, start : Int = 0) =
  BetterIterator(textTokenList(text), start)


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


val emptyRule = BaseRule(slot = dummySlot("none"))

val emptyExample = Example(
  blankTemplate = BlankTemplate("", hashSetOf()),
  document = Document(),
  filledTemplate = FilledTemplate(hashMapOf())
)
