package com.frankandrobot.rapier.util


fun indent(str : String, n : Int) : String {

  val lines = str.split("\n")
  val space = (1..n).joinToString(separator = "", transform = {" "})

  return return lines.joinToString(prefix = space, separator = "\n$space")
}
