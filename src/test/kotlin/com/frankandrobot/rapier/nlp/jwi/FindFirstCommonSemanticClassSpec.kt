package com.frankandrobot.rapier.nlp.jwi

import com.frankandrobot.rapier.lemmaToken
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import java.io.File


class FindFirstCommonSemanticClassSpec : Spek({
  describe("findFirstCommonSemanticClass") {
    val userDir = System.getProperty("user.dir")
    val s = File.separator
    val path = "$userDir${s}src${s}main${s}resources${s}wordnet${s}dict"
    val dict = load(path)

    it("should work for woman/man") {
      val result = dict.findFirstCommonSemanticClass(
        lemmaToken("woman", "NN"),
        lemmaToken("man", "NN")
      ).get().words.map{it.lemma}.distinct()
      result shouldEqual listOf("adult", "grownup")
    }

    fit("should work for man/world") {
      val result = dict.findFirstCommonSemanticClass(
        lemmaToken("man", "NN"),
        lemmaToken("world", "NN")
      ).get().words.map{it.lemma}.distinct()
      result shouldEqual listOf("world", "human_race", "humanity", "humankind",
        "human_beings", "humans", "mankind", "man")
    }

    it("should work for man/rock") {
      val result = dict.findFirstCommonSemanticClass(
        lemmaToken("man", "NN"),
        lemmaToken("rock", "NN")
      ).get().words.map{it.lemma}.distinct()
      result shouldEqual listOf("person", "individual", "someone", "somebody", "mortal",
        "soul")
    }

    it("should work for woman/entity") {
      val result = dict.findFirstCommonSemanticClass(
        lemmaToken("woman", "NN"),
        lemmaToken("entity", "NN")
      ).get().words.map{it.lemma}.distinct()
      result shouldEqual listOf("entity")
    }
  }
})
