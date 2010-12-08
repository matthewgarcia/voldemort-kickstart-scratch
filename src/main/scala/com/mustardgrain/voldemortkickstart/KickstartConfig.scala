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
  val ami: String,
  val accessId: String,
  val secretKey: String,
  val keypairId: String,
  val instanceType: Ec2InstanceType.Value,
  val instanceCount: Int) {

  override def toString(): String = getClass.getName + " - clusterName: " + clusterName +
    ", ami: " + ami +
    ", instanceType: " + instanceType +
    ", instanceCount: " + instanceCount

}

object KickstartConfig {

  def fromString(jsonString: String): KickstartConfig = {
    val config = JSON.parseFull(jsonString).getOrElse({
      println("Invalid Kickstart configuration")
      System.exit(1)
    }).asInstanceOf[Map[String, String]]

    val clusterName = config.get("name").getOrElse("default")
    val ami = getRequiredString(config, "ami")
    val accessId = getRequiredString(config, "accessId")
    val secretKey = getRequiredString(config, "secretKey")
    val keypairId = getRequiredString(config, "keypairId")
    val instanceType = Ec2InstanceType.valueOf(config.get("instanceType").getOrElse("DEFAULT")).getOrElse(Ec2InstanceType.DEFAULT)
    val instanceCount = config.get("instanceCount").getOrElse("1").toInt

    new KickstartConfig(clusterName, ami, accessId, secretKey, keypairId, instanceType, instanceCount)
  }

  private def getRequiredString(config: Map[String, String], parameterName: String): String = {
    config.get(parameterName).getOrElse({
      println("Please enter a value for " + parameterName)
      System.exit(1)
    }).asInstanceOf[String]
  }

}