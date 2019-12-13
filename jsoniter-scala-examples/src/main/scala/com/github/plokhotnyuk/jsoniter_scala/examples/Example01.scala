package com.github.plokhotnyuk.jsoniter_scala.examples

case class AppData(apps :List[(Int, String)]) extends AnyVal

object Example01 {
  def main(args: Array[String]): Unit = {
    import java.io._

    import com.github.plokhotnyuk.jsoniter_scala.macros._
    import com.github.plokhotnyuk.jsoniter_scala.core._

    implicit val codec: JsonValueCodec[AppData] = JsonCodecMaker.make(CodecMakerConfig)

    val filePath = "/tmp/app_data.json"
    val out = new FileOutputStream(filePath)
    try writeToStream[AppData](AppData(List((1, "a"), (2, "b"))), out)
    finally out.close()
    val in = new FileInputStream(filePath)
    val appData =
      try readFromStream[AppData](in)
      finally in.close()
    println(appData)
  }
}
