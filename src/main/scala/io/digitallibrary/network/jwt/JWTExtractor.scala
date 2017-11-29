/*
 * Part of GDL network.
 * Copyright (C) 2017 Global Digital Library
 *
 * See LICENSE
 */

package io.digitallibrary.network.jwt

import javax.servlet.http.HttpServletRequest

import authentikat.jwt.JsonWebToken
import io.digitallibrary.network.model.JWTClaims
import org.json4s.native.Serialization.read

import scala.util.{Failure, Success, Try}


class JWTExtractor(request: HttpServletRequest) {

  implicit val formats = org.json4s.DefaultFormats

  private val jwtClaims = Option(request.getHeader("Authorization")).flatMap(authHeader => {
    val jwt = authHeader.replace("Bearer ", "")
    jwt match {
      case JsonWebToken(header, claimsSet, signature) => Some(JWTClaims(claimsSet))
      case _ => None
    }
  })

  def extractUserId(): Option[String] = {
    jwtClaims.flatMap(_.user_id)
  }

  def extractUserRoles(): List[String] = {
    jwtClaims.map(_.scope).getOrElse(List.empty)
  }

  def extractUserName(): Option[String] = {
    jwtClaims.flatMap(_.user_name)
  }
}
