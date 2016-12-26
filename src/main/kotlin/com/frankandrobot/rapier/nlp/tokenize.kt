package com.frankandrobot.rapier.nlp

import edu.emory.mathcs.nlp.common.util.IOUtils
import edu.emory.mathcs.nlp.decode.DecodeConfig
import edu.emory.mathcs.nlp.decode.NLPDecoder
import org.funktionale.option.Option.Some
import java.util.*


private val configUri = "src/main/resources/nlp4j/config.xml"
private val config = DecodeConfig(IOUtils.createFileInputStream(configUri))
private val decoder = NLPDecoder(config)


fun tokenize(string: String) =

  decoder.decode(string)
    .drop(1)
    .map({ node -> wordTagToken(node.wordForm, node.partOfSpeechTag) })
    as ArrayList<Token>

fun tokenizeWords(string: String) =

  decoder.decode(string)
    .drop(1)
    .map({ node -> WordToken(Some(node.wordForm)) })
    as ArrayList<WordToken>
