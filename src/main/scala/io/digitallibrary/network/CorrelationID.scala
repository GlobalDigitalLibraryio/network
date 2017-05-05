/*
 * Part of GDL network.
 * Copyright (C) 2017 Global Digital Library
 *
 * See LICENSE
 */

package io.digitallibrary.network

import java.util.UUID

object CorrelationID {
  private val correlationID = new ThreadLocal[String]

  def set(correlationId: Option[String]) = {
    correlationId match {
      case Some(x) => correlationID.set(x)
      case None => correlationID.set(UUID.randomUUID().toString)
    }
  }

  def get: Option[String] = {
    Option(correlationID.get)
  }

  def clear() = {
    correlationID.remove()
  }
}
