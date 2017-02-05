/*
 *    Copyright 2016 Uriel Avalos
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.frankandrobot.rapier.meta

import java.util.*


data class RapierParams(
  /**
   * the number of consecutive outer-loop failures before giving up.
   */
  @JvmField val maxOuterLoopFails: Int = 4,
  /**
   * The number of random pairs to use in the compression algorithm
   */
  @JvmField val compressionRandomPairs: Int = 6,
  @JvmField val compressionPriorityQueueSize: Int = 7,
  /**
   * The number of consecutive compression failures before giving up
   */
  @JvmField val compressionFails: Int = 3,
  /**
   * The minimum number of positive matches before a rule has an infinite metric, that
   * is, it is automatically discarded.
   */
  @JvmField val metricMinPositiveMatches: Int = 3,
  /**
   * Maximum number of elements to specialize in one iteration of
   * specialize*Filler.
   */
  @JvmField val maxElementsToSpecialize: Int = 5,
  /**
   * Used by RuleSize
   */
  @JvmField val ruleSizeWeight: Double = 0.01,
  /**
   * Patterns longer than this value will be generalized by
   * #caseVeryLongPatterns. Range for this value is basically
   * limited by CPU. Larger values require faster CPU.
   */
  @JvmField val maxPatternLength: Int = 15,
  /**
   * If longer pattern length is more than this value and pattern
   * length difference is at least 2, then the pair will be
   * generalized by #caseVeryLongPatterns. Range for this
   * value is basically limited by CPU. Larger values require
   * faster CPU.
   */
  @JvmField val maxUnequalPatternLength: Int = 10,
  /**
   * If the difference in pattern lengths is more than this
   * value, the pair will be generalized by #caseVeryLongPatterns.
   * Range for this value is basically limited by CPU. Larger
   * values require faster CPU.
   */
  @JvmField val maxDifferenceInPatternLength: Int = 5,
  @JvmField val Random : Random = Random()
)
