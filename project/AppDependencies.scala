import sbt._

object AppDependencies {

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "org.reactivemongo"      %% "play2-reactivemongo"            % "1.1.0-play28-RC11",
    "org.reactivemongo"      %% "reactivemongo-play-json-compat" % "1.1.0-play28-RC11",
    "org.mindrot"             % "jbcrypt"                        % "0.4"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatestplus.play" %% "scalatestplus-play"  % "5.1.0"   % Test,
    "org.scalatestplus"      %% "mockito-3-4"         % "3.2.9.0" % Test,
    "org.mockito"             % "mockito-core"        % "3.12.4"  % Test
  )

  def apply(): Seq[ModuleID] = compile ++ test

  val akkaVersion = "2.6.20"
  val akkaHttpVersion = "10.1.12"

  val overrides = Seq(
    "com.typesafe.akka"            % "akka-stream_2.13"        % akkaVersion,
    "com.typesafe.akka"            % "akka-protobuf_2.13"      % akkaVersion,
    "com.typesafe.akka"            % "akka-slf4j_2.13"         % akkaVersion,
    "com.typesafe.akka"            % "akka-actor_2.13"         % akkaVersion,
    "com.typesafe.akka"            % "akka-http-core_2.13"     % akkaHttpVersion,
    "com.typesafe"                 % "config"                  % "1.4.2",
    "com.google.guava"             % "guava"                   % "30.1.1-jre",
    "com.fasterxml.jackson.core"   % "jackson-databind"        % "2.11.4",
    "com.fasterxml.jackson.core"   % "jackson-annotations"     % "2.10.5",
    "org.slf4j"                    % "slf4j-api"               % "2.0.6",
    "org.scala-lang.modules"       % "scala-java8-compat_2.13" % "1.0.2"
  )
}
