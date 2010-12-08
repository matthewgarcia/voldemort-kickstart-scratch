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

package com.mustardgrain.voldemortkickstart

import java.io._
import scala.io._
import scala.util.parsing.json._

class Ec2ClusterConfig(val name: String, val userId: String, val privateKeyFile: File, val instances: List[Ec2Instance]) {

  def mkString(): String = {
    val buffer = new StringBuffer()
    buffer.append("{\n")
    buffer.append("  \"name\": \"" + name + "\",\n")
    buffer.append("  \"userId\": \"" + userId + "\",\n")
    buffer.append("  \"privateKeyFile\": \"" + privateKeyFile.getAbsolutePath() + "\",\n")
    buffer.append("  \"nodes\": [\n")
    buffer.append("     ")

    var flag = false

    instances.foreach { instance =>
      if (flag)
        buffer.append(", ")
      else
        flag = true

      buffer.append("{")
      buffer.append("\"instanceId\": \"" + instance.instanceId + "\", ")
      buffer.append("\"externalHostName\": \"" + instance.externalHostName + "\", ")
      buffer.append("\"internalHostName\": \"" + instance.internalHostName + "\"")
      buffer.append("}")
    }

    buffer.append("\n  ]\n}")
    buffer.toString()
  }

  override def toString(): String = getClass.getName + " - name: " + name + ", instances: " + instances

}

object Ec2ClusterConfig {

  def fromString(jsonString: String): Ec2ClusterConfig = {
    val config = JSON.parseFull(jsonString).getOrElse({
      println("Invalid EC2 cluster configuration")
      System.exit(1)
    }).asInstanceOf[Map[String, String]]

    val clusterName = getRequiredString(config, "name")
    val userId = config.getOrElse("userId", "root")
    val privateKeyFile = new File(config.getOrElse("privateKeyFile", System.getenv("HOME") + "/.ssh/id_rsa"))

    val instances = config.get("nodes").getOrElse({
      println("Please enter a value for nodes")
      System.exit(1)
    }).asInstanceOf[List[Map[String, String]]].map({ innerMap =>
      val instanceId = getRequiredString(innerMap, "instanceId")
      val externalHostName = getRequiredString(innerMap, "externalHostName")
      val internalHostName = getRequiredString(innerMap, "internalHostName")
      new Ec2Instance(instanceId, externalHostName, internalHostName)
    })

    new Ec2ClusterConfig(clusterName, userId, privateKeyFile, instances)
  }

  private def getRequiredString(config: Map[String, String], parameterName: String): String = {
    config.get(parameterName).getOrElse({
      println("Please enter a value for " + parameterName)
      System.exit(1)
    }).asInstanceOf[String]
  }

}