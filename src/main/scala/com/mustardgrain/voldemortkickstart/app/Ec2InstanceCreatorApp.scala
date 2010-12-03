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

package com.mustardgrain.voldemortkickstart.app

import com.mustardgrain.voldemortkickstart._
import com.mustardgrain.voldemortkickstart.util._

object Ec2InstanceCreatorApp {

  def main(argv: Array[String]): Unit = {
    /*
    val accessId = ""
    val secretKey = ""
    val ami = ""
    val keypairId = ""
    val instanceCount = 1
    val instanceType = Ec2InstanceType.DEFAULT

    val ec2Connection: Ec2Connection = Ec2Connection(accessId, secretKey)

    val hostNamePairs = ec2Connection.createInstances(ami,
      keypairId,
      instanceType,
      instanceCount)
    */

    val test = """ssh root@localhost "mkdir -p /tmp/hiya; mkdir -p /tmp/hiya/buddy ; find /tmp | sort""""
    val args = CommandLineParser.parse(test)
    val cmd = new UnixCommand("localhost", args)
    val ret = cmd.execute
    
    //println("cmd return value: " + ret)
  }

}
