package com.github.plokhotnyuk.jsoniter_scala.examples

import com.github.plokhotnyuk.jsoniter_scala.core._
import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker._
import com.github.plokhotnyuk.jsoniter_scala.examples.Maybe._

final class Maybe[+A <: AnyRef] private(private val value: A) extends AnyVal {
  def isDefined: Boolean = value ne null

  def isEmpty: Boolean = value eq null

  def get: A =
    if (value ne null) value
    else throw new NoSuchElementException

  def getOrElse[B >: A <: AnyRef](default: => B): B =
    if (value ne null) value
    else default

  def getOrNull: A = value

  def flatMap[B <: AnyRef](f: A => Maybe[B]): Maybe[B] =
    if (value ne null) f(value)
    else new Maybe(null.asInstanceOf[B])

  def map[B <: AnyRef](f: A => B) : Maybe[B] = new Maybe({
    if (value ne null) f(value)
    else null.asInstanceOf[B]
  })

  def toOption: Option[A] =
    if (value ne null) new Some(value)
    else None

  override def toString: String = s"Maybe($value)"
}

object Maybe {
  def none[A <: AnyRef]: Maybe[A] = new Maybe(null.asInstanceOf[A])

  implicit def apply[A <: AnyRef](value: A): Maybe[A] = new Maybe(value)

  def unapply[A <: AnyRef](n: Maybe[A]): Option[A] = n.toOption

  def toNullableCodec[A <: AnyRef](codec: JsonValueCodec[A]): JsonValueCodec[Maybe[A]] =
    new JsonValueCodec[Maybe[A]] {
      override def decodeValue(in: JsonReader, default:  Maybe[A]): Maybe[A] =
        if (in.isNextToken('n')) in.readNullOrError(default, "expected JSON value")
        else {
          in.rollbackToken()
          new Maybe(codec.decodeValue(in, default.value))
        }

      override def encodeValue(x: Maybe[A], out: JsonWriter): Unit =
        if (x == nullValue) out.writeNull()
        else codec.encodeValue(x.value, out)

      override val nullValue: Maybe[A] = new Maybe(null.asInstanceOf[A])
    }
}

/**
  * Example of basic usage from README.md
  */
object Example01 {
  case class Device(id: Int, model: Maybe[String])

  case class User(name: String, devices: Seq[Device])

  implicit val nullableStringCodec: JsonValueCodec[Maybe[String]] = toNullableCodec(make(CodecMakerConfig()))
  implicit val codec: JsonValueCodec[User] = make(CodecMakerConfig())

  def main(args: Array[String]): Unit = {
    val user = readFromArray[User]("""{"name":"John","devices":[{"id":1,"model":null}]}""".getBytes("UTF-8"))
    val json = writeToArray(User(name = "John", devices = Seq(Device(id = 1, model = none))))

    println(user)
    println(new String(json, "UTF-8"))
  }
}
