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
import scala.actors._
import scala.actors.Actor._
import scala.concurrent.ops._

object Tester {

  private val WAIT_TIME = 3000

  def main(args: Array[String]): Unit = {
    val clusterConfigFileName = args(0)
    val clusterConfig = Ec2ClusterConfig.fromString(Source.fromFile(clusterConfigFileName).mkString)

    //val clusterXml = ClusterGenerator.createCluster(clusterConfig, 13)
    //val storesXml = StoresGenerator.createStoreDescriptor("test", "foo", "bar", 1, Math.min(clusterConfig.instances.length, 3), Math.min(clusterConfig.instances.length, 3))
    rsync(clusterConfig, "/home/kirk/dev/voldemort/li-r1044/config/single_node_cluster/config/cluster.xml", "config/kickstart/config/cluster.xml")
    rsync(clusterConfig, "/home/kirk/dev/voldemort/li-r1044/config/single_node_cluster/config/server.properties", "config/kickstart/config/server.properties")
    rsync(clusterConfig, "/home/kirk/dev/voldemort/li-r1044/config/single_node_cluster/config/stores.xml", "config/kickstart/config/stores.xml")
    //ssh(clusterConfig, "cd voldemort ; ./bin/voldemort-server.sh kickstart/config")
  }

  def ssh(clusterConfig: Ec2ClusterConfig, body: String) = {
    def caller = self

    clusterConfig.instances.foreach({ instance =>
      actor {
        val argLine = "ssh -i " + clusterConfig.privateKeyFile + " -o StrictHostKeyChecking=no " + clusterConfig.userId + "@" + instance.externalHostName + " \"" + body + "\""
        val args = CommandLineParser.parse(argLine)
        val cmd = new UnixCommand(instance.externalHostName, args)
        cmd.execute(TestLogger)

        caller ! null
      }
    })

    var continue = clusterConfig.instances.length

    while (continue > 0) {
      receiveWithin(WAIT_TIME) {
        case TIMEOUT => System.out.println("receiving Timeout")
        case _ => continue -= 1
      }
    }
  }

  def rsync(clusterConfig: Ec2ClusterConfig, sourceFile: String, destinationDirectory: String) = {
    clusterConfig.instances.foreach({ instance =>
      spawn {
        val argLine = "rsync -vazc -r --delete --progress --exclude=.git -e \"ssh -i " + clusterConfig.privateKeyFile + " -o StrictHostKeyChecking=no\" " + sourceFile + " " + clusterConfig.userId + "@" + instance.externalHostName + ":" + clusterConfig.voldemortHome + "/" + destinationDirectory
        val args = CommandLineParser.parse(argLine)
        val cmd = new UnixCommand(instance.externalHostName, args)
        cmd.execute(TestLogger)

        println("finished")
      }
    })

    println("totally finished")
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