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
    .map({ node -> wordTagToken(
      word = node.wordForm,
      tag = node.partOfSpeechTag,
      start = node.startOffset,
      end = node.endOffset - 1) })
    as ArrayList<Token>

fun tokenizeWords(string: String) =

  decoder.decode(string)
    .drop(1)
    .map({ node -> WordToken(Some(node.wordForm)) })
    as ArrayList<WordToken>
