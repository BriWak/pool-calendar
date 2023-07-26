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
  .enablePlugins(PlayScala, AssemblyPlugin)
  .settings(
    scoverageSettings,
    assembly / assemblyMergeStrategy := {
      case "META-INF/io.netty.versions.properties" => MergeStrategy.first // Keep the first version found
      case "module-info.class" => MergeStrategy.first // Keep the first version found
      case "play/reference-overrides.conf" => MergeStrategy.first // Keep the first version found
      case x =>
        val oldStrategy = (assembly / assemblyMergeStrategy).value
        oldStrategy(x)
    },
    (Runtime / managedClasspath) += (Assets / packageBin).value,
    assembly / assemblyOutputPath := file("pool-calendar.jar"),
    inConfig(Test)(testSettings),
    name := """pool-calendar""",
    organization := "com.example",
    version := "1.5",
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