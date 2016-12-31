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

package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.meta.Example
import com.frankandrobot.rapier.meta.Examples
import com.frankandrobot.rapier.meta.SlotFiller
import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.rule.IRule
import org.funktionale.memoization.memoize
import java.util.*



data class FillerMatchResults(val positives : List<SlotFiller>,
                              val negatives : List<SlotFiller>)


/**
 * Matches the rule against the example. If a filler occurs in the Example
 * enabledSlotFiller, then it is a positive match. Otherwise, it is a negative match.
 */
private val _getMatchedFillers = { rule: IRule, example: Example ->

  val slot = example[rule.slotName]

  if (slot.enabled) {
    val doc = example.document()
    val matchResults = rule.exactMatch(doc)
    val fillerMatchResults = matchResults.map { it.fillerMatch }.filter { it.isDefined() }
    // put the match results in a SlotFiller
    val slotFillers =
      fillerMatchResults
        .map { it.get().map(Token::dropTagAndSemanticProperties) }
        .map { SlotFiller(tokens = it as ArrayList) }

    val positives = slotFillers.filter { slot.slotFillers.contains(it) }
    val negatives = slotFillers.filter { !slot.slotFillers.contains(it) }

    FillerMatchResults(positives = positives, negatives = negatives)
  }
  else {

    FillerMatchResults(positives = emptyList(), negatives = emptyList())
  }
}.memoize()


internal fun getMatchedFillers(rule : IRule, example : Example) : FillerMatchResults
  = _getMatchedFillers(rule, example)


private val _ruleGetMatchedFillers = { rule : IRule, examples : Examples ->
  examples()
    .map{ example -> getMatchedFillers(rule, example) }
    .fold(FillerMatchResults(emptyList(), emptyList())) { total, cur ->
      FillerMatchResults(
        positives = total.positives + cur.positives,
        negatives = total.negatives + cur.negatives
      )
    }
}.memoize()


fun IRule.getMatchedFillers(examples : Examples) : FillerMatchResults
  = _ruleGetMatchedFillers(this, examples)
