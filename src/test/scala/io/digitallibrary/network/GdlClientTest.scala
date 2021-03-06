/*
 * Part of GDL network.
 * Copyright (C) 2017 Global Digital Library
 *
 * See LICENSE
 */

package io.digitallibrary.network

import javax.servlet.http.HttpServletRequest

import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.TryValues._

import scalaj.http.{HttpRequest, HttpResponse}

class GdlClientTest extends UnitSuite with GdlClient {

  case class TestObject(id: String, verdi: String)

  val ParseableContent =
    """
      |{
      |  "id": "1",
      |  "verdi": "This is the value"
      |}
    """.stripMargin

  val gdlClient: GdlClient = new GdlClient

  override def beforeEach = {
    CorrelationID.clear()
  }

  test("That a HttpRequestException is returned when receiving an http-error") {
    val httpRequestMock = mock[HttpRequest]
    val httpResponseMock = mock[HttpResponse[String]]

    when(httpRequestMock.asString).thenReturn(httpResponseMock)
    when(httpRequestMock.url).thenReturn("someUrl")

    when(httpResponseMock.isError).thenReturn(true)
    when(httpResponseMock.code).thenReturn(123)
    when(httpResponseMock.statusLine).thenReturn("status")
    when(httpResponseMock.body).thenReturn("body-with-error")

    val result = gdlClient.fetch[TestObject](httpRequestMock)

    result should be a 'failure
    result.failure.exception.getMessage should equal("Received error 123 status when calling someUrl. Body was body-with-error")
  }

  test("that fetchBytes returns a Failure with HttpRequestException Failure when receiving a http-error") {
    val httpRequestMock = mock[HttpRequest]
    val httpResponseMock = mock[HttpResponse[Array[Byte]]]

    when(httpRequestMock.asBytes).thenReturn(httpResponseMock)
    when(httpRequestMock.url).thenReturn("someUrl")

    when(httpResponseMock.code).thenReturn(123)
    when(httpResponseMock.statusLine).thenReturn("status")

    when(httpResponseMock.isError).thenReturn(true)

    val result = gdlClient.fetchBytes(httpRequestMock)
    result should be a 'failure
    result.failure.exception.getMessage should equal ("Received error 123 status when calling someUrl.")
  }

  test("that fetchBytes returns a Success with bytes when all ok") {
    val httpRequestMock = mock[HttpRequest]
    val httpResponseMock = mock[HttpResponse[Array[Byte]]]

    when(httpRequestMock.asBytes).thenReturn(httpResponseMock)
    when(httpResponseMock.isError).thenReturn(false)
    when(httpResponseMock.body).thenReturn("This worked nicely".getBytes)

    val result = gdlClient.fetchBytes(httpRequestMock)
    result should be a 'success
    result.get should equal ("This worked nicely".getBytes)
  }

  test("That a HttpRequestException is returned when response is not parseable") {
    val unparseableResponse = "This string cannot be parsed to a TestObject"
    val httpRequestMock = mock[HttpRequest]
    val httpResponseMock = mock[HttpResponse[String]]

    when(httpRequestMock.asString).thenReturn(httpResponseMock)
    when(httpResponseMock.isError).thenReturn(false)
    when(httpResponseMock.body).thenReturn(unparseableResponse)

    val result = gdlClient.fetch[TestObject](httpRequestMock)
    result should be a 'failure
    result.failure.exception.getMessage should equal(s"Could not parse response $unparseableResponse")
  }

  test("That a testObject is returned when no error is returned and content is parseable") {
    val httpRequestMock = mock[HttpRequest]
    val httpResponseMock = mock[HttpResponse[String]]

    when(httpRequestMock.asString).thenReturn(httpResponseMock)
    when(httpResponseMock.isError).thenReturn(false)
    when(httpResponseMock.body).thenReturn(ParseableContent)

    val result = gdlClient.fetch[TestObject](httpRequestMock)
    result should be a 'success
    result.get.id should equal("1")
    result.get.verdi should equal("This is the value")

    verify(httpRequestMock, never()).header(any[String], any[String])
  }

  test("That CorrelationID is added to request if set on ThreadContext") {
    CorrelationID.set(Some("correlation-id"))

    val httpRequestMock = mock[HttpRequest]
    val httpResponseMock = mock[HttpResponse[String]]

    when(httpRequestMock.header(eqTo("X-Correlation-ID"), eqTo("correlation-id"))).thenReturn(httpRequestMock)
    when(httpRequestMock.asString).thenReturn(httpResponseMock)
    when(httpResponseMock.isError).thenReturn(false)
    when(httpResponseMock.body).thenReturn(ParseableContent)

    val result = gdlClient.fetch[TestObject](httpRequestMock)
    result should be a 'success
    result.get.id should equal("1")
    result.get.verdi should equal("This is the value")

    verify(httpRequestMock, times(1)).header(eqTo("X-Correlation-ID"), eqTo("correlation-id"))
  }

  test("That BasicAuth header is added to request when user and password is defined") {
    val user = "user"
    val password = "password"

    val httpRequestMock = mock[HttpRequest]
    val httpResponseMock = mock[HttpResponse[String]]

    when(httpRequestMock.auth(eqTo(user), eqTo(password))).thenReturn(httpRequestMock)
    when(httpRequestMock.asString).thenReturn(httpResponseMock)
    when(httpResponseMock.isError).thenReturn(false)
    when(httpResponseMock.body).thenReturn(ParseableContent)

    val result = gdlClient.fetchWithBasicAuth[TestObject](httpRequestMock, user, password)
    result should be a 'success
    result.get.id should equal("1")
    result.get.verdi should equal("This is the value")

    verify(httpRequestMock, times(1)).auth(eqTo(user), eqTo(password))
    verify(httpRequestMock, never()).header(any[String], any[String])
  }

  test("That Authorization header is added to request if set on Thread") {
    val servletRequestMock = mock[HttpServletRequest]
    val httpRequestMock = mock[HttpRequest]
    val httpResponseMock = mock[HttpResponse[String]]
    val authHeaderKey = "Authorization"
    val authHeader = "abc"

    when(servletRequestMock.getHeader(eqTo(authHeaderKey))).thenReturn(authHeader)
    AuthUser.set(servletRequestMock)

    when(httpRequestMock.header(eqTo(authHeaderKey), eqTo(authHeader))).thenReturn(httpRequestMock)
    when(httpRequestMock.asString).thenReturn(httpResponseMock)
    when(httpResponseMock.isError).thenReturn(false)
    when(httpResponseMock.body).thenReturn(ParseableContent)

    val result = gdlClient.fetchWithForwardedAuth[TestObject](httpRequestMock)
    result should be a 'success
    result.get.id should equal("1")
    result.get.verdi should equal("This is the value")

    verify(httpRequestMock, times(1)).header(eqTo(authHeaderKey), eqTo(authHeader))
  }
}
