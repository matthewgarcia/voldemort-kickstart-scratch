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
    var kickstartConfig: KickstartConfig = null;

    try {
      kickstartConfig = KickstartConfig.fromString(Source.fromFile(Config.KICKSTART_CONFIG).mkString)
    } catch {
      case e: FileNotFoundException =>
        println("ERROR: Please ensure that " + Config.KICKSTART_CONFIG + " exists and is readable")
      case e: InvalidKickstartConfigException =>
        println("ERROR: Please ensure that " + Config.KICKSTART_CONFIG + " contains valid configuration - " + e.getMessage)
        System.exit(1)
    }

    try {
      val ec2Connection = Ec2Connection(kickstartConfig.accessId, kickstartConfig.secretKey)
      val instances = ec2Connection.createInstances(kickstartConfig.ami, kickstartConfig.keypairId, kickstartConfig.instanceType, kickstartConfig.instanceCount)
      val clusterConfig = new Ec2ClusterConfig(kickstartConfig.clusterName, kickstartConfig.userId, "voldemort", kickstartConfig.privateKeyFile, instances)

      val clusterConfigFileName = Config.CONFIG_DIRECTORY + "/" + clusterConfig.name + ".json"
      outputFile(clusterConfigFileName, clusterConfig.mkString)

      val clusterXml = ClusterGenerator.createCluster(clusterConfig, kickstartConfig.partitionsPerNode)
      val clusterConfigXmlFileName = Config.CONFIG_DIRECTORY + "/" + clusterConfig.name + "-cluster.xml"
      outputFile(clusterConfigXmlFileName, clusterXml)

      println("Use the following value for your client's \"bootstrap URL\": tcp://" + clusterConfig.instances(0).externalHostName + ":6666")
    } catch {
      case e: InvalidKickstartConfigException => println(e.getMessage)
    }
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
