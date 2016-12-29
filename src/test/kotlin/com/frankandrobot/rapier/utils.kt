package com.frankandrobot.rapier

import com.frankandrobot.rapier.meta.*
import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.nlp.WordToken
import com.frankandrobot.rapier.parse.ParseResult
import com.frankandrobot.rapier.pattern.*
import com.frankandrobot.rapier.rule.BaseRule
import com.frankandrobot.rapier.util.BetterIterator
import org.funktionale.option.Option
import org.funktionale.option.Option.None
import org.funktionale.option.Option.Some
import java.util.*


fun tokens(vararg words : String) = words.map(::token) as ArrayList
fun token(word : String) = Token(
  word = Some(word),
  posTag = None,
  semanticClass = None
)
fun wordTokens(vararg words : String) = words.map{ WordToken(Some(it)) } as ArrayList
/**
 * @deprecated use tokens
 */
fun textTokenList(vararg text : String) = text.flatMap{it.split(" ")}.map(::token)
  as ArrayList
fun textTokenIterator(text : String, start : Int = 0) =
  BetterIterator(textTokenList(text), start)


fun dummySlotName(name : String) = SlotName(name)
fun wordSlotFiller(vararg filler : String) = SlotFiller(tokens = wordTokens(*filler))
fun slots(vararg slot : Slot) =
  Slots(
    slot.fold(HashMap<SlotName, Slot>()) { total, slot ->
      total[slot.name] = slot; total
    }
  )


fun patternItemOfWords(vararg words : String) =
  PatternItem(wordConstraints = words.map(::WordConstraint).toHashSet())
fun patternOfWordItems(vararg words : String) =
  Pattern(words.map { PatternItem(words(it)) })
fun patternOfWordsList(length : Int = 1, vararg word : String) =
  Pattern(PatternList(length = length, wordConstraints = words(*word)))


val emptyRule = BaseRule(slotName = dummySlotName("none"))
fun emptyBaseRule() = BaseRule(
  preFiller = Pattern(),
  filler = Pattern(),
  postFiller = Pattern(),
  slotName = SlotName("none")
)


val emptyExample = Example(
  blankTemplate = BlankTemplate("", hashSetOf()),
  document = Document(),
  filledTemplate = FilledTemplate(Slots(hashMapOf()))
)
val emptyExamples = Examples(listOf(emptyExample))


fun parseResult(tokens : BetterIterator<Token>,
                matchFound : Boolean = true,
                vararg matches : String) =
  ParseResult(
    _tokens = tokens,
    matchFound = matchFound,
    matches = matches.map{Some(token(it))} as ArrayList<Option<Token>>
  )
