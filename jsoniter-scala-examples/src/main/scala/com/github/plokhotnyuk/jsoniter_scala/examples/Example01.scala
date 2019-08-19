package com.github.plokhotnyuk.jsoniter_scala.examples

import com.github.plokhotnyuk.jsoniter_scala.core._
import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker._
import com.github.plokhotnyuk.jsoniter_scala.examples.Nullable._

final class Nullable[+A <: AnyRef] private(private val value: A) extends AnyVal {
  def isEmpty: Boolean = value eq null

  def isDefined: Boolean = value ne null

  def get: A =
    if (value ne null) value
    else throw new NoSuchElementException

  def getOrElse[B >: A <: AnyRef](default: => B): B =
    if (value ne null) value
    else default

  def flatMap[B <: AnyRef](f: A => Nullable[B]): Nullable[B] =
    if (value ne null) f(value)
    else new Nullable(null.asInstanceOf[B])

  def map[B <: AnyRef](f: A => B) : Nullable[B] = new Nullable({
    if (value ne null) f(value)
    else null.asInstanceOf[B]
  })

  def toOption: Option[A] = if (value ne null) new Some(value) else None

  override def toString: String = s"Nullable($value)"
}

object Nullable {
  def none[A <: AnyRef]: Nullable[A] = new Nullable(null.asInstanceOf[A])

  implicit def apply[A <: AnyRef](value: A): Nullable[A] = new Nullable(value)

  def unapply[A <: AnyRef](n: Nullable[A]): Option[A] = n.toOption

  def toNullableCodec[A <: AnyRef](codec: JsonValueCodec[A]): JsonValueCodec[Nullable[A]] =
    new JsonValueCodec[Nullable[A]] {
      override def decodeValue(in: JsonReader, default:  Nullable[A]): Nullable[A] =
        if (in.isNextToken('n')) in.readNullOrError(default, "expected JSON value")
        else {
          in.rollbackToken()
          new Nullable(codec.decodeValue(in, default.value))
        }

      override def encodeValue(x: Nullable[A], out: JsonWriter): Unit =
        if (x == nullValue) out.writeNull()
        else codec.encodeValue(x.value, out)

      override val nullValue: Nullable[A] = new Nullable(null.asInstanceOf[A])
    }
}

/**
  * Example of basic usage from README.md
  */
object Example01 {
  case class Device(id: Int, model: Nullable[String])

  case class User(name: String, devices: Seq[Device])

  implicit val nullableStringCodec: JsonValueCodec[Nullable[String]] = toNullableCodec(make(CodecMakerConfig()))
  implicit val codec: JsonValueCodec[User] = make(CodecMakerConfig())

  def main(args: Array[String]): Unit = {
    val user = readFromArray[User]("""{"name":"John","devices":[{"id":1,"model":null}]}""".getBytes("UTF-8"))
    val json = writeToArray(User(name = "John", devices = Seq(Device(id = 1, model = none))))

    println(user)
    println(new String(json, "UTF-8"))
  }
}
