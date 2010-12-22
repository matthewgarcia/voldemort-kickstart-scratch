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
import scala.actors._
import scala.actors.Actor._
import scala.concurrent.ops._
import scala.io._
import scala.util.parsing.json._

import java.util.concurrent._
import scala.collection.mutable._

object ConfigUpdater {

  val WAIT_TIME = 60

  def main(args: Array[String]): Unit = {
    val clusterConfigFileName = args(0)
    val voldemortConfigDir = args(1)
    val clusterConfig = Ec2ClusterConfig.fromString(Source.fromFile(clusterConfigFileName).mkString)

    //val clusterXml = ClusterGenerator.createCluster(clusterConfig, 13)
    //val storesXml = StoresGenerator.createStoreDescriptor("test", "foo", "bar", 1, Math.min(clusterConfig.instances.length, 3), Math.min(clusterConfig.instances.length, 3))

    ssh(clusterConfig)

    rsync(clusterConfig, voldemortConfigDir + "/config/cluster.xml", "config/kickstart/config/cluster.xml")
    rsync(clusterConfig, voldemortConfigDir + "/config/server.properties", "config/kickstart/config/server.properties")
    rsync(clusterConfig, voldemortConfigDir + "/config/stores.xml", "config/kickstart/config/stores.xml")
  }

  def ssh(clusterConfig: Ec2ClusterConfig) = {
    val commands: Map[String, String] = Map[String, String]()

    clusterConfig.instances.foreach({ instance =>
      commands(instance.externalHostName) = "ssh -i " + clusterConfig.privateKeyFile + " -o StrictHostKeyChecking=no " + clusterConfig.userId + "@" + instance.externalHostName + " \"mkdir -p voldemort/config/kickstart/config\""
    })

    remoteExec(commands)
  }

  def rsync(clusterConfig: Ec2ClusterConfig, sourceFile: String, destinationDirectory: String) = {
    val commands: Map[String, String] = Map[String, String]()

    clusterConfig.instances.foreach({ instance =>
      commands(instance.externalHostName) = "rsync -vazc -r --delete --progress --exclude=.git -e \"ssh -i " + clusterConfig.privateKeyFile + " -o StrictHostKeyChecking=no\" " + sourceFile + " " + clusterConfig.userId + "@" + instance.externalHostName + ":" + clusterConfig.voldemortHome + "/" + destinationDirectory
    })

    remoteExec(commands)
  }

  def remoteExec(commands: Map[String, String]) = {
    val latch = new CountDownLatch(commands.size)

    commands.foreach({
      case (hostName, argLine) =>
        spawn {
          val args = CommandLineParser.parse(argLine)
          val cmd = new UnixCommand(hostName, args)
          cmd.execute(TestLogger)

          latch.countDown();
        }
    })

    latch.await(WAIT_TIME, TimeUnit.SECONDS)
  }

}

object TestLogger extends CommandOutputListener {

  def outputReceived(hostName: String, line: String) = {
    if (line.contains("Exception") || line.startsWith("\tat")) {
      System.err.println(hostName + ": " + line);
    } else {
      System.out.println(hostName + ": " + line);
    }
  }

}