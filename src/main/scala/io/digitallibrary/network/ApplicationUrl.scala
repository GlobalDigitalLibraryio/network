/*
 * Part of GDL network.
 * Copyright (C) 2017 Global Digital Library
 *
 * See LICENSE
 */

package io.digitallibrary.network

import javax.servlet.http.HttpServletRequest

object ApplicationUrl {
  val X_FORWARDED_PROTO_HEADER = "X-Forwarded-Proto"
  val FORWARDED_HEADER = "Forwarded"
  val FORWARDED_PROTO = "proto"
  val HTTP = "http"
  val HTTPS = "https"
  val HTTP_PORT = 80
  val HTTPS_PORT = 443

  val applicationUrl = new ThreadLocal[String]
  val applicationHost = new ThreadLocal[String]

  def set(request: HttpServletRequest) {
    val xForwardedProtoHeaderProtocol = Option(request.getHeader(X_FORWARDED_PROTO_HEADER))
    val forwardedHeaderProtocol = Option(request.getHeader(FORWARDED_HEADER)) flatMap (_.replaceAll("\\s","").split(";").find(_.contains(FORWARDED_PROTO)).map(_.dropWhile(c => c != '=').tail))
    val schemeProtocol = if (request.getServerPort == HTTP_PORT || request.getServerPort == HTTPS_PORT) Some(request.getScheme) else None

    val chosenProtocol = List(forwardedHeaderProtocol, xForwardedProtoHeaderProtocol, schemeProtocol).find(x => x.isDefined && (x.get.equals(HTTP) || x.get.equals(HTTPS))).flatten

    if (chosenProtocol.isDefined) {
      applicationUrl.set(s"${chosenProtocol.get}://${request.getServerName}${request.getServletPath}/")
      applicationHost.set(s"${chosenProtocol.get}://${request.getServerName}")
    } else {
      applicationUrl.set(s"${request.getScheme}://${request.getServerName}:${request.getServerPort}${request.getServletPath}/")
      applicationHost.set(s"${request.getScheme}://${request.getServerName}:${request.getServerPort}")
    }
  }

  def get: String = applicationUrl.get
  def getHost: String = applicationHost.get

  def clear(): Unit = applicationUrl.remove()
}
