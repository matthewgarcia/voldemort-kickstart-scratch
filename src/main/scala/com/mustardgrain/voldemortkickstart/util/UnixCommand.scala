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
import com.mustardgrain.voldemortkickstart._

import java.io._
import org.apache.commons.logging.LogFactory
import scala.actors._
import scala.actors.Actor._
import scala.collection.JavaConversions._

class UnixCommand(val hostName: String, private val args: List[String]) {

  private val logger = LogFactory.getLog(getClass())
  private val WAIT_TIME = 2000

  def execute() = {
    println(args)

    val builder = new ProcessBuilder(args.toList)
    val proc = builder.start()
    val lineReader = new BufferedReader(new InputStreamReader(proc.getInputStream))

    val caller = self

    val reader = actor {
      var continue = true

      loopWhile(continue) {
        reactWithin(WAIT_TIME) {
          case TIMEOUT =>
            caller ! "react timeout"

          case proc: Process =>
            var line: String = null

            while ({ line = lineReader.readLine; line != null }) {
              caller ! line
            }

            lineReader.close
            continue = false
            caller ! caller

        }
      }
    }

    reader ! proc

    var continue = true

    while (continue) {
      //Receive the console output from the actor.
      receiveWithin(WAIT_TIME) {
        case TIMEOUT => println("receiving Timeout")
        case result: String => println(hostName + ": " + result)
        case caller: Actor => continue = false
      }
    }

    proc.waitFor

    if (proc.exitValue != 0)
      println("Exited with code " + proc.exitValue)

    proc.exitValue
  }

}