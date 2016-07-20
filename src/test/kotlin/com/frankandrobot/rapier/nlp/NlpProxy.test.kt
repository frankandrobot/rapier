package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.nlp.tokenize
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class NlpProxyTest : Spek({

 // val text = """If you can't explain it simply, you don't understand it well enough."""

  describe("#tokenize") {
    it("should tokenize contractions and punctuations correctly") {

      val text = "can't explain, you!"
      var result = tokenize(text).map{ it.word }

      assertEquals(listOf("ca", "n't", "explain", ",", "you", "!"), result)
    }

    it("should tokenize URLs correctly") {

      val text = "http://foobar.com is good"
      val result = tokenize(text).map{ it.word }

      assertEquals(listOf("http://foobar.com", "is", "good"), result)
    }

    it("should tokenize hastags correctly") {

      val text = "explain #simply"
      val result = tokenize(text).map{ it.word }

      assertEquals(listOf("explain", "#simply"), result)
    }
  }
})
