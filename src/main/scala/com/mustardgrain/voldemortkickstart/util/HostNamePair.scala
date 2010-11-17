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

package com.mustardgrain.voldemortkickstart.util

/**
 * HostNamePair represents a pairing of a host's external and internal names.
 * Depending on the network topology, a given server may be referenced by a
 * different name to systems outside its local network than the name used
 * internal to the local network.
 * 
 * <p/>
 * 
 * An EC2 instance, as an example, has two different host names. The external
 * name (e.g. ec2-72-44-40-78.compute-1.amazonaws.com) and an internal one
 * (domU-12-31-39-06-BE-25.compute-1.internal).
 * 
 * <p/>
 * 
 * For systems which have only one name, both the external and internal host
 * names should be the same. That is, they should be identical and neither
 * should be set to null.
 * 
 */

class HostNamePair(val externalHostName: String, val internalHostName: String) {

  override def hashCode() = {
        val prime = 31
        var result = 1
        result = prime * result + externalHostName.hashCode
        result = prime * result + internalHostName.hashCode
        result
    }

    override def equals(other: Any) = other match {
      case that: HostNamePair => this.externalHostName == that.externalHostName && this.internalHostName == that.internalHostName
      case _ => false
    }

}
