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
    "test" -> "http://test-proxy-1865761686.eu-central-1.elb.amazonaws.com",
    "staging" -> "http://staging-proxy-95967625.eu-central-1.elb.amazonaws.com",
    "prod" -> "http://api.digitallibrary.io"
  ).getOrElse(environment, s"http://$environment.api.digitallibrary.io")


}
