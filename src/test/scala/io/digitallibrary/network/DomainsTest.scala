/*
 * Part of GDL network.
 * Copyright (C) 2017 Global Digital Library
 *
 * See LICENSE
 */

package io.digitallibrary.network

class DomainsTest extends UnitSuite {

  test("That local env returns local.digitallibrary.io") {
    Domains.get("local") should equal("http://local.digitallibrary.io")
  }

  test("That prod env returns prod") {
    Domains.get("prod") should equal("http://api.digitallibrary.io")
  }

  test("That ant other env returns any other env") {
    Domains.get("anyotherenv") should equal("http://anyotherenv.api.digitallibrary.io")
  }
}
