/*
 *    Copyright 2016 Uriel Avalos
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.frankandrobot.rapier

import com.frankandrobot.rapier.meta.*
import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.nlp.WordToken
import com.frankandrobot.rapier.parse.ParsePatternItemList
import com.frankandrobot.rapier.parse.ParseResult
import com.frankandrobot.rapier.pattern.*
import com.frankandrobot.rapier.rule.BaseRule
import com.frankandrobot.rapier.rule.IRule
import com.frankandrobot.rapier.util.BetterIterator
import org.funktionale.option.Option
import org.funktionale.option.Option.None
import org.funktionale.option.Option.Some
import java.util.*


fun tokens(vararg words : String) = words.map(::token) as ArrayList
fun token(word : String) = Token(
  word = Some(word),
  posTag = None,
  semanticClass = None,
  startIndex = None,
  endIndex = None
)
fun wordTokens(vararg words : String) = words.map{ WordToken(Some(it)) } as ArrayList
fun wordTagToken(word : String, tag : String) = Token(
  word = Some(word),
  posTag = Some(tag),
  semanticClass = None,
  startIndex = None,
  endIndex = None
)
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
fun toBaseRule(rule : IRule) = BaseRule(
  preFiller = rule.preFiller,
  filler = rule.filler,
  postFiller = rule.postFiller,
  slotName = rule.slotName
)


val emptyExample = Example(
  blankTemplate = BlankTemplate("", hashSetOf()),
  document = Document(),
  filledTemplate = FilledTemplate(Slots(hashMapOf()))
)
val emptyExamples = Examples(listOf(emptyExample))


fun parsePatternItemList(vararg word : String)
 = ParsePatternItemList(
  (ArrayList<PatternItem>() + word.map{PatternItem(words(it))}) as ArrayList<PatternItem>
)
fun parseResult(tokens : BetterIterator<Token>,
                index : Option<Int> = None,
                vararg matches : String) =
  ParseResult(
    tokens = tokens,
    index = index,
    matches = matches.map(::token) as ArrayList<Token>
  )
