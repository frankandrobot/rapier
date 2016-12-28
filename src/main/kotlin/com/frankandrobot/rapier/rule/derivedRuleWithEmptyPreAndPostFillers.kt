package com.frankandrobot.rapier.rule

import com.frankandrobot.rapier.pattern.Pattern


fun derivedRuleWithEmptyPreAndPostFillers(pattern : Pattern,
                                          baseRule1 : IRule,
                                          baseRule2 : IRule) : IDerivedRule {

  assert(baseRule1.slotName == baseRule2.slotName)

  return DerivedRule(
    preFiller = Pattern(),
    filler = pattern,
    postFiller = Pattern(),
    slotName = baseRule1.slotName,
    baseRule1 = baseRule1,
    baseRule2 = baseRule2
  )
}
