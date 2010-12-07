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
    output()
  }

  def output(): Unit = {
    val ec2Instances = List(new Ec2Instance("foo", "bar", "baz"), new Ec2Instance("foo2", "bar3", "baz4"))

    val buffer = new StringBuffer()
    ec2Instances.foreach { instance =>
      if (buffer.length() > 0)
        buffer.append(", ")

      buffer.append("{")
      buffer.append("\"instanceId\": \"" + instance.instanceId + "\", ");
      buffer.append("\"externalHostName\": \"" + instance.externalHostName + "\", ");
      buffer.append("\"internalHostName\": \"" + instance.internalHostName + "\"");
      buffer.append("}")
    }

    buffer.insert(0, "{ \n  \"nodes\": [\n    ")
    buffer.append("\n  ]\n}")

    var writer: FileWriter = null

    try {
      writer = new FileWriter(new File("/home/kirk/Desktop/default.json"))
      writer.write(buffer.toString())
    } catch {
      case _ => println("error writing file"); System.exit(1)
    }
    finally {
      writer.close()
    }

    val fileName = "/home/kirk/Desktop/default.json"
    val source = Source.fromFile(fileName).mkString
    val parsed = JSON.parseFull(source).asInstanceOf[Option[Map[String, String]]]
    println(parsed)
  }

  def launchInstances(): List[Ec2Instance] = {
    val fileName = "/home/kirk/Desktop/cluster.json"
    val source = Source.fromFile(fileName).mkString
    val parsed = JSON.parseFull(source).asInstanceOf[Option[Map[String, String]]]

    val config = parsed.getOrElse(Map())
    val clusterName = config.get("name").getOrElse("default")
    val ami = getConfig(config, "ami")
    val accessId = getConfig(config, "accessId")
    val secretKey = getConfig(config, "secretKey")
    val keypairId = getConfig(config, "keypairId")
    val instanceType = Ec2InstanceType.valueOf(config.get("instanceType").getOrElse("DEFAULT")).getOrElse(Ec2InstanceType.DEFAULT)
    val instanceCount = config.get("instanceCount").getOrElse("1").toInt

    val ec2Connection: Ec2Connection = Ec2Connection(accessId, secretKey)
    ec2Connection.createInstances(ami, keypairId, instanceType, instanceCount)
  }

  def testSsh() = {
    val test = """ssh root@localhost "mkdir -p /tmp/hiya; mkdir -p /tmp/hiya/buddy ; find /tmp | sort""""
    val args = CommandLineParser.parse(test)
    val cmd = new UnixCommand("localhost", args)
    val ret = cmd.execute()
  }

  def getConfig(config: Map[String, String], parameterName: String): String = {
    config.get(parameterName).getOrElse({
      println("Please enter a value for " + parameterName)
      System.exit(1)
      ""
    })
  }

}
