package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.meta.RapierParams
import com.frankandrobot.rapier.pattern.FillerIndexInfo
import com.frankandrobot.rapier.pattern.IDerivedRule
import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.RuleWithPositionInfo
import org.funktionale.option.Option
import org.funktionale.option.Option.None
import org.funktionale.option.Option.Some


/**
 * Returns the sub-Pattern consisting of the pattern elements from fromIndex to
 * toIndex - 1. That means that when fromIndex == toIndex, it returns Some(Pattern()),
 * that is, the empty Pattern. This is a valid pattern and therefore it makes sense to
 * return Some as opposed to None.
 *
 * Note that when the constraints aren't satisfied, it returns None, not the empty
 * Pattern.
 */
fun Pattern.subPattern(fromIndex: Int,
                       toIndex: Int,
                       maxElements: Int) : Option<Pattern> {

  if (0 <= fromIndex && fromIndex <= toIndex && toIndex <= this.length &&
    toIndex - fromIndex <= maxElements) {

    return Some(Pattern(this().subList(fromIndex, toIndex)))
  }

  return None
}


fun specializePreFiller(rule : IDerivedRule, n : Int, params : RapierParams) =
  specializePreFiller(rule = RuleWithPositionInfo(rule), n = n, params = params)

fun specializePostFiller(rule : IDerivedRule, n : Int, params : RapierParams) =
  specializePostFiller(rule = RuleWithPositionInfo(rule), n = n, params = params)


/**
 * Must satisfy these contraints in order to apply specialization algorithm:
 * - areBasesLongEnough = basePreFillerLen1 >= n1 && basePreFillerLen2 >= n2
 * - usedLessThanRequested = n1 > numUsed1 && n2 > numUsed2
 * - underSearchLimit = n1 - numUsed1 <= k_MaxNoGainSearch &&
 *   n2 - numUsed2 <= k_MaxNoGainSearch
 */
internal fun specializePreFiller(rule : RuleWithPositionInfo,
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
        fromIndex = basePreFillerLen1 - n1,
        toIndex = basePreFillerLen1 - numUsed1,
        maxElements = k_MaxNoGainSearch
      ),
      basePreFiller2.subPattern(
        fromIndex = basePreFillerLen2 - n2,
        toIndex = basePreFillerLen2 - numUsed2,
        maxElements = k_MaxNoGainSearch
      )
    ).filter { it.isDefined() }
      .map { pattern ->
        val newPreFiller = pattern.get() + rule.preFiller
        val positionInfo = FillerIndexInfo(numUsed1 = n1, numUsed2 = n2)

        RuleWithPositionInfo(
          preFiller = newPreFiller,
          filler = rule.filler,
          postFiller = rule.postFiller,
          slotName = rule.slotName,
          baseRule1 = rule.baseRule1,
          baseRule2 = rule.baseRule2,
          preFillerInfo = positionInfo,
          postFillerInfo = rule.postFillerInfo
        )
      }
  }

  return emptyList()
}


internal fun specializePreFiller(rule : RuleWithPositionInfo,
                                 n : Int,
                                 params : RapierParams) : List<RuleWithPositionInfo> {

  val genSet1 = specializePreFiller(n1 = n,   n2 = n-1, rule = rule, params = params)
  val genSet2 = specializePreFiller(n1 = n-1, n2 = n,   rule = rule, params = params)
  val genSet3 = specializePreFiller(n1 = n,   n2 = n,   rule = rule, params = params)

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
      fromIndex = numUsed1,
      toIndex = n1,
      maxElements = k_MaxNoGainSearch
    ),
    postFiller2.subPattern(
      fromIndex = numUsed2,
      toIndex = n2,
      maxElements = k_MaxNoGainSearch
    )
  ).filter { it.isDefined() }
    .map { pattern ->
      val newPostFiller = rule.postFiller + pattern.get()
      val positionInfo = FillerIndexInfo(numUsed1 = n1, numUsed2 = n2)

      RuleWithPositionInfo(
        preFiller = rule.preFiller,
        filler = rule.filler,
        postFiller = newPostFiller,
        slotName = rule.slotName,
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
