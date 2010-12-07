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

/**
 * Ec2Instance represents the combination of an EC2 instance ID, and its external and internal names.
 * An EC2 instance has two different host names. The external
 * name (e.g. ec2-72-44-40-78.compute-1.amazonaws.com) and an internal one
 * (domU-12-31-39-06-BE-25.compute-1.internal).
 */

class Ec2Instance(val instanceId: String, val externalHostName: String, val internalHostName: String) {

  override def hashCode() = {
    val prime = 31
    var result = 1
    result = prime * result + instanceId.hashCode
    result = prime * result + externalHostName.hashCode
    result = prime * result + internalHostName.hashCode
    result
  }

  override def equals(other: Any) = other match {
    case that: Ec2Instance =>
      this.externalHostName == that.externalHostName &&
        this.externalHostName == that.externalHostName &&
        this.internalHostName == that.internalHostName
    case _ => false
  }

  override def toString() = "instanceId: " + instanceId +
    ", externalHostName: " + externalHostName +
    ", internalHostName: " + internalHostName

}
