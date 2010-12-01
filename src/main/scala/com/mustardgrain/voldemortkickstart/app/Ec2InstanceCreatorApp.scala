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

object Ec2InstanceCreatorApp {

  def main(argv: Array[String]): Unit = {
    val accessId = ""
    val secretKey = ""
    val regionUrl = ""
    val ami = ""
    val keypairId = ""
    val instanceCount = 0
    val instanceType = Ec2InstanceType.DEFAULT

    val ec2Connection: Ec2Connection = Ec2Connection(accessId, secretKey, regionUrl)
    val hostNamePairs = ec2Connection.createInstances(ami,
      keypairId,
      instanceType,
      instanceCount)
  }

}
