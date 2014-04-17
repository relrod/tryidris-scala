package me.elrod.tryidris.test

import org.scalatest.FunSuite

import argonaut._, Argonaut._
import me.elrod.tryidris._, TryIdris._
import scalaz._, Scalaz._
import scalaz.effect._

class TryIdrisTests extends FunSuite {
  test("Decodes success responses") {
    val Some(x) = interpretIO(InterpretRequest("reverse \"hello\""))
            .map(toUtf8String)
            .map(_.decodeOption[InterpretResponse])
            .unsafePerformIO

    assertResult(""""olleh" : String""", ".result")(x.result)
    assertResult(":return", ".responseType")(x.responseType)
    assertResult(":ok", ".status")(x.status)
  }
  test("Decodes error responses") {
    val Some(x) = interpretIO(InterpretRequest("thisDoesNotExist123 \"hello\""))
            .map(toUtf8String)
            .map(_.decodeOption[InterpretResponse])
            .unsafePerformIO

    assertResult("No such variable thisDoesNotExist123", ".result")(x.result)
    assertResult(":return", ".responseType")(x.responseType)
    assertResult(":error", ".status")(x.status)
  }
}
