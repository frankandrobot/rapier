package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.pattern.*


fun Rule.ruleSize() : Int {

  return preFiller.ruleSize() + filler.ruleSize() + postFiller.ruleSize()
}

/**
 * - each PatternItem counts 2
 * - each PatternList counts 3
 * - each disjunct in a word constraint counts 2
 * - each disjunct in a POS tag constraint counts 1
 * - each disjunct in a semantic constraint counts 1
 */
fun Pattern.ruleSize() : Int {

  return invoke().map{ patternElement ->

    var metric = 0

    if (patternElement is PatternItem) { metric += 2 }
    if (patternElement is PatternList) { metric += 3 }

    metric += (patternElement.wordConstraints.size - 1) * 2
    metric += (patternElement.posTagContraints.size - 1) * 1
    metric += (patternElement.semanticConstraints.size - 1) * 1

    metric

  }.fold(0) { total, cur -> total + cur } / 100
}
