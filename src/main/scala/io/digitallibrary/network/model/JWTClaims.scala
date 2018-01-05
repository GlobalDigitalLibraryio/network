/*
 * Part of GDL network.
 * Copyright (C) 2017 Global Digital Library
 *
 * See LICENSE
 */

package io.digitallibrary.network.model

import authentikat.jwt.JwtClaimsSet
import org.json4s.native.JsonMethods.parse

case class JWTClaims(iss: Option[String],
                     sub: Option[String],
                     iat: Option[Long],
                     exp: Option[Long],
                     azp: Option[String],
                     gty: Option[String],
                     scope: List[String],
                     user_id: Option[String],
                     user_name: Option[String]
                    )

object JWTClaims {
  implicit val formats = org.json4s.DefaultFormats
  val gdl_id_key = "https://digitallibrary.io/gdl_id"
  val user_name_key = "https://digitallibrary.io/user_name"

  def apply(claims: JwtClaimsSet): JWTClaims = {
    val content = parse(claims.asJsonString).extract[Map[String, Any]]
    JWTClaims(content.get("iss").asInstanceOf[Option[String]],
      content.get("sub").asInstanceOf[Option[String]],
      content.get("iat").asInstanceOf[Option[Long]],
      content.get("exp").asInstanceOf[Option[Long]],
      content.get("azp").asInstanceOf[Option[String]],
      content.get("gty").asInstanceOf[Option[String]],
      content.get("scope").asInstanceOf[Option[String]].map(_.split(' ').toList).getOrElse(List.empty),
      content.get(gdl_id_key).asInstanceOf[Option[String]],
      content.get(user_name_key).asInstanceOf[Option[String]])
  }
}