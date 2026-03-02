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
    ScoverageKeys.coverageMinimumStmtTotal := 80,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    scoverageSettings,
    (Runtime / managedClasspath) += (Assets / packageBin).value,
    inConfig(Test)(testSettings),
    name := """pool-calendar""",
    organization := "com.example",
    version := "1.5",
    scalaVersion := "2.13.16",
    libraryDependencies += guice,
    libraryDependencies ++= AppDependencies(),
    dependencyOverrides ++= AppDependencies.overrides,
    Universal / javaOptions ++= Seq(
      "-J-Xmx256m",
      "-J-Xms128m",
      "-J-Xss512k",
      "-J-XX:+UseG1GC",
      "-Dpidfile.path=/dev/null"
    )
  )

lazy val testSettings: Seq[Def.Setting[_]] = Seq(
  fork        := true,
  javaOptions ++= Seq(
    "-Dconfig.resource=test.application.conf"
  )
)