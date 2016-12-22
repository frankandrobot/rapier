package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.meta.RapierParams
import com.frankandrobot.rapier.pattern.FillerIndexInfo
import com.frankandrobot.rapier.pattern.IDerivedRule
import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.RuleWithPositionInfo
import org.funktionale.option.Option


fun Pattern.subPattern(from: Int,
                       to: Int,
                       maxDistance : Int) : Option<Pattern> {

  if (0 <= from && from < to && to - from <= maxDistance) {

    return Option.Some(Pattern(this().subList(from, to)))
  }

  return Option.None
}


fun specializePrefiller(rule : IDerivedRule, n : Int, params : RapierParams) =
  specializePrefiller(rule = RuleWithPositionInfo(rule), n = n, params = params)

fun specializePostFiller(rule : IDerivedRule, n : Int, params : RapierParams) =
  specializePostFiller(rule = RuleWithPositionInfo(rule), n = n, params = params)

internal fun specializePrefiller(rule : RuleWithPositionInfo,
                                 n : Int,
                                 params : RapierParams) : List<RuleWithPositionInfo> {

  val k_MaxNoGainSearch = params.k_MaxNoGainSearch
  val numUsed1 = rule.preFillerInfo.numUsed1
  val numUsed2 = rule.preFillerInfo.numUsed2
  val preFiller1 = rule.baseRule1.preFiller
  val preFiller2 = rule.baseRule2.preFiller
  val patternLen1 = preFiller1.length
  val patternLen2 = preFiller2.length

  val genSet1 = generalize(
    preFiller1.subPattern(
      from = patternLen1 - n,
      to = patternLen1 - numUsed1,
      maxDistance = k_MaxNoGainSearch
    ),
    preFiller2.subPattern(
      from = patternLen2 - n + 1,
      to = patternLen2 - numUsed2,
      maxDistance = k_MaxNoGainSearch
    )
  ).filter{ it.isDefined() }
    .map{ Pair(it.get(), FillerIndexInfo(numUsed1 = n, numUsed2 = n - 1)) }

  val genSet2 = generalize(
    preFiller1.subPattern(
      from = patternLen1 - n + 1,
      to = patternLen1 - numUsed1,
      maxDistance = k_MaxNoGainSearch
    ),
    preFiller2.subPattern(
      from = patternLen2 - n,
      to = patternLen2 - numUsed2,
      maxDistance = k_MaxNoGainSearch
    )
  ).filter{ it.isDefined() }
    .map{ Pair(it.get(), FillerIndexInfo(numUsed1 = n - 1, numUsed2 = n)) }

  val genSet3 = generalize(
    preFiller1.subPattern(
      from = patternLen1 - n,
      to = patternLen1 - numUsed1,
      maxDistance = k_MaxNoGainSearch
    ),
    preFiller2.subPattern(
      from = patternLen2 - n,
      to = patternLen2 - numUsed2,
      maxDistance = k_MaxNoGainSearch)
  ).filter{ it.isDefined() }
    .map{ Pair(it.get(), FillerIndexInfo(numUsed1 = n, numUsed2 = n)) }

  val genSet = genSet1 + genSet2 + genSet3

  return genSet.map{ pattern ->
    val newPreFiller = pattern.first + rule.preFiller

    RuleWithPositionInfo(
      preFiller = newPreFiller,
      filler = rule.filler,
      postFiller = rule.postFiller,
      slot = rule.slot,
      baseRule1 = rule.baseRule1,
      baseRule2 = rule.baseRule2,
      preFillerInfo = pattern.second,
      postFillerInfo = rule.postFillerInfo
    )
  }
}

internal fun specializePostFiller(rule : RuleWithPositionInfo,
                                  n : Int,
                                  params: RapierParams) : List<RuleWithPositionInfo> {

  val k_MaxNoGainSearch = params.k_MaxNoGainSearch
  val numUsed1 = rule.postFillerInfo.numUsed1
  val numUsed2 = rule.postFillerInfo.numUsed2
  val postFiller1 = rule.baseRule1.postFiller
  val postFiller2 = rule.baseRule2.postFiller

  val genSet1 = generalize(
    postFiller1.subPattern(
      from = numUsed1,
      to = n,
      maxDistance = k_MaxNoGainSearch
    ),
    postFiller2.subPattern(
      from = numUsed2,
      to = n-1,
      maxDistance = k_MaxNoGainSearch
    )
  ).filter{ it.isDefined() }
    .map{ Pair(it.get(), FillerIndexInfo(numUsed1 = n, numUsed2 = n - 1)) }

  val genSet2 = generalize(
    postFiller1.subPattern(
      from = numUsed1,
      to = n-1,
      maxDistance = k_MaxNoGainSearch
    ),
    postFiller2.subPattern(
      from = numUsed2,
      to = n,
      maxDistance = k_MaxNoGainSearch
    )
  ).filter{ it.isDefined() }
    .map{ Pair(it.get(), FillerIndexInfo(numUsed1 = n - 1, numUsed2 = n)) }

  val genSet3 = generalize(
    postFiller1.subPattern(
      from = numUsed1,
      to = n,
      maxDistance = k_MaxNoGainSearch
    ),
    postFiller2.subPattern(
      from = numUsed2,
      to = n,
      maxDistance = k_MaxNoGainSearch
    )
  ).filter{ it.isDefined() }
    .map{ Pair(it.get(), FillerIndexInfo(numUsed1 = n, numUsed2 = n)) }

  val genSet = genSet1 + genSet2 + genSet3

  return genSet.map{ pattern ->
    val newPostFiller = rule.postFiller + pattern.first

    RuleWithPositionInfo(
      preFiller = rule.preFiller,
      filler = rule.filler,
      postFiller = newPostFiller,
      slot = rule.slot,
      baseRule1 = rule.baseRule1,
      baseRule2 = rule.baseRule2,
      preFillerInfo = rule.preFillerInfo,
      postFillerInfo = pattern.second
    )
  }
}
