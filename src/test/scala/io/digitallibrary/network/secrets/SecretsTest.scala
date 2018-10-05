/*
 * Part of GDL network.
 * Copyright (C) 2017 Global Digital Library
 *
 * See LICENSE
 */

package io.digitallibrary.network.secrets

import java.io.ByteArrayInputStream

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{S3Object, S3ObjectInputStream}
import io.digitallibrary.network.UnitSuite
import org.apache.http.client.methods.HttpRequestBase
import org.mockito.Matchers._
import org.mockito.Mockito._

import scala.util.{Failure, Success}

class SecretsTest extends UnitSuite {

  val amazonClient = mock[AmazonS3Client]
  val s3Object = mock[S3Object]

  val ValidFileContent = s"""{
                             "${PropertyKeys.MetaResourceKey}": "database-name",
                             "${PropertyKeys.MetaServerKey}": "database-host",
                             "${PropertyKeys.MetaUserNameKey}": "database-user",
                             "${PropertyKeys.MetaPasswordKey}": "database-password",
                             "${PropertyKeys.MetaPortKey}": "1234",
                             "${PropertyKeys.MetaSchemaKey}": "database-schema",
                             "GOOGLE_API_KEY": "ABCD1234"
                           }""".stripMargin

  override def beforeEach(): Unit = {
    reset(amazonClient, s3Object)
    when(amazonClient.getObject(anyString(), anyString())).thenReturn(s3Object)
  }

  test("That empty Map is returned when env is local") {
    new Secrets(amazonClient, "local", "secretsFile").readSecrets() should equal (Success(Map()))
  }

  test("That Map containing details about database is returned when env is test") {
    when(s3Object.getObjectContent).thenReturn(new S3ObjectInputStream(new ByteArrayInputStream(ValidFileContent.getBytes("UTF-8")), mock[HttpRequestBase]))
    val secretsAttempt = new Secrets(amazonClient, "test", "secretsFile").readSecrets()

    secretsAttempt match {
      case Failure(err) => fail(err)
      case Success(secrets) => {
        secrets(PropertyKeys.MetaUserNameKey) should equal ("database-user")
        secrets(PropertyKeys.MetaPasswordKey) should equal ("database-password")
        secrets(PropertyKeys.MetaResourceKey) should equal ("database-name")
        secrets(PropertyKeys.MetaServerKey) should equal ("database-host")
        secrets(PropertyKeys.MetaPortKey) should equal ("1234")
        secrets(PropertyKeys.MetaSchemaKey) should equal ("database-schema")
        secrets("GOOGLE_API_KEY") should equal ("ABCD1234")
      }
    }
  }

  test("That Map containing details about database and API key is returned when env is test") {
    when(s3Object.getObjectContent).thenReturn(new S3ObjectInputStream(new ByteArrayInputStream(ValidFileContent.getBytes("UTF-8")), mock[HttpRequestBase]))
    val secretsAttempt = new Secrets(amazonClient, "test", "secretsFile").readSecrets()

    secretsAttempt match {
      case Failure(err) => fail(err)
      case Success(secrets) => {
        secrets(PropertyKeys.MetaUserNameKey) should equal ("database-user")
        secrets(PropertyKeys.MetaPasswordKey) should equal ("database-password")
        secrets(PropertyKeys.MetaResourceKey) should equal ("database-name")
        secrets(PropertyKeys.MetaServerKey) should equal ("database-host")
        secrets(PropertyKeys.MetaPortKey) should equal ("1234")
        secrets(PropertyKeys.MetaSchemaKey) should equal ("database-schema")
        secrets("GOOGLE_API_KEY") should equal ("ABCD1234")
      }
    }
  }

  test("That Failure is returned when not able to parse secret content") {
    when(s3Object.getObjectContent).thenReturn(new S3ObjectInputStream(new ByteArrayInputStream("ThouShaltNotParse".getBytes("UTF-8")), mock[HttpRequestBase]))
    new Secrets(amazonClient, "test", "secretsFile").readSecrets().isFailure should be (true)
  }

  test("That Failure is returned when Amazon-problems occur") {
    when(s3Object.getObjectContent).thenThrow(new RuntimeException("AmazonProblem"))
    new Secrets(amazonClient, "test", "secretsFile").readSecrets().isFailure should be (true)
  }
}
