name := "tryidris"

organization := "me.elrod"

scalaVersion := "2.10.4"

resolvers += Resolver.sonatypeRepo("releases")

//addCompilerPlugin("org.brianmckenna" %% "wartremover" % "0.8")

// TODO: Figure out a way to remove cast to HTTPUrlConnection that causes this to error...
//scalacOptions in (Compile, compile) += "-P:wartremover:traverser:org.brianmckenna.wartremover.warts.Unsafe"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard"
)

libraryDependencies += "io.argonaut" %% "argonaut" % "6.0.3"

libraryDependencies += "org.scalaz.stream" %% "scalaz-stream" % "0.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.3" % "test"
