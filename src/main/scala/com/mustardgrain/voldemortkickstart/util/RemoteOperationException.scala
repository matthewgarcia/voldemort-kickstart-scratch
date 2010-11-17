package com.mustardgrain.voldemortkickstart.util

class RemoteOperationException(message: String, cause: Throwable) extends Exception(message, cause) {

  def this() = this(null, null)
  
  def this(message: String) = this(message, null)

  def this(cause: Throwable) = this(null, cause)

}

