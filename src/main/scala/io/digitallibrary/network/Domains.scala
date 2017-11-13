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
    "test" -> "https://api.test.digitallibrary.io",
    "staging" -> "https://api.staging.digitallibrary.io",
    "prod" -> "https://api.digitallibrary.io"
  ).getOrElse(environment, s"https://$environment.api.digitallibrary.io")


}
