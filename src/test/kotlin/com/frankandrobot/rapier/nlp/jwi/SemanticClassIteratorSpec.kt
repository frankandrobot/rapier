package com.frankandrobot.rapier.nlp.jwi

import edu.mit.jwi.item.POS
import org.amshove.kluent.shouldEqual
import org.funktionale.option.Option.Some
import org.jetbrains.spek.api.Spek
import java.io.File


class SemanticClassIteratorSpec : Spek({
  describe("SemanticClassIterator") {
    val userDir = System.getProperty("user.dir")
    val s = File.separator
    val path = "$userDir${s}src${s}main${s}resources${s}wordnet${s}dict"
    val dict = load(path)
    var iterator = SemanticClassIterator(dict, "woman", Some(POS.NOUN))

    beforeEach {
      iterator = SemanticClassIterator(dict, "woman", Some(POS.NOUN))
    }

    describe("initial semantic classes") {
      it("should haveNext") {
        iterator.hasNext() shouldEqual true
      }

      it("should exist") {
        val initial = iterator.next()
        val words = initial.flatMap { it.words }.map{it.lemma}.distinct()
        val expected = listOf("womanhood", "woman", "fair_sex", "adult_female",
          "charwoman", "char", "cleaning_woman", "cleaning_lady")
        words.toHashSet() shouldEqual expected.toHashSet()
      }

      it("should exist after calling haveNext twice") {
        iterator.hasNext() shouldEqual true
        iterator.hasNext() shouldEqual true
        val initial = iterator.next()
        val words = initial.flatMap { it.words }.map{it.lemma}.distinct()
        val expected = listOf("womanhood", "woman", "fair_sex", "adult_female",
          "charwoman", "char", "cleaning_woman", "cleaning_lady")
        words.toHashSet() shouldEqual expected.toHashSet()
      }
    }

    describe("non-initial semantic classes") {
      beforeEach {
        iterator.next()
      }

      it("should haveNext") {
        iterator.hasNext() shouldEqual true
      }

      it("should exist") {
        val initial = iterator.next()
        val words = initial.flatMap { it.words }.map{it.lemma}.distinct()
        val expected = listOf("class", "stratum", "social_class",
        "socio-economic_class", "female", "female_person", "cleaner", "adult",
        "grownup")
        words.toHashSet() shouldEqual expected.toHashSet()
      }

      it("should exist after calling haveNext twice") {
        iterator.hasNext() shouldEqual true
        iterator.hasNext() shouldEqual true
        val initial = iterator.next()
        val words = initial.flatMap { it.words }.map{it.lemma}.distinct()
        val expected = listOf("class", "stratum", "social_class",
          "socio-economic_class", "female", "female_person", "cleaner", "adult",
          "grownup")
        words.toHashSet() shouldEqual expected.toHashSet()
      }
    }
  }
})
