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

/**
 * LoggingCommandOutputListener simply takes the output from the remote system
 * and logs it using the logging API.
 */

class LoggingCommandOutputListener(delegate:CommandOutputListener, logger:(String, Boolean) => Unit, shouldProcessExceptions:Boolean) extends DelegatingCommandOutputListener(delegate) {

    override def outputReceived(hostName:String, line:String) = {
        // If desired we can increase the checking of the exception to make it
        // more reliably differentiate real problems.
        val wasError = shouldProcessExceptions && (line.contains("Exception") || line.startsWith("\tat"))
        logger(hostName + ": " + line, wasError)
        super.outputReceived(hostName, line);
    }

}
