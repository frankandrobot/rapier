package com.frankandrobot.rapier.meta


data class RapierParams(
  /**
   * the number of consecutive outer-loop failures before giving up.
   */
  val maxOuterLoopFails: Int = 4,
  /**
   * The number of random pairs to use in the compression algorithm
   */
  val compressionRandomPairs: Int = 6,
  val compressionPriorityQueueSize: Int = 7,
  /**
   * The number of consecutive compression failures before giving up
   */
  val compressionFails: Int = 3,
  /**
   * The minimum number of positive matches before a rule has an infinite metric, that
   * is, it is automatically discarded.
   */
  val metricMinPositiveMatches: Int = 3,
  /**
   * Maximum number of elements to specialize in one iteration of
   * specialize*Filler.
   */
  val maxElementsToSpecialize: Int = 5,
  /**
   * Used by RuleSize
   */
  val ruleSizeWeight: Double = 0.01,
  /**
   * Patterns longer than this value will be generalized by
   * #caseVeryLongPatterns. Range for this value is basically
   * limited by CPU. Larger values require faster CPU.
   */
  val maxPatternLength: Int = 15,
  /**
   * If longer pattern length is more than this value and pattern
   * length difference is at least 2, then the pair will be
   * generalized by #caseVeryLongPatterns. Range for this
   * value is basically limited by CPU. Larger values require
   * faster CPU.
   */
  val maxUnequalPatternLength: Int = 10,
  /**
   * If the difference in pattern lengths is more than this
   * value, the pair will be generalized by #caseVeryLongPatterns.
   * Range for this value is basically limited by CPU. Larger
   * values require faster CPU.
   */
  val maxDifferenceInPatternLength: Int = 5
)
