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

import com.frankandrobot.rapier.wordTokens
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class TokenizeSpec : Spek({

 // val text = """If you can't explain it simply, you don't understand it well enough."""

  describe("#tokenize") {
    it("should tokenize contractions and punctuations correctly") {

      val text = "can't explain, you!"
      var result = tokenize(text).map{ it.word.get() }

      assertEquals(listOf("ca", "n't", "explain", ",", "you", "!"), result)
    }

    it("should tokenize URLs correctly") {

      val text = "http://foobar.com is good"
      val result = tokenize(text).map{ it.word.get() }

      assertEquals(listOf("http://foobar.com", "is", "good"), result)
    }

    it("should tokenize hastags correctly") {

      val text = "explain #simply"
      val result = tokenize(text).map{ it.word.get() }

      assertEquals(listOf("explain", "#simply"), result)
    }
  }


  describe("#tokenizeWords") {
    it("should tokenize contractions and punctuations correctly") {

      val text = "can't explain, you!"
      var result = tokenizeWords(text)

      result shouldEqual wordTokens("ca", "n't", "explain", ",", "you", "!")
    }

    it("should tokenize URLs correctly") {

      val text = "http://foobar.com is good"
      val result = tokenizeWords(text)

      result shouldEqual wordTokens("http://foobar.com", "is", "good")
    }

    it("should tokenize hastags correctly") {

      val text = "explain #simply"
      val result = tokenizeWords(text)

      result shouldEqual wordTokens("explain", "#simply")
    }
  }
})
