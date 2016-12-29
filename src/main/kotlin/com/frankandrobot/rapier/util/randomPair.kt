package com.frankandrobot.rapier.util

import org.funktionale.memoization.memoize
import java.util.*


internal val generatePairs = { n : Int ->
  val pairs = ArrayList<Pair<Int,Int>>(Math.ceil(n*(n - 1.0)/2.0).toInt())

  var i = 0
  var j : Int

  while(i<n) {
    j = i + 1
    while(j<n) {
      pairs.add(Pair(i,j))
      j++
    }
    i++
  }

  pairs
}.memoize()


private fun swap(a : ArrayList<Pair<Int,Int>>, i : Int, j : Int) {
  if (i != j) {
    val tmp = a[i]
    a[i] = a[j]
    a[j] = tmp
  }
}


/**
 * Get k random pairs from a list of size n.
 *
 * Not thread safe because of memoize. Also, because it constructs an array of all
 * possible pairs (of size O(n^2)), it's limited by memory size. Assuming 16 bytes per
 * Pair (4 bytes per integer and 8 bytes for the pointer):
 *
 * - a list of size n = 1000, uses the order of 7.992 mb
 * - a list of size n = 5000, uses the order of 199.96 mb
 * - a list of size n = 10,000, uses the order of 799.992 mb (almost a gig!)
 *
 * Thus, we (arbitrarily) limit the size of n at 5,000.
 */
fun randomPairs(n : Int, k : Int, random : Random = Random()) : List<Pair<Int,Int>> {

  assert(k > 0)
  assert(k <= n)
  assert(n <= 5000)

  if (n > 1) {

    val allPairs = generatePairs(n).clone() as ArrayList<Pair<Int, Int>>
    val pairs = ArrayList<Pair<Int, Int>>(k)

    var j = 0
    while (j < k) {

      val i = random.nextInt(allPairs.size - j) + j
      val randomPair = allPairs[i]

      pairs.add(randomPair)

      swap(allPairs, j, i)

      j++
    }

    return pairs
  }

  return emptyList()
}


/**
 * See #randomPairs for limitations and usage.
 */
fun<T> ArrayList<T>.randomPairs(k : Int, random : Random = Random()) : List<Pair<T,T>> {
  val kPairs = randomPairs(n = size, k = k, random = random)

  return kPairs.map{ Pair(this[it.first], this[it.second]) }
}
