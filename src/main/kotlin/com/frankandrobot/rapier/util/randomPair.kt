package com.frankandrobot.rapier.util

import java.util.*


private val randomGenerator = Random()

/**
 * Select a random pair from a list of size n
 */
private fun randomPair(n : Int) : Pair<Int, Int> {

  val a = randomGenerator.nextInt(n)
  var b = randomGenerator.nextInt(n)

  while(b == a) { b = randomGenerator.nextInt(n) }

  return Pair(a, b)
}

/**
 * Select a random pair from an array list
 *
 * Optimization: this gets faster as the list size increases
 * For smaller sized lists, it may be worth hardcoding the combinations.
 */
fun <T> randomPair(list : ArrayList<T>) : Pair<T, T> {

  val pairIndexes = randomPair(list.size)

  return Pair(list[pairIndexes.first], list[pairIndexes.second])
}
