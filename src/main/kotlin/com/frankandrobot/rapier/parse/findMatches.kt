package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.meta.Document
import com.frankandrobot.rapier.meta.SlotName
import com.frankandrobot.rapier.rule.IRule
import java.util.*


fun IRule.findMatches(doc : Document) : Pair<SlotName, List<String>> {
  assert(doc.raw.isDefined())
  val rawDoc = doc.raw.get()
  val matches = this.exactMatch(doc()).map{
    val tokens = it.fillerMatch().get()
    val start = tokens[0].startIndex.get()
    val end = tokens[tokens.size - 1].endIndex.get()
    val word = rawDoc.substring(IntRange(start, end))

    word
  }
  return Pair(this.slotName, matches.distinct())
}

fun List<IRule>.findMatches(doc : Document) : HashMap<SlotName,List<String>> {
  assert(doc.raw.isDefined())
  return this
    .map{it.findMatches(doc)}
    .fold(HashMap<SlotName,List<String>>()) { total, curMatches ->
      val prev = total.getOrDefault(curMatches.first, emptyList())
      total[curMatches.first] = (prev + curMatches.second).distinct()
      total
    }
}
