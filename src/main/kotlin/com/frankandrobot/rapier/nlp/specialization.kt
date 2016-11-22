package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.pattern.BaseRule
import com.frankandrobot.rapier.pattern.DerivedRule
import com.frankandrobot.rapier.pattern.Pattern


data class FillerIndexInfo(val numUsed1 : Int = 0, val numUsed2 : Int = 0)

data class RuleWithPositionInfo(
  private val rule : DerivedRule,
  val preFillerInfo: FillerIndexInfo = FillerIndexInfo(),
  val postFillerInfo: FillerIndexInfo = FillerIndexInfo()) {

  operator fun invoke() = rule
}


fun initialRule(pattern : Pattern, baseRule1 : BaseRule, baseRule2 : BaseRule)
  : DerivedRule {

  assert(baseRule1.slot == baseRule2.slot)

  return DerivedRule(
    preFiller = Pattern(),
    filler = pattern,
    postFiller = Pattern(),
    slot = baseRule1.slot,
    baseRule1 = baseRule1,
    baseRule2 = baseRule2
  )
}


fun specializePrefiller(rule : RuleWithPositionInfo, n : Int) :
  List<RuleWithPositionInfo> {

  val numUsed1 = rule.preFillerInfo.numUsed1
  val numUsed2 = rule.preFillerInfo.numUsed2
  val preFiller1 = rule().baseRule1.preFiller
  val preFiller2 = rule().baseRule2.preFiller
  val patternLen1 = preFiller1.length
  val patternLen2 = preFiller2.length

  val genSet1 = generalize(
    preFiller1.croppedSubPattern(patternLen1 - n, patternLen1 - numUsed1),
    preFiller2.croppedSubPattern(patternLen2 - n + 1, patternLen2 - numUsed2)
  ).map{ Pair(it, FillerIndexInfo(numUsed1 = n, numUsed2 = n - 1)) }

  val genSet2 = generalize(
    preFiller1.croppedSubPattern(patternLen1 - n + 1, patternLen1 - numUsed1),
    preFiller2.croppedSubPattern(patternLen2 - n, patternLen2 - numUsed2)
  ).map{ Pair(it, FillerIndexInfo(numUsed1 = n - 1, numUsed2 = n)) }

  val genSet3 = generalize(
    preFiller1.croppedSubPattern(patternLen1 - n, patternLen1 - numUsed1),
    preFiller2.croppedSubPattern(patternLen2 - n, patternLen2 - numUsed2)
  ).map{ Pair(it, FillerIndexInfo(numUsed1 = n, numUsed2 = n)) }

  val genSet = genSet1 + genSet2 + genSet3

  return genSet.map{ pattern ->
    val newPreFiller = pattern.first + rule().preFiller

    RuleWithPositionInfo(
      DerivedRule(
        preFiller = newPreFiller,
        filler = rule().filler,
        postFiller = rule().postFiller,
        slot = rule().slot,
        baseRule1 = rule().baseRule1,
        baseRule2 = rule().baseRule2
      ),
      preFillerInfo = pattern.second,
      postFillerInfo = rule.postFillerInfo
    )
  }
}

fun specializePostFiller(rule : RuleWithPositionInfo, n : Int) :
  List<RuleWithPositionInfo> {

  val numUsed1 = rule.postFillerInfo.numUsed1
  val numUsed2 = rule.postFillerInfo.numUsed2
  val postFiller1 = rule().baseRule1.postFiller
  val postFiller2 = rule().baseRule2.postFiller

  val genSet1 = generalize(
    postFiller1.croppedSubPattern(numUsed1, n),
    postFiller2.croppedSubPattern(numUsed2, n-1)
  ).map{ Pair(it, FillerIndexInfo(numUsed1 = n, numUsed2 = n - 1)) }

  val genSet2 = generalize(
    postFiller1.croppedSubPattern(numUsed1, n-1),
    postFiller2.croppedSubPattern(numUsed2, n)
  ).map{ Pair(it, FillerIndexInfo(numUsed1 = n - 1, numUsed2 = n)) }

  val genSet3 = generalize(
    postFiller1.croppedSubPattern(numUsed1, n),
    postFiller2.croppedSubPattern(numUsed2, n)
  ).map{ Pair(it, FillerIndexInfo(numUsed1 = n, numUsed2 = n)) }

  val genSet = genSet1 + genSet2 + genSet3

  return genSet.map{ pattern ->
    val newPostFiller = rule().postFiller + pattern.first

    RuleWithPositionInfo(
      DerivedRule(
        preFiller = rule().preFiller,
        filler = rule().filler,
        postFiller = newPostFiller,
        slot = rule().slot,
        baseRule1 = rule().baseRule1,
        baseRule2 = rule().baseRule2
      ),
      preFillerInfo = rule.preFillerInfo,
      postFillerInfo = pattern.second
    )
  }
}
