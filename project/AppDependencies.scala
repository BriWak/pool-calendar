import sbt._

object AppDependencies {

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "org.reactivemongo"      %% "play2-reactivemongo" % "0.20.13-play28",
    "org.mindrot"             % "jbcrypt"             % "0.4"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatestplus.play" %% "scalatestplus-play"  % "5.1.0"   % Test,
    "org.scalatestplus"      %% "mockito-3-4"         % "3.2.9.0" % Test,
    "org.mockito"             % "mockito-core"        % "3.12.4"  % Test
  )

  def apply(): Seq[ModuleID] = compile ++ test

  val akkaVersion = "2.6.10"
  val akkaHttpVersion = "10.1.12"

  val overrides = Seq(
    "com.typesafe.akka"           %% "akka-stream_2.13"     % akkaVersion,
    "com.typesafe.akka"           %% "akka-protobuf_2.13"   % akkaVersion,
    "com.typesafe.akka"           %% "akka-slf4j_2.13"      % akkaVersion,
    "com.typesafe.akka"           %% "akka-actor_2.13"      % akkaVersion,
    "com.typesafe.akka"           %% "akka-http-core_2.13"  % akkaHttpVersion,
    "com.typesafe"                %% "ssl-config-core"      % "0.4.2",
    "com.google.guava"             % "guava"                % "28.2-jre",
    "com.fasterxml.jackson.core"   % "jackson-databind"     % "2.10.5.1",
    "com.fasterxml.jackson.core"   % "jackson-annotations"  % "2.10.5",
    "org.slf4j"                    % "slf4j-api"            % "1.7.30"
  )
}
