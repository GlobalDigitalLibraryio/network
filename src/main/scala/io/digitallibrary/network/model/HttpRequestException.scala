/*
 * Part of GDL network.
 * Copyright (C) 2017 Global Digital Library
 *
 * See LICENSE
 */

package io.digitallibrary.network.model

import scalaj.http.HttpResponse

class HttpRequestException(message: String, httpResponse: Option[HttpResponse[String]] = None) extends RuntimeException(message) {
  def is404:Boolean = httpResponse.exists(_.isCodeInRange(404, 404))
}

class AuthorizationException(message: String) extends RuntimeException(message)