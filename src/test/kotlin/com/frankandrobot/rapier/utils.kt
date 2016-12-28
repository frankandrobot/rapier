package com.frankandrobot.rapier

import com.frankandrobot.rapier.meta.*
import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.nlp.WordToken
import com.frankandrobot.rapier.parse.ParseResult
import com.frankandrobot.rapier.pattern.*
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
  filledTemplate = FilledTemplate(Slots(hashMapOf()))
)


fun parseResult(tokens : BetterIterator<Token>,
                matchFound : Boolean = true,
                vararg matches : String) =
  ParseResult(
    _tokens = tokens,
    matchFound = matchFound,
    matches = matches.map{Some(token(it))} as ArrayList<Option<Token>>
  )
