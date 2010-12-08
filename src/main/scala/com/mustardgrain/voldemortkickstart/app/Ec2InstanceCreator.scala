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
    val kickstartConfigFileName = args(0)
    val kickstartConfig = KickstartConfig.fromString(Source.fromFile(kickstartConfigFileName).mkString)

    val clusterConfig = launchInstances(kickstartConfig)
    val clusterConfigFileName = "/home/kirk/Desktop/" + clusterConfig.name + ".json"
    outputFile(clusterConfigFileName, clusterConfig.mkString)
  }

  def launchInstances(config: KickstartConfig): Ec2ClusterConfig = {
    val ec2Connection = Ec2Connection(config.accessId, config.secretKey)
    val instances = ec2Connection.createInstances(config.ami, config.keypairId, config.instanceType, config.instanceCount)
    new Ec2ClusterConfig(config.clusterName, config.userId, config.privateKeyFile, instances)
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

}
