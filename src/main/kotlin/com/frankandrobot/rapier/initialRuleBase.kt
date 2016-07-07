package com.frankandrobot.rapier

import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.Rule
import com.frankandrobot.rapier.pattern.WordConstraint
import com.frankandrobot.rapier.document.Document
import com.frankandrobot.rapier.template.Slot

import edu.emory.mathcs.nlp.tokenization.EnglishTokenizer
import edu.emory.mathcs.nlp.component.pos.POSTagger
import edu.emory.mathcs.nlp.component.template.node.NLPNode


fun initialRuleBase(slot : Slot, document: Document) : List<Rule> {

    val doc = document.value
    val filler = slot.value

    var startIndex = 0
    var _index = { doc.indexOf(filler, startIndex) }
    var index = _index()

    val rules = mutableListOf<Rule>()

    while( index >=0 ) {

        val preFiller = doc.substring(startIndex, index)
        val postFiller = doc.substring(index + filler.length)

        //rules.add(initialRule(preFiller, filler, postFiller))

        startIndex = index + 1
        index = _index()
    }

    return rules
}

fun initialRule(preFiller : String, filler : String, postFiller : String) : Rule {

    val tokenizer = EnglishTokenizer()

    val preFillerTokens = tokenizer.tokenize(preFiller).map{ it.toString() }
    val fillerTokens = tokenizer.tokenize(filler).map{ it.toString() }
    val postFillerTokens = tokenizer.tokenize(postFiller).map{ it.toString() }


    val preFillerPatterns = preFillerTokens.map{ PatternItem(listOf(WordConstraint(it))) }
    val fillerPatterns = fillerTokens.map{ PatternItem(listOf(WordConstraint(it))) }
    val postFillerPatterns = postFillerTokens.map{ PatternItem(listOf(WordConstraint(it))) }

    return Rule(preFiller = Pattern(preFillerPatterns),
            filler = Pattern(fillerPatterns),
            posFiller = Pattern(postFillerPatterns))
}

fun main(args: Array<String>) {

    val tagger = POSTagger<NLPNode>()

    val `in` = "Hello world."

    val result = tagger.
    for (token in tokenizer.tokenize(`in`))
        System.out.println(token)
}