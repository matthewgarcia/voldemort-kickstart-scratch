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

package com.mustardgrain.voldemortkickstart

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import com.mustardgrain.voldemortkickstart.util.HostNamePair

import com.xerox.amazonws.ec2.InstanceType
import com.xerox.amazonws.ec2.Jec2
import com.xerox.amazonws.ec2.LaunchConfiguration
import com.xerox.amazonws.ec2.ReservationDescription

import scala.collection._
import scala.collection.mutable._
import scala.collection.JavaConversions._

/**
 * Ec2Connection uses the Typica
 * library (http://code.google.com/p/typica/) for EC2 access.
 */

class Ec2Connection private (private val ec2: Jec2) {

  val POLL_INTERVAL = 15

  val logger = LogFactory.getLog(getClass())

  def list(): List[HostNamePair] = {
    val hostNamePairs = new ListBuffer[HostNamePair]()
    val jList = ec2.describeInstances(new java.util.ArrayList[String]())

    for (res <- jList.toList) {
      if (res.getInstances() != null) {
        for (instance <- res.getInstances()) {
          if (!isValid(instance.getDnsName, instance.getPrivateDnsName)) {
            if (logger.isWarnEnabled())
              logger.warn("Instance " + instance.getInstanceId() + " present, but missing external and/or internal host name")
          } else {
            val hostNamePair = new HostNamePair(instance.getDnsName().trim(), instance.getPrivateDnsName().trim())
            hostNamePairs.add(hostNamePair)
          }
        }
      }
    }

    hostNamePairs.toList
  }

  def createInstances(ami: String, keypairId: String,
    instanceType: Ec2InstanceType.Value,
    instanceCount: Int): List[HostNamePair] = {
    val launchConfiguration = new LaunchConfiguration(ami)
    launchConfiguration.setInstanceType(InstanceType.valueOf(instanceType.toString))
    launchConfiguration.setKeyName(keypairId)
    launchConfiguration.setMinCount(instanceCount)
    launchConfiguration.setMaxCount(instanceCount)

    val reservationDescription = ec2.runInstances(launchConfiguration)
    val instanceIds = new ListBuffer[String]()

    for (instance <- reservationDescription.getInstances()) {
      val instanceId = instance.getInstanceId()

      if (logger.isInfoEnabled())
        logger.info("Instance " + instanceId + " launched")

      instanceIds.add(instanceId)
    }

    val hostNamePairs = new ListBuffer[HostNamePair]()
    var interrupted = false

    while (!instanceIds.isEmpty() && !interrupted) {
      try {
        if (logger.isDebugEnabled())
          logger.debug("Sleeping for " + POLL_INTERVAL + " seconds...")

        Thread.sleep(POLL_INTERVAL * 1000)
      } catch {
        case e: InterruptedException => interrupted = true
      }

      if (!interrupted) {
        for (res <- ec2.describeInstances(instanceIds)) {
          if (res.getInstances() != null) {
            for (instance <- res.getInstances()) {
              val state = String.valueOf(instance.getState()).toLowerCase()

              if (!state.equals("running")) {
                if (logger.isDebugEnabled())
                  logger.debug("Instance " + instance.getInstanceId() + " in state: " + state)
              } else {
                if (!isValid(instance.getDnsName, instance.getPrivateDnsName)) {
                  if (logger.isWarnEnabled())
                    logger.warn("Instance " + instance.getInstanceId() + " in running state, but missing external and/or internal host name")
                } else {
                  val hostNamePair = new HostNamePair(instance.getDnsName().trim(), instance.getPrivateDnsName().trim())
                  hostNamePairs.add(hostNamePair)

                  if (logger.isInfoEnabled())
                    logger.info("Instance " + instance.getInstanceId()
                      + " running with external host name: "
                      + hostNamePair.externalHostName
                      + ", internal host name: "
                      + hostNamePair.internalHostName)

                  instanceIds.remove(instance.getInstanceId())
                }
              }
            }
          }
        }
      }
    }

    return hostNamePairs.toList
  }

  /**
   * Sorry, I'm through trying to figure out how to import com.xerox.amazonws.ec2.ReservationDescription's inner class Instance.
   */

  private def isValid(dnsName: String, privateDnsName: String) = {
    dnsName != null && privateDnsName != null && !dnsName.trim().isEmpty() && !privateDnsName.trim().isEmpty()
  }

}

object Ec2Connection {

  def apply(accessId: String, secretKey: String) = {
    var ec2 = new Jec2(accessId, secretKey)
    new Ec2Connection(ec2)
  }

}