package com.frankandrobot.rapier.nlp.jwi

import edu.mit.jwi.IDictionary
import edu.mit.jwi.item.ISynset
import edu.mit.jwi.item.POS
import edu.mit.jwi.item.Pointer
import java.util.*


private val posList = listOf(POS.ADJECTIVE, POS.ADVERB, POS.NOUN, POS.VERB)

/**
 * Iterates thru the semantic class heiarachy one level at a time i.e.,
 * #next returns the next level of semantic classes.
 */
data class SemanticClassIterator(val dict : IDictionary, val word : String) :
  Iterator<HashSet<ISynset>> {

  private val initialSemanticClasses : HashSet<ISynset> by lazy {
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

    synsets
  }

  private var initial = false
  private var curSynsets : HashSet<ISynset> = HashSet()

  override fun hasNext(): Boolean {
    return !initial || curSynsets.size > 0
  }

  override fun next(): HashSet<ISynset> {
    if (!initial) {
      initial = true
      curSynsets = initialSemanticClasses
    }
    else {
      curSynsets = curSynsets.flatMap{it.getRelatedSynsets (Pointer.HYPERNYM)}
        .distinct()
        .map{dict.getSynset(it)}
        .toHashSet()
    }
    return curSynsets
  }
}
