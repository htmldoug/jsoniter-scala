val `jsoniter-scala-examples` = project.in(file("."))
  .settings(
    scalaVersion := "2.13.1",
    scalacOptions ++= Seq("-Xmacro-settings:print-codecs"),
    crossScalaVersions := Seq("2.13.1", "2.12.10", "2.11.12"),
    mainClass in assembly := Some("com.github.plokhotnyuk.jsoniter_scala.examples.Example01")
  )
