package com.frankandrobot.rapier.parse

data class MatchResult(val preFillerMatch : MatchInfo,
                       val fillerMatch : MatchInfo,
                       val postFillerMatch : MatchInfo,
                       val matchFound : Boolean = true)
