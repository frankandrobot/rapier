package com.frankandrobot.rapier.util

import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.nlp.WordToken
import com.frankandrobot.rapier.tokens
import com.frankandrobot.rapier.wordTokens
import org.jetbrains.spek.api.Spek
import java.util.*
import kotlin.test.assertEquals


class IndexOfWordsSpec : Spek({

  describe("#indexOfWords") {

    describe("edge cases") {

      it("should handle the case when the array list is empty") {
        val empty = ArrayList<Token>()
        val searchList = wordTokens("hello")
        val result = empty.indexOfWords(searchList)
        assertEquals(-1, result)
      }

      it("should handle the case when search list is empty") {
        val empty = tokens("hello")
        val searchList = ArrayList<WordToken>()
        val result = empty.indexOfWords(searchList)
        assertEquals(-1, result)
      }
    }

    describe("boundary") {

      it("should find a single item at start") {
        val list = tokens("hello", "world")
        val searchList = wordTokens("hello")
        val result = list.indexOfWords(searchList)
        assertEquals(0, result)
      }

      it("should find a single item at end") {
        val list = tokens("hello", "world")
        val searchList = wordTokens("world")
        val result = list.indexOfWords(searchList)
        assertEquals(1, result)
      }

      it("should find a list at start") {
        val list = tokens("a", "b", "c")
        val searchList = wordTokens("a", "b")
        val result = list.indexOfWords(searchList)
        assertEquals(0, result)
      }

      it("should find a list at end") {
        val list = tokens("a", "b", "c")
        val searchList = wordTokens("b", "c")
        val result = list.indexOfWords(searchList)
        assertEquals(1, result)
      }

      it("should not find match when search list is cutoff") {
        val list = tokens("a", "b", "c")
        val searchList = wordTokens("c", "d")
        val result = list.indexOfWords(searchList)
        assertEquals(-1, result)
      }
    }

    it("should find a list") {
      val list = tokens("a", "b", "c", "d")
      val searchList = wordTokens("b", "c")
      val result = list.indexOfWords(searchList)
      assertEquals(1, result)
    }

    it("should find match from start index") {
      val list = tokens("a", "b", "a", "b")
      val searchList = wordTokens("a", "b")
      val result = list.indexOfWords(searchList, start = 1)
      assertEquals(2, result)
    }

//    it("should match against words only") {
//      val list = arrayListOf(wordTagToken("a", "1"))
//      val searchList = arrayListOf(wordTagToken("a", "2"))
//      val result = list.indexOfWords(searchList)
//      assertEquals(0, result)
//    }
  }
})
