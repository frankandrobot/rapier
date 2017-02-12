package com.frankandrobot.rapier.nlp.jwi

import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek

import org.junit.Assert.*
import java.io.File


class FindFirstCommonSemanticClassSpec : Spek({
  describe("findFirstCommonSemanticClass") {
    val userDir = System.getProperty("user.dir")
    val s = File.separator
    val path = "$userDir${s}src${s}main${s}resources${s}wordnet${s}dict"
    val dict = load(path)

    it("should work for woman/man") {
      val result = dict.findFirstCommonSemanticClass("woman", "man")
        .get().words.map{it.lemma}.distinct()
      result shouldEqual listOf("adult", "grownup")
    }

    it("should work for man/world") {
      val result = dict.findFirstCommonSemanticClass("man", "world")
        .get().words.map{it.lemma}.distinct()
      result shouldEqual listOf("world", "human_race", "humanity", "humankind",
        "human_beings", "humans", "mankind", "man")
    }

    it("should work for woman/rock") {
      val result = dict.findFirstCommonSemanticClass("man", "entity")
        .get().words.map{it.lemma}.distinct()
//      result shouldEqual listOf("person", "individual", "someone", "somebody", "mortal",
//        "soul")
      println(result)
      SemanticClassIterator(dict, "entity").forEach { println(it.flatMap { it.words }
        .map{it.lemma}) }
//      println("---")
//      SemanticClassIterator(dict, "rock").forEach { println(it.flatMap { it.words }
//        .map{it.lemma}) }
//      println("----")
//      println(result)
    }
  }
})
