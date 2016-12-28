package com.frankandrobot.rapier.rule


interface IDerivedRule : IRule {
  val baseRule1 : IRule
  val baseRule2 : IRule
}
