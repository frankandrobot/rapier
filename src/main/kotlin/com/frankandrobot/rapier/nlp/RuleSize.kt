package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.PatternList
import com.frankandrobot.rapier.rule.IRule
import org.funktionale.memoization.memoize


fun IRule.ruleSize(kRuleSizeWeight: Double) : Double {

  return preFiller.ruleSize(kRuleSizeWeight) +
    filler.ruleSize(kRuleSizeWeight) +
    postFiller.ruleSize(kRuleSizeWeight)
}


fun Pattern.ruleSize(kRuleSizeWeight: Double) : Double {

  return _unweightedRuleSize(this) * kRuleSizeWeight
}

/**
 * - each PatternItem counts 2
 * - each PatternList counts 3
 * - each disjunct in a word constraint counts 2
 * - each disjunct in a POS tag constraint counts 1
 * - each disjunct in a semantic constraint counts 1
 */
internal var _unweightedRuleSize: (Pattern) -> Int  = { pattern : Pattern ->

  pattern().map{ patternElement ->

    var metric = 0

    if (patternElement is PatternItem) { metric += 2 }
    if (patternElement is PatternList) { metric += 3 }

    metric += Math.max(0, patternElement.wordConstraints.size - 1) * 2
    metric += Math.max(0, patternElement.posTagConstraints.size - 1) * 1
    metric += Math.max(0, patternElement.semanticConstraints.size - 1) * 1

    metric

  }.fold(0) { total, cur -> total + cur }
}.memoize()
