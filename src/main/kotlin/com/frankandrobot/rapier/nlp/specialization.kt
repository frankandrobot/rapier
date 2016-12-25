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

  if (0 <= from && from <= to && to <= this.length && to - from <= maxDistance) {

    return Option.Some(Pattern(this().subList(from, to)))
  }

  return Option.None
}


fun specializePrefiller(rule : IDerivedRule, n : Int, params : RapierParams) =
  specializePrefiller(rule = RuleWithPositionInfo(rule), n = n, params = params)

fun specializePostFiller(rule : IDerivedRule, n : Int, params : RapierParams) =
  specializePostFiller(rule = RuleWithPositionInfo(rule), n = n, params = params)


/**
 * Must satisfy these contraints in order to apply specialization algorithm:
 * - areBasesLongEnough = basePreFillerLen1 >= n1 && basePreFillerLen2 >= n2
 * - usedLessThanRequested = n1 > numUsed1 && n2 > numUsed2
 * - underSearchLimit = n1 - numUsed1 <= k_MaxNoGainSearch &&
 *   n2 - numUsed2 <= k_MaxNoGainSearch
 */
internal fun specializePrefiller(rule : RuleWithPositionInfo,
                                 params : RapierParams,
                                 n1 : Int,
                                 n2 : Int) : List<RuleWithPositionInfo> {

  val k_MaxNoGainSearch = params.k_MaxNoGainSearch
  val numUsed1 = rule.preFillerInfo.numUsed1
  val numUsed2 = rule.preFillerInfo.numUsed2
  val basePreFiller1 = rule.baseRule1.preFiller
  val basePreFiller2 = rule.baseRule2.preFiller
  val basePreFillerLen1 = basePreFiller1.length
  val basePreFillerLen2 = basePreFiller2.length

  val areBasesLongEnough = basePreFillerLen1 >= n1 && basePreFillerLen2 >= n2

  if (areBasesLongEnough) {

    return generalize(
      basePreFiller1.subPattern(
        from = basePreFillerLen1 - n1,
        to = basePreFillerLen1 - numUsed1,
        maxDistance = k_MaxNoGainSearch
      ),
      basePreFiller2.subPattern(
        from = basePreFillerLen2 - n2,
        to = basePreFillerLen2 - numUsed2,
        maxDistance = k_MaxNoGainSearch
      )
    ).filter { it.isDefined() }
      .map { pattern ->
        val newPreFiller = pattern.get() + rule.preFiller
        val positionInfo = FillerIndexInfo(numUsed1 = n1, numUsed2 = n2)

        RuleWithPositionInfo(
          preFiller = newPreFiller,
          filler = rule.filler,
          postFiller = rule.postFiller,
          slot = rule.slot,
          baseRule1 = rule.baseRule1,
          baseRule2 = rule.baseRule2,
          preFillerInfo = positionInfo,
          postFillerInfo = rule.postFillerInfo
        )
      }
  }

  return emptyList()
}


internal fun specializePrefiller(rule : RuleWithPositionInfo,
                                 n : Int,
                                 params : RapierParams) : List<RuleWithPositionInfo> {

  val genSet1 = specializePrefiller(n1 = n,   n2 = n-1, rule = rule, params = params)
  val genSet2 = specializePrefiller(n1 = n-1, n2 = n,   rule = rule, params = params)
  val genSet3 = specializePrefiller(n1 = n,   n2 = n,   rule = rule, params = params)

  return genSet1 + genSet2 + genSet3
}


internal fun specializePostFiller(rule : RuleWithPositionInfo,
                                  params : RapierParams,
                                  n1 : Int,
                                  n2 : Int) : List<RuleWithPositionInfo> {

  val k_MaxNoGainSearch = params.k_MaxNoGainSearch
  val numUsed1 = rule.postFillerInfo.numUsed1
  val numUsed2 = rule.postFillerInfo.numUsed2
  val postFiller1 = rule.baseRule1.postFiller
  val postFiller2 = rule.baseRule2.postFiller

  return generalize(
    postFiller1.subPattern(
      from = numUsed1,
      to = n1,
      maxDistance = k_MaxNoGainSearch
    ),
    postFiller2.subPattern(
      from = numUsed2,
      to = n2,
      maxDistance = k_MaxNoGainSearch
    )
  ).filter { it.isDefined() }
    .map { pattern ->
      val newPostFiller = rule.postFiller + pattern.get()
      val positionInfo = FillerIndexInfo(numUsed1 = n1, numUsed2 = n2)

      RuleWithPositionInfo(
        preFiller = rule.preFiller,
        filler = rule.filler,
        postFiller = newPostFiller,
        slot = rule.slot,
        baseRule1 = rule.baseRule1,
        baseRule2 = rule.baseRule2,
        preFillerInfo = rule.preFillerInfo,
        postFillerInfo = positionInfo
    )
  }
}

internal fun specializePostFiller(rule : RuleWithPositionInfo,
                                  n : Int,
                                  params: RapierParams) : List<RuleWithPositionInfo> {

  val genSet1 = specializePostFiller(n1 = n,   n2 = n-1, rule = rule, params = params)
  val genSet2 = specializePostFiller(n1 = n-1, n2 = n,   rule = rule, params = params)
  val genSet3 = specializePostFiller(n1 = n,   n2 = n,   rule = rule, params = params)

  return genSet1 + genSet2 + genSet3
}
