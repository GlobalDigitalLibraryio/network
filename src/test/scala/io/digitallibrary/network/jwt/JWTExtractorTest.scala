/*
 * Part of GDL network.
 * Copyright (C) 2017 Global Digital Library
 *
 * See LICENSE
 */

package io.digitallibrary.network.jwt

import javax.servlet.http.HttpServletRequest

import io.digitallibrary.network.UnitSuite
import org.mockito.Mockito.when

class JWTExtractorTest extends UnitSuite {

  test("That userId is None when no authorization header is set"){
    new JWTExtractor(mock[HttpServletRequest]).extractUserId() should be (None)
  }

  test("That userId is None when an illegal JWT is set") {
    val request = mock[HttpServletRequest]
    when(request.getHeader("Authorization")).thenReturn("This is an invalid JWT")

    new JWTExtractor(request).extractUserId() should be (None)
  }

  // Tokens can be decoded at jwt.io

  test("That userId is None when no app-metadata is present") {
    val token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL3NvbWUtZG9tYWluLyIsInN1YiI6Imdvb2dsZS1vYXV0aDJ8MTIzIiwiYXVkIjoiYXNkZmFzZGYiLCJleHAiOjE0ODYwNzAwNjMsImlhdCI6MTQ4NjAzNDA2M30.KEjhvPUooLSFExTrv8XsioJks-NAMzYZjGn32MABvg4"
    val request = mock[HttpServletRequest]
    when(request.getHeader("Authorization")).thenReturn(s"Bearer $token")
    new JWTExtractor(request).extractUserId() should be (None)
  }

  test("That JWTExtractor.extractUserId is set even if roles are not present") {
    val token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJodHRwczovL2RpZ2l0YWxsaWJyYXJ5LmlvL2dkbF9pZCI6ImFiYzEyMyIsImlzcyI6Imh0dHBzOi8vc29tZS1kb21haW4vIiwic3ViIjoiZ29vZ2xlLW9hdXRoMnwxMjMiLCJhdWQiOiJhc2RmYSIsImV4cCI6MTQ4NjA3MDA2MywiaWF0IjoxNDg2MDM0MDYzfQ.sgikHG1tGpMKfRd3rv_sWQez-JcTrhKPdbJv3os1OI0"
    val request = mock[HttpServletRequest]
    when(request.getHeader("Authorization")).thenReturn(s"Bearer $token")
    val jWTExtractor = new JWTExtractor(request)
    jWTExtractor.extractUserId() should equal (Some("abc123"))
    jWTExtractor.extractUserRoles() should equal(List.empty)
  }

  test("That all roles are extracted") {
    val token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiRG9uYWxkIER1Y2siLCJpc3MiOiJodHRwczovL3NvbWUtZG9tYWluLyIsInN1YiI6Imdvb2dsZS1vYXV0aDJ8MTIzIiwiYXVkIjoiYWJjIiwiZXhwIjoxNDg2MDcwMDYzLCJpYXQiOjE0ODYwMzQwNjMsInNjb3BlIjoiaW1hZ2VzLWxvY2FsOndyaXRlIGltYWdlcy1sb2NhbDpyZWFkIGltYWdlcy1sb2NhbDphbGwifQ.AgDqNUZeey4_FAKpuhzUADlB678sGtp_-T0KR6zgLfk"
    val request = mock[HttpServletRequest]
    when(request.getHeader("Authorization")).thenReturn(s"Bearer $token")

    val jwtExtractor = new JWTExtractor(request)
    val roles = jwtExtractor.extractUserRoles()
    roles.size should be (3)
    roles.contains("images:write") should be (true)
    roles.contains("images:read") should be (true)
    roles.contains("images:all") should be (true)
  }

  test("That name is extracted") {
    val token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzY29wZSI6InJvbGUxIHJvbGUyIHJvbGUzIiwiaHR0cHM6Ly9kaWdpdGFsbGlicmFyeS5pby9nZGxfaWQiOiJhYmMxMjMiLCJodHRwczovL2RpZ2l0YWxsaWJyYXJ5LmlvL3VzZXJfbmFtZSI6IkRvbmFsZCBEdWNrIiwiaXNzIjoiaHR0cHM6Ly9zb21lLWRvbWFpbi8iLCJzdWIiOiJnb29nbGUtb2F1dGgyfDEyMyIsImF1ZCI6ImFiYyIsImV4cCI6MTQ4NjA3MDA2MywiaWF0IjoxNDg2MDM0MDYzfQ.gbMF8F1LLMUVroXbmStL02R6EPZjeZkbowseE5SAN9U"
    val request = mock[HttpServletRequest]
    when(request.getHeader("Authorization")).thenReturn(s"Bearer $token")

    val jwtExtractor = new JWTExtractor(request)
    val name = jwtExtractor.extractUserName() should equal (Some("Donald Duck"))
  }

}
