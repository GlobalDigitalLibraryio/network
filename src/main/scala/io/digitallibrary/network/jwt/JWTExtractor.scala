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

import scala.util.{Failure, Properties, Success, Try}


class JWTExtractor(request: HttpServletRequest) {

  implicit val formats = org.json4s.DefaultFormats

  private val jwtClaims = Option(request.getHeader("Authorization")).flatMap(authHeader => {
    val jwt = authHeader.replace("Bearer ", "")
    jwt match {
      case JsonWebToken(header, claimsSet, signature) => {
        Try(JWTClaims(claimsSet)) match {
          case Success(claims) => Some(claims)
          case Failure(_) => None
        }
      }
      case _ => None
    }
  })

  def extractUserId(): Option[String] = {
    jwtClaims.flatMap(_.user_id)
  }

  def extractUserRoles(): List[String] = {
    val raw = jwtClaims.map(_.scope).getOrElse(List.empty)
    val env = Properties.envOrElse("GDL_ENVIRONMENT", "local")
    val envSuffix = s"-$env:"
    val roles = raw.filter(_.contains(envSuffix)).map(_.replace(envSuffix, ":"))
    roles
  }

  def extractUserName(): Option[String] = {
    jwtClaims.flatMap(_.user_name)
  }
}
