package com.frankandrobot.rapier.nlp.jwi

import edu.mit.jwi.IDictionary
import edu.mit.jwi.item.ISynset
import edu.mit.jwi.item.POS
import edu.mit.jwi.item.Pointer
import org.funktionale.option.Option
import org.funktionale.option.Option.*
import java.util.*


private val posList = listOf(POS.ADJECTIVE, POS.ADVERB, POS.NOUN, POS.VERB)

/**
 * Iterates thru the semantic class heiarachy one level at a time i.e.,
 * #next returns the next level of semantic classes.
 */
data class SemanticClassIterator(val dict : IDictionary, val word : String) :
  Iterator<HashSet<ISynset>> {

  private var calculatedNext = false
  private var next : Option<HashSet<ISynset>> = None

  private fun initialSemanticClasses() : HashSet<ISynset> {
    val idxWords = posList
      .map{dict.getIndexWord(word, it)}
      .filter{it != null}
      .map{it!!}
    val synsets = idxWords
      .flatMap{it.wordIDs}
      .map{dict.getWord(it)}
      .map{it.synset}
      .distinct()
      .toHashSet()

    return synsets
  }

  private fun next(current : Option<HashSet<ISynset>>) =
    if (current.isEmpty()) Some(initialSemanticClasses())
    else {
      val result = current.get()
        .flatMap{it.getRelatedSynsets (Pointer.HYPERNYM)}
        .distinct()
        .map{dict.getSynset(it)}
        .toHashSet()

      if (result.size > 0) Some(result)
      else None
    }

  override fun hasNext(): Boolean {
    if (!calculatedNext) {
      next = next(next)
      calculatedNext = true
    }
    return next.isDefined()
  }

  override fun next(): HashSet<ISynset> {
    if (!calculatedNext) {
      next = next(next)
    }
    calculatedNext = false
    return next.get()
  }
}
