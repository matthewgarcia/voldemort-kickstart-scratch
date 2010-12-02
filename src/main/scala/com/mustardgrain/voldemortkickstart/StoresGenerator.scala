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

import java.io.PrintWriter
import java.io.StringWriter
import java.util.List

import scala.xml._

/**
 * StoresGenerator generates the stores.xml file given either a store name along
 * with zone specific replication factors. Currently hard-coding the serializers
 * to string format
 * 
 */
class StoresGenerator {

  def createStoreDescriptor(storeName: String,
    persistence: String,
    routingStrategy: String,
    requiredReads: Int,
    requiredWrites: Int,
    totalReplicationFactor: Int): String = {
    val str =
      <stores>
        <store>
          <name>{ storeName }</name>
          <persistence>{ persistence }</persistence>
          <routing>client</routing>
          <routing-strategy>{ routingStrategy }</routing-strategy>
          <key-serializer>
            <type>string</type>
            <schema-info>UTF-8</schema-info>
          </key-serializer>
          <value-serializer>
            <type>string</type>
            <schema-info>UTF-8</schema-info>
          </value-serializer>
          <required-reads>{ requiredReads }</required-reads>
          <required-writes>{ requiredWrites }</required-writes>
          <replication-factor>{ totalReplicationFactor }</replication-factor>
        </store>
      </stores>

    val pp = new PrettyPrinter(80, 2)
    println(pp.format(str))
    str.toString
  }

}
