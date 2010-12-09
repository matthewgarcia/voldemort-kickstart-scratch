/*
 * Copyright 2010 Mustard Grain, Inc.
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
import java.io._
import scala.io._
import scala.util.parsing.json._

object Tester {

  def main(args: Array[String]): Unit = {
    val clusterConfigFileName = args(0)
    val clusterConfig = Ec2ClusterConfig.fromString(Source.fromFile(clusterConfigFileName).mkString)

    val clusterXml = ClusterGenerator.createCluster(clusterConfig, 13)
    val storesXml = StoresGenerator.createStoreDescriptor("test", "foo", "bar", 1, Math.min(clusterConfig.instances.length, 3), Math.min(clusterConfig.instances.length, 3))

    println(clusterXml)
    println(storesXml)
    System.exit(0)

    clusterConfig.instances.foreach({ instance =>
      val test = "ssh -i " + clusterConfig.privateKeyFile + " -o StrictHostKeyChecking=no " + clusterConfig.userId + "@" + instance.externalHostName + " \"ls -a -l ~ | sort\""
      val args = CommandLineParser.parse(test)
      val cmd = new UnixCommand(instance.externalHostName, args)
      val ret = cmd.execute()
    })
  }

}
