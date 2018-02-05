/*
 * Part of GDL network.
 * Copyright (C) 2017 Global Digital Library
 *
 * See LICENSE
 */

package io.digitallibrary.network.secrets

import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import io.digitallibrary.network.secrets.PropertyKeys._
import org.json4s.native.Serialization.read

import scala.io.Source
import scala.util.{Properties, Success, Try}

object Secrets {
  private val amazonClient = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_CENTRAL_1).build()
  val DBKeys = Set(MetaUserNameKey, MetaPasswordKey, MetaResourceKey, MetaServerKey, MetaPortKey, MetaSchemaKey)

  def readSecrets(secretsFile: String): Try[Map[String, String]] =
    new Secrets(amazonClient, Properties.envOrElse("GDL_ENVIRONMENT", "local"), secretsFile, DBKeys).readSecrets()

  def readSecrets(secretsFile: String, secretsKeysToRead: Set[String], readDBCredentials: Boolean = true): Try[Map[String, String]] = {
    val keysToRead = secretsKeysToRead ++ (if (readDBCredentials) DBKeys else Set())
    new Secrets(amazonClient, Properties.envOrElse("GDL_ENVIRONMENT", "local"), secretsFile, keysToRead).readSecrets()
  }
}

object PropertyKeys {
  val MetaUserNameKey = "META_USER_NAME"
  val MetaPasswordKey = "META_PASSWORD"
  val MetaResourceKey = "META_RESOURCE"
  val MetaServerKey = "META_SERVER"
  val MetaPortKey = "META_PORT"
  val MetaSchemaKey = "META_SCHEMA"
}

class Secrets(amazonClient: AmazonS3, environment: String, secretsFile: String, secretKeysToRead: Set[String]) {

  def readSecrets(): Try[Map[String, String]] = {
    implicit val formats = org.json4s.DefaultFormats

    environment match {
      case "local" => Success(Map())
      case _ =>
        Try(amazonClient.getObject(s"$environment.secrets.gdl", secretsFile)).flatMap(s3Object => {
          Try(Source.fromInputStream(s3Object.getObjectContent).getLines().mkString).flatMap(fileContent => {
            Try(read[Map[String, String]](fileContent)).map(allSecrets => {
              allSecrets.filterKeys(key => secretKeysToRead.contains(key))
            })
          })
        })
    }
  }
}

case class Database(database: String,
                    host: String,
                    user: String,
                    password: String,
                    port: String,
                    schema: String)
