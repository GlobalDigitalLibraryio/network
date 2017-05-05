/*
 * Part of GDL network.
 * Copyright (C) 2017 Global Digital Library
 *
 * See LICENSE
 */

package io.digitallibrary.network.model

case class JWTClaims(name: Option[String],
                             iss: Option[String],
                             sub: Option[String],
                             aud: Option[String],
                             exp: Option[Long],
                             iat: Option[Long],
                             app_metadata: Option[JWTAppMetadata])

case class JWTAppMetadata(ndla_id: String, roles: List[String])