/*
 * Part of GDL network.
 * Copyright (C) 2017 Global Digital Library
 *
 * See LICENSE
 */

package io.digitallibrary.network

import javax.servlet.http.HttpServletRequest

import org.mockito.Mockito._

class ApplicationUrlTest extends UnitSuite {

  val httpRequest = mock[HttpServletRequest]
  val servername = "unittest.testesen.no"
  val scheme = "testscheme"
  val port = 666
  val path = "dette/er/en/test/path"

  override def beforeEach(): Unit = {
    reset(httpRequest)
    when(httpRequest.getServerName).thenReturn(servername)
    when(httpRequest.getScheme).thenReturn(scheme)
    when(httpRequest.getServerPort).thenReturn(port)
    when(httpRequest.getServletPath).thenReturn(path)
  }

  test("That applicationUrl returns default wnen header is not defined") {
    ApplicationUrl.set(httpRequest)
    ApplicationUrl.get should equal(s"${scheme}://${servername}:${port}${path}/")
  }

  test("That applicationHost returns default when header is not defined") {
    ApplicationUrl.set(httpRequest)
    ApplicationUrl.getHost should equal (s"${scheme}://${servername}:${port}")
  }

  test("That applicationUrl returns http wnen header is not defined and port is 80") {
    when(httpRequest.getScheme).thenReturn("http")
    when(httpRequest.getServerPort).thenReturn(80)
    ApplicationUrl.set(httpRequest)
    ApplicationUrl.get should equal(s"http://${servername}${path}/")
  }

  test("That applicationHost returns http when header is not defined and port is 80") {
    when(httpRequest.getScheme).thenReturn("http")
    when(httpRequest.getServerPort).thenReturn(80)
    ApplicationUrl.set(httpRequest)
    ApplicationUrl.getHost should equal(s"http://$servername")
  }

  test("That applicationUrl returns https wnen header is not defined and port is 443") {
    when(httpRequest.getScheme).thenReturn("https")
    when(httpRequest.getServerPort).thenReturn(443)
    ApplicationUrl.set(httpRequest)
    ApplicationUrl.get should equal(s"https://${servername}${path}/")
  }

  test("That applicationHost returns https when header is not defined and port is 443") {
    when(httpRequest.getScheme).thenReturn("https")
    when(httpRequest.getServerPort).thenReturn(443)
    ApplicationUrl.set(httpRequest)
    ApplicationUrl.getHost should equal(s"https://$servername")
  }

  test("That applicationUrl returns http when only x-forwarded-proto header and it is http") {
    when(httpRequest.getHeader(ApplicationUrl.X_FORWARDED_PROTO_HEADER)).thenReturn("http")
    ApplicationUrl.set(httpRequest)
    ApplicationUrl.get should equal(s"http://${servername}${path}/")
  }

  test("That applicationUrl returns https when only x-forwarded-proto header and it is https") {
    when(httpRequest.getHeader(ApplicationUrl.X_FORWARDED_PROTO_HEADER)).thenReturn("https")
    ApplicationUrl.set(httpRequest)
    ApplicationUrl.get should equal(s"https://${servername}${path}/")
  }

  test("That x-forwarded-proto header for https trumps http port") {
    when(httpRequest.getHeader(ApplicationUrl.X_FORWARDED_PROTO_HEADER)).thenReturn("https")
    ApplicationUrl.set(httpRequest)
    when(httpRequest.getServerPort).thenReturn(80)
    ApplicationUrl.get should equal(s"https://${servername}${path}/")
  }

  test("That x-forwarded-proto header for http trumps https port") {
    when(httpRequest.getHeader(ApplicationUrl.X_FORWARDED_PROTO_HEADER)).thenReturn("http")
    ApplicationUrl.set(httpRequest)
    when(httpRequest.getServerPort).thenReturn(443)
    ApplicationUrl.get should equal(s"http://${servername}${path}/")
  }

  test("That applicationUrl returns default when headers is unrecognized") {
    when(httpRequest.getHeader(ApplicationUrl.X_FORWARDED_PROTO_HEADER)).thenReturn("tullogtoys")
    when(httpRequest.getHeader(ApplicationUrl.FORWARDED_HEADER)).thenReturn("for=1.2.3.4;proto=tullogtoys")
    ApplicationUrl.set(httpRequest)
    ApplicationUrl.get should equal(s"${scheme}://${servername}:${port}${path}/")
  }

  test("That forwarded header for https trumps http port and x-forwarded-header") {
    when(httpRequest.getHeader(ApplicationUrl.X_FORWARDED_PROTO_HEADER)).thenReturn("http")
    when(httpRequest.getHeader(ApplicationUrl.FORWARDED_HEADER)).thenReturn("for=1.2.3.4;proto=https")
    ApplicationUrl.set(httpRequest)
    when(httpRequest.getServerPort).thenReturn(80)
    ApplicationUrl.get should equal(s"https://${servername}${path}/")
  }

  test("That forwarded header for http trumps https port and x-forwarded-header") {
    when(httpRequest.getHeader(ApplicationUrl.X_FORWARDED_PROTO_HEADER)).thenReturn("https")
    when(httpRequest.getHeader(ApplicationUrl.FORWARDED_HEADER)).thenReturn("for=1.2.3.4;proto=http")
    ApplicationUrl.set(httpRequest)
    when(httpRequest.getServerPort).thenReturn(443)
    ApplicationUrl.get should equal(s"http://${servername}${path}/")
  }

  test("That applicationUrl returns x-forwarded-proto-header when forwarded-header is unrecognized format") {
    when(httpRequest.getHeader(ApplicationUrl.X_FORWARDED_PROTO_HEADER)).thenReturn("https")
    when(httpRequest.getHeader(ApplicationUrl.FORWARDED_HEADER)).thenReturn("tullogtoys")
    ApplicationUrl.set(httpRequest)
    ApplicationUrl.get should equal(s"https://${servername}${path}/")
  }

  test("That applicationUrl returns x-forwarded-proto-header when forwarded-header is unrecognized") {
    when(httpRequest.getHeader(ApplicationUrl.X_FORWARDED_PROTO_HEADER)).thenReturn("https")
    when(httpRequest.getHeader(ApplicationUrl.FORWARDED_HEADER)).thenReturn("for=1.2.3.4;proto=tullogtoys")
    ApplicationUrl.set(httpRequest)
    ApplicationUrl.get should equal(s"https://${servername}${path}/")
  }
}
