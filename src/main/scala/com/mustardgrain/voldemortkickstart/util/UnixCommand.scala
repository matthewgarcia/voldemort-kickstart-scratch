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

import scala.actors._
import scala.actors.Actor._
import com.mustardgrain.voldemortkickstart._
import org.apache.commons.logging.LogFactory

import scala.collection.JavaConversions._

class UnixCommand(val hostName: String, private val args: List[String]) {

  private val logger = LogFactory.getLog(getClass())
  private val WAIT_TIME = 2000

  private val caller = self

  private val reader = actor {
    println("created actor: " + Thread.currentThread)
    var continue = true
    loopWhile(continue) {
      reactWithin(WAIT_TIME) {
        case TIMEOUT =>
          caller ! "react timeout"
        case proc: Process =>
          println("entering first actor " + Thread.currentThread)
          val streamReader = new java.io.InputStreamReader(proc.getInputStream)
          val bufferedReader = new java.io.BufferedReader(streamReader)
          val stringBuilder = new java.lang.StringBuilder()
          var line: String = null
          while ({ line = bufferedReader.readLine; line != null }) {
            stringBuilder.append(line)
            stringBuilder.append("\n")
          }
          bufferedReader.close
          caller ! stringBuilder.toString
      }
    }
  }

  def execute = {
    println(args.toList)
    val builder = new ProcessBuilder(args.toList)
    val proc = builder.start()

    reader ! proc

    //Receive the console output from the actor.
    receiveWithin(WAIT_TIME) {
      case TIMEOUT => println("receiving Timeout")
      case result: String => println(result)
    }
    
    proc.waitFor
    
    proc.exitValue
  }

}