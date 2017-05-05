/*
 * Part of GDL network.
 * Copyright (C) 2017 Global Digital Library
 *
 * See LICENSE
 */

package io.digitallibrary.network

object Domains {

  def get(environment: String): String = Map(
    "local" -> "http://proxy.gdl-local",
    "prod" -> "http://api.digitallibrary.io"
  ).getOrElse(environment, s"http://$environment.api.digitallibrary.io")


}
