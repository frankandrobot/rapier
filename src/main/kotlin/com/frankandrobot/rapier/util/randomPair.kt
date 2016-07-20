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
  assert(numPairs <= list.size/2, { "Too many pairs for list" })

  // picker for random uniques
  val randomUniquePicker = object {
    private val picked = HashSet<Int>()
    private val max = list.size

    fun next() : T {
      assert(picked.size < max, {
        "No more elements to pick"
      })

      var i = randomGenerator.nextInt(max)
      while (picked.contains(i)) {
        i = randomGenerator.nextInt(max)
      }
      picked.add(i)
      return list.get(i)
    }
  }

  // prep the return
  val out = ArrayList<Pair<T,T>>()

  for(i in 1..numPairs) {
    out.add(
      Pair(
        randomUniquePicker.next(),
        randomUniquePicker.next()
      )
    )
  }

  return out
}

