package com.frankandrobot.rapier.nlp

import edu.emory.mathcs.nlp.common.util.IOUtils
import edu.emory.mathcs.nlp.decode.DecodeConfig
import edu.emory.mathcs.nlp.decode.NLPDecoder


val configUri = "src/main/resources/nlp4j/config.xml"
val config = DecodeConfig(IOUtils.createFileInputStream(configUri))
val decoder = NLPDecoder(config)


data class Token(val word: String, val posTag: String)


fun tokenize(string: String): List<Token> {

  return decoder.decode(string)
    .foldIndexed(mutableListOf<Token>()) { i, tokens, node ->

      if (i != 0) {
        tokens.add(Token(node.wordForm, node.partOfSpeechTag))
      }
      tokens
    }
}
