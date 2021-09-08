import scoverage.ScoverageKeys

val ScoverageExclusionPatterns = List(
  "<empty>",
  "app.*",
  "config.*",
  "views.*",
  ".*Reverse.*",
  ".*Routes.*",
  ".*repositories.*",
  ".*HttpPageErrorHandler.*"
)

lazy val scoverageSettings = {
  Seq(
    ScoverageKeys.coverageExcludedPackages := ScoverageExclusionPatterns.mkString("", ";", ""),
    ScoverageKeys.coverageMinimum := 80,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    scoverageSettings,
    inConfig(Test)(testSettings),
    name := """pool-calendar""",
    organization := "com.example",
    version := "1.0",
    scalaVersion := "2.13.0",
    libraryDependencies += guice,
    libraryDependencies ++= AppDependencies(),
    dependencyOverrides ++= AppDependencies.overrides
  )

lazy val testSettings: Seq[Def.Setting[_]] = Seq(
  fork        := true,
  javaOptions ++= Seq(
    "-Dconfig.resource=test.application.conf"
  )
)