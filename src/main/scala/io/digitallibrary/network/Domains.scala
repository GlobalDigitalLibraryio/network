/*
 * Part of GDL network.
 * Copyright (C) 2017 Global Digital Library
 *
 * See LICENSE
 */

package io.digitallibrary.network

object Domains {

  def get(environment: String): String = Map(
    "local" -> "http://local.digitallibrary.io",
    "prod" -> "https://api.digitallibrary.io"
  ).getOrElse(environment, s"https://api.$environment.digitallibrary.io")


}
