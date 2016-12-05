package com.frankandrobot.rapier


fun outer(compressLim: Int = 3) {
  //val slotRules = most specific rules for S from example documents
  var failures = 0
  while (failures < compressLim) {
    //best new rule = findNewRule(slotRules, examples)
    //if bestNewRule is acceptable
    //  add bestNewRule to slotRules
    //  remove empirally subsumed rules from slotRules
    //else
    failures++
  }
}
