import scoverage.ScoverageKeys

val ScoverageExclusionPatterns = List(
  "<empty>",
  "app.*",
  "config.*",
  "views.*",
  ".*Routes.*",
  ".*Reverse.*"
)

lazy val scoverageSettings = {
  Seq(
    ScoverageKeys.coverageExcludedPackages := ScoverageExclusionPatterns.mkString("", ";", ""),
    ScoverageKeys.coverageMinimum := 85,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    scoverageSettings,
    name := """pool-calendar""",
    organization := "com.example",
    version := "1.0",
    scalaVersion := "2.13.0",
    libraryDependencies += guice,
    libraryDependencies ++= Seq(
      "org.reactivemongo"      %% "play2-reactivemongo" % "0.18.6-play27",
      "org.mindrot"             % "jbcrypt"             % "0.3m",
      "org.scalatestplus.play" %% "scalatestplus-play"  % "4.0.3" % Test,
      "org.mockito"             % "mockito-core"        % "3.0.0" % Test
    ),
    dependencyOverrides ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.5.25",
      "com.typesafe.akka" %% "akka-slf4j" % "2.5.25"
    )
  )


// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
