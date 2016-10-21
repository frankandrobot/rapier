package com.frankandrobot.rapier.util


interface Option<T> {
  val value : T
  val valid : Boolean
}

class Some<T>(override val value : T) : Option<T> {
  override val valid = true
}

class None<T>() : Option<T?> {
  override val value : T? = null
  override val valid = false
}
