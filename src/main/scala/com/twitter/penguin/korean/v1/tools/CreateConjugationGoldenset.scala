/*
 * Twitter Korean Text - Scala library to process Korean text
 *
 * Copyright 2014 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twitter.penguin.korean.v1.tools

import java.io.FileOutputStream

import com.twitter.penguin.korean.v1.util.KoreanConjugation._
import com.twitter.penguin.korean.v1.util.KoreanDictionaryProvider._

import scala.collection.JavaConversions._

/**
 * Create Korean predicate expansion goldenset from adjective and verb resources.
 * The first argument is the test resource directory.
 */
object CreateConjugationGoldenset {

  case class ConjugationExample(word: String, conjugations: Seq[String])

  def main(args: Array[String]) {
    System.err.println("Reading the verbs and adjectives..")

    def updateConjugateExamples(file: String, isAdj: Boolean, outputFileName: String) {
      System.err.println("Writing the expansion goldenset in " + outputFileName)

      val outputPath = "src/test/resources/com/twitter/penguin/korean/v1/util/" + outputFileName
      val out = new FileOutputStream(outputPath)

      val words = readWordsAsSeq(file)
      val goldenset = words.map(word =>
        ConjugationExample(word,
          conjugatePredicates(Set(word), isAdj)
              .map { case word: Array[Char] => new String(word)}
              .toSeq.sorted)
      )

      goldenset.foreach {
        c => out.write(
          "%s\t%s\n".format(c.word, c.conjugations.mkString(", ")).getBytes
        )
      }

      out.close()
    }

    updateConjugateExamples("adjective/adjective.txt", isAdj = true, "adj_conjugate.txt")
    updateConjugateExamples("verb/verb.txt", isAdj = false, "verb_conjugate.txt")
  }
}
