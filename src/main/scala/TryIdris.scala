package me.elrod.tryidris

import argonaut._, Argonaut._
import java.io.{ DataOutputStream, InputStream }
import java.net.{ HttpURLConnection, URL, URLEncoder }
import scala.io.{ Codec, Source }
import scalaz._, Scalaz._
import scalaz.effect._
//import scalaz.stream._
//import scalaz.concurrent.Task

sealed trait Request
case class InterpretRequest(expression: String) extends Request
// TODO: CompileRequest?

sealed trait Response
case class Token(startChar: Int, length: Int, metadata: List[(String, String)])
case class InterpretResponse(responseType: String, status: String, result: String, tokens: Option[List[Token]]) extends Response

object TryIdris {
  implicit def RequestCodecJson: CodecJson[InterpretRequest] =
    casecodec1(InterpretRequest.apply, InterpretRequest.unapply)("expression")

  implicit def TokenDecodeJson: DecodeJson[Token] =
    DecodeJson(c => for {
      startChar <- c.\\.as[Int]
      length    <- (c.\\ :->- 1).as[Int]
      metadata  <- (c.\\ :->- 2).as[List[(String, String)]]
    } yield Token(startChar, length, metadata))

  implicit def ResponseDecodeJson: DecodeJson[InterpretResponse] =
    DecodeJson(c => {
      val first = c.\\
      val second = (first =\ 0) :->- 1
      val tokens = second.\\ :->- 2
      for {
        responseType <- (first =\ 0).as[String]
        status       <- second.\\.as[String]
        result       <- (second.\\ :->- 1).as[String]
        tokens       <- (second.\\ :->- 2).as[Option[List[Token]]]
      } yield InterpretResponse(responseType, status, result, tokens)
    })

  // TODO: Make a reasonable/purely functional HTTP client?
  // Returns an InputStream so that we can nicely use scalaz-stream one day.
  def interpretIO(r: InterpretRequest): IO[InputStream] = IO {
    val endpoint = new URL("http://www.tryidris.org/interpret")
    val connection: HttpURLConnection = endpoint.openConnection.asInstanceOf[HttpURLConnection]
    connection.setRequestMethod("POST")
    connection.setDoOutput(true)
    connection.setRequestProperty("Content-Type", "application/json")
    val os = new DataOutputStream(connection.getOutputStream)
    os.writeBytes(r.asJson.toString)
    os.close
    connection.getInputStream
  }

  def toUtf8String(i: InputStream): String =
    Source.fromInputStream(i)(Codec.UTF8).mkString

  def toJson: String => String \/ InterpretResponse = _.decodeEither[InterpretResponse]
}

