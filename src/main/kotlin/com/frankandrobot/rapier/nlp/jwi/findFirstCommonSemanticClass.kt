package com.frankandrobot.rapier.nlp.jwi

import edu.mit.jwi.IDictionary
import edu.mit.jwi.item.ISynset
import edu.mit.jwi.item.ISynsetID
import org.funktionale.option.Option
import org.funktionale.option.Option.None
import org.funktionale.option.Option.Some
import java.util.*


fun IDictionary.findFirstCommonSemanticClass(lemma1 : String, lemma2 : String)
  : Option<ISynset> {
  // first get all synsets
  val iter1Synsets = ArrayList<ISynsetID>(20)
  val iter2Synsets = ArrayList<ISynsetID>(20)
  val iter1 = SemanticClassIterator(this, lemma1)
  val iter2 = SemanticClassIterator(this, lemma2)

  iter1.forEach{iter1Synsets.addAll(it.map{it.id})}
  iter2.forEach{iter2Synsets.addAll(it.map{it.id})}

  // now find first match
  for(id1 in iter1Synsets)
    for(id2 in iter2Synsets)
      if (id1.equals(id2)) { return Some(this.getSynset(id1)) }

  return None
}
