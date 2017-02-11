package com.frankandrobot.rapier.nlp.jwi

import edu.mit.jwi.Dictionary
import edu.mit.jwi.IDictionary


fun load(path : String) : IDictionary {
  val url =  java.net.URL("file",null ,path)
  val dict = Dictionary(url)
  return dict
}

