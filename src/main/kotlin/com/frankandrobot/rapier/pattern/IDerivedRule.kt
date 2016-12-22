package com.frankandrobot.rapier.pattern

interface IDerivedRule : IRule {
  val baseRule1 : IRule
  val baseRule2 : IRule
}
