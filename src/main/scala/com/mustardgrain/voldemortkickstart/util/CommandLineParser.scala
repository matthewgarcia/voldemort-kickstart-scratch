/*
 * Copyright 2009 LinkedIn, Inc., 2010 Mustard Grain, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.mustardgrain.voldemortkickstart.util

import scala.collection.mutable._

object CommandLineParser {

  def parse(command: String):List[String] = {
    val commands = new ListBuffer[String]()
    var isInQuotes = false
    var start = 0

    for (i <- 0 until command.length()) {
      val c = command.charAt(i)

      if (c == '\"') {
        isInQuotes = !isInQuotes;
      } else if (c == ' ' && !isInQuotes) {
        val substring = command.substring(start, i).trim()
        start = i + 1;

        if (!substring.trim().isEmpty)
          commands += substring.replace("\"", "")
      }
    }

    val substring = command.substring(start).trim()

    if (substring.length() > 0)
      commands += substring.replace("\"", "")

    commands.toList
  }

}