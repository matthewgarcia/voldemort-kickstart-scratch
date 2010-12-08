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
import java.io._
import scala.io._
import scala.util.parsing.json._

object Ec2InstanceCreator {

  def main(args: Array[String]): Unit = {
    val kickstartConfigFileName = "/home/kirk/Desktop/cluster.json"

    val kickstartConfig = KickstartConfig.fromString(inputFile(kickstartConfigFileName))
    //val clusterConfig = launchInstances(kickstartConfig)

    val clusterConfigFileName = "/home/kirk/Desktop/" + kickstartConfig.clusterName + ".json"
    val clusterConfig = Ec2ClusterConfig.fromString(inputFile(clusterConfigFileName))

    outputFile(clusterConfigFileName, clusterConfig.mkString)

    val clusterConfig2 = Ec2ClusterConfig.fromString(inputFile(clusterConfigFileName))

    println(clusterConfig2)
  }

  def launchInstances(config: KickstartConfig): Ec2ClusterConfig = {
    val ec2Connection = Ec2Connection(config.accessId, config.secretKey)
    val instances = ec2Connection.createInstances(config.ami, config.keypairId, config.instanceType, config.instanceCount)
    new Ec2ClusterConfig(config.clusterName, instances)
  }

  def testSsh() = {
    val test = """ssh root@localhost "mkdir -p /tmp/hiya; mkdir -p /tmp/hiya/buddy ; find /tmp | sort""""
    val args = CommandLineParser.parse(test)
    val cmd = new UnixCommand("localhost", args)
    val ret = cmd.execute()
  }

  def outputFile(fileName: String, contents: String) = {
    var writer: FileWriter = null

    try {
      writer = new FileWriter(new File(fileName))
      writer.write(contents)
    } catch {
      case _ => println("error writing file " + fileName); System.exit(1)
    }
    finally {
      writer.close()
    }
  }

  def inputFile(fileName: String): String = {
    Source.fromFile(fileName).mkString
  }

}
