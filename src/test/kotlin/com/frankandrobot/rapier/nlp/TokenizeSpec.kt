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
