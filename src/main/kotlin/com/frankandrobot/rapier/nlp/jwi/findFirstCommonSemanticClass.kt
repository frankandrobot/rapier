package com.frankandrobot.rapier.nlp.jwi

import edu.mit.jwi.IDictionary
import edu.mit.jwi.item.ISynset
import org.funktionale.option.Option


fun IDictionary.findFirstCommonSemanticClass(lemma1 : String, lemma2 : String)
  : Option<ISynset> {
  val iter1 = SemanticClassIterator(this, lemma1)
  val iter2 = SemanticClassIterator(this, lemma2)

  while(iter1.hasNext() && iter2.hasNext()) {
    val classes1 = iter1.next()
    val classes2 = iter2.next()

    for(semClass in classes2) {
      if (classes1.contains(semClass)) {
        return Option.Some(semClass)
      }
    }
  }

  return Option.None
}
