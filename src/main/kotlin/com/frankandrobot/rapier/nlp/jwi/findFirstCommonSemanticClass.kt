package com.frankandrobot.rapier.nlp.jwi

import com.frankandrobot.rapier.nlp.Token
import edu.mit.jwi.IDictionary
import edu.mit.jwi.item.ISynset
import edu.mit.jwi.item.ISynsetID
import edu.mit.jwi.item.POS
import org.funktionale.option.Option
import org.funktionale.option.Option.None
import org.funktionale.option.Option.Some
import java.util.*


fun toJWIPOS(posTag : String) =
  when(posTag) {
    "NN", "NNS", "NNP", "NNPS", "PRP", "PRP$", "WP", "WP$" -> Some(POS.NOUN)
    "VB", "VBD", "VBG", "VBN", "VBP", "VBZ" -> Some(POS.VERB)
    "JJ", "JJR", "JJS" -> Some(POS.ADJECTIVE)
    "RB", "RBR", "RBS", "WRB" -> Some(POS.ADVERB)
    else -> None
  }

fun IDictionary.findFirstCommonSemanticClass(word1 : Token, word2 : Token)
  : Option<ISynset> {

  assert(word1.lemma.isDefined())
  assert(word1.posTag.isDefined())
  assert(word2.lemma.isDefined())
  assert(word2.posTag.isDefined())

  // first get all synsets
  val iter1Synsets = ArrayList<ISynsetID>(20)
  val iter2Synsets = ArrayList<ISynsetID>(20)
  val iter1 = SemanticClassIterator(
    this,
    lemma = word1.lemma.get(),
    pos = toJWIPOS(word1.posTag.get())
  )
  val iter2 = SemanticClassIterator(
    this,
    lemma = word2.lemma.get(),
    pos = toJWIPOS(word2.posTag.get())
  )

  iter1.forEach{iter1Synsets.addAll(it.map{it.id})}
  iter2.forEach{iter2Synsets.addAll(it.map{it.id})}

  // now find first match
  for(id1 in iter1Synsets)
    for(id2 in iter2Synsets)
      if (id1.equals(id2)) { return Some(this.getSynset(id1)) }

  return None
}
