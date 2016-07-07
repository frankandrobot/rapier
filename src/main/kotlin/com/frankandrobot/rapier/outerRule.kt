package com.frankandrobot.rapier


/**
 * Compression - rapier begins with a most specific definition and then attempts to compact that definition
 * by replacing rules with more general rules. Since rules for different slots are independent of one another,
 * the system actually creates the most specific definition and then compacts it separately for each slot
 * in the template.
 */
fun outer(compressLim : Int = 3) {
    //val slotRules = most specific rules for S from example documents
    var failures = 0
    while(failures < compressLim) {
        //best new rule = findNewRule(slotRules, examples)
        //if bestNewRule is acceptable
        //  add bestNewRule to slotRules
        //  remove empirally subsumed rules from slotRules
        //else
        failures++
    }
}