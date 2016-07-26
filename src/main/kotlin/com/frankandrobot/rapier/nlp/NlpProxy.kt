package com.frankandrobot.rapier.nlp

import edu.emory.mathcs.nlp.common.util.IOUtils
import edu.emory.mathcs.nlp.decode.DecodeConfig
import edu.emory.mathcs.nlp.decode.NLPDecoder


private val configUri = "src/main/resources/nlp4j/config.xml"
private val config = DecodeConfig(IOUtils.createFileInputStream(configUri))
private val decoder = NLPDecoder(config)


fun tokenize(string: String): List<Token> {

  return decoder.decode(string)
    .drop(1)
    .map({ node -> Token(node.wordForm, node.partOfSpeechTag, semanticClass = "") })
}
