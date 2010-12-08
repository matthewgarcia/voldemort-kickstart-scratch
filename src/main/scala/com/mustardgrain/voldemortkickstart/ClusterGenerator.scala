/*
 * Copyright 2009 LinkedIn, Inc., 2010 Mustard Grain, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License you may not
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

import scala.util._
import scala.xml._
import scala.collection.mutable._

object ClusterGenerator {

  def createCluster(clusterConfig: Ec2ClusterConfig, partitionsPerNode: Int): String = {
    val hostNames = clusterConfig.instances.map({ _.internalHostName })
    val hostNameToPartitions = scala.collection.mutable.Map[String, String]()
    val allPartitions = Random.shuffle(ListBuffer.range(0, clusterConfig.instances.length * partitionsPerNode))

    hostNames.foreach({ hostName: String =>
      val partitions = allPartitions.take(partitionsPerNode)
      allPartitions.trimStart(partitionsPerNode)
      hostNameToPartitions(hostName) = partitions.toList.sort({ _ < _ }).mkString(", ")
    })

    var nodeId = -1
    val str =
      <cluster>
        <name>{ clusterConfig.name }</name>
        {
          hostNames.map({ hostName: String =>
            <server>
              <id>{ nodeId += 1; nodeId }</id>
              <host>{ hostName }</host>
              <http-port>8081</http-port>
              <socket-port>6666</socket-port>
              <admin-port>6667</admin-port>
              <partitions>{ hostNameToPartitions(hostName) }</partitions>
            </server>
          })
        }
      </cluster>

    val pp = new PrettyPrinter(80, 2)
    pp.format(str)
  }

}
