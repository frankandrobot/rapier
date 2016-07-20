package com.frankandrobot.rapier.util

import java.util.*


private val randomGenerator = Random()

/**
 * Select a random pair from a list of size n
 */
private fun randomPair(n: Int): Pair<Int, Int> {

  val a = randomGenerator.nextInt(n)
  var b = randomGenerator.nextInt(n)

  while (b == a) {
    b = randomGenerator.nextInt(n)
  }

  return Pair(a, b)
}

/**
 * Select a random pair from an array list
 *
 * Optimization: this gets faster as the list size increases
 * For smaller sized lists, it may be worth hardcoding the combinations.
 */
fun <T> randomPair(list: ArrayList<T>): Pair<T, T> {

  val pairIndexes = randomPair(list.size)

  return Pair(list[pairIndexes.first], list[pairIndexes.second])
}

/**
 * Generates unique pairs of random elements from `list`
 * Complexity is O(2n), uses Java implementation of Fisher-Yates shuffle
 *
 * @param list input list
 * @param numPairs the number of pairs to generate (cannot be greater than list.size/2)
 * @return an ArrayList of unique pairs of elements
 */

fun <T> randomPairs(list: ArrayList<T>, numPairs: Int = (list.size / 2)): ArrayList<Pair<T, T>> {
  // create an index list and shuffle it
  val indicies = (0..(list.size)).toList()
  Collections.shuffle(indicies)

  // prep the return
  val out = ArrayList<Pair<T,T>>()

  // could also use for(i in (0..(numpairs))
  (0..(numPairs)).map {
    out.add(Pair(
      list.get(
        indicies.get(it * 2)
      ),
      list.get(
        indicies.get(it * 2 + 1)
      )
    ))
  }

  return out
}
