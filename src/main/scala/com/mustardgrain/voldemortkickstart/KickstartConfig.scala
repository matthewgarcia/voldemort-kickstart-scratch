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

class KickstartConfig(val clusterName: String,
  val privateKeyFile: File,
  val ami: String,
  val accessId: String,
  val secretKey: String,
  val keypairId: String,
  val instanceType: Ec2InstanceType.Value,
  val instanceCount: Int,
  val partitionsPerNode: Int,
  val userId: String) {

  if (clusterName.exists(!_.isLetterOrDigit))
    throw new InvalidKickstartConfigException("Cluster name contains non-alphanumeric characters")

  override def toString(): String = getClass.getName + " - clusterName: " + clusterName +
    ", ami: " + ami +
    ", keypairId: " + keypairId +
    ", instanceType: " + instanceType +
    ", instanceCount: " + instanceCount +
    ", partitionsPerNode: " + partitionsPerNode +
    ", userId: " + userId

}

object KickstartConfig {

  def fromString(jsonString: String): KickstartConfig = {
    val config = JSON.parseFull(jsonString).getOrElse({
      throw new InvalidKickstartConfigException()
    }).asInstanceOf[Map[String, String]]

    val clusterName = config.get("clusterName").getOrElse("default")
    val privateKeyFile = new File(config.getOrElse("privateKeyFile", System.getProperty("user.home") + "/.ssh/id_rsa"))
    val ami = getRequiredString(config, "ami")
    val accessId = getRequiredString(config, "accessId")
    val secretKey = getRequiredString(config, "secretKey")
    val keypairId = getRequiredString(config, "keypairId")
    val instanceType = Ec2InstanceType.valueOf(config.get("instanceType").getOrElse("DEFAULT")).getOrElse(Ec2InstanceType.DEFAULT)

    implicit def any2int(d: Any): Int = new Integer(d.toString().replace(".0", "")).intValue
    implicit def string2int(s: String): Int = new Integer(s.replace(".0", "")).intValue

    val instanceCount: Int = config.get("instanceCount").getOrElse(1)
    val partitionsPerNode: Int = getRequired(config, "partitionsPerNode")
    val userId = config.get("userId").getOrElse("root")

    new KickstartConfig(clusterName, privateKeyFile, ami, accessId, secretKey, keypairId, instanceType, instanceCount, partitionsPerNode, userId)
  }

  private def getRequiredString(config: Map[String, String], parameterName: String): String = {
    getRequired(config, parameterName).asInstanceOf[String]
  }

  private def getRequired(config: Map[String, String], parameterName: String): Any = {
    config.get(parameterName).getOrElse({
      throw new InvalidKickstartConfigException("Missing value for " + parameterName)
    })
  }

}