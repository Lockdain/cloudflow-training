import sbt.Keys._
import sbt._

val akkaHttpSprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.10"
val log4j2Version    = "2.13.0"
val log4jApi         = "org.apache.logging.log4j" % "log4j-api" % log4j2Version
val log4jSlf4j       = "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4j2Version
val log4jCore        = "org.apache.logging.log4j" % "log4j-core" % log4j2Version
val disruptor        = "com.lmax" % "disruptor" % "3.4.2" // for async log4j2
val scalaTest        = "org.scalatest" %% "scalatest" % "3.0.8" % "test"
val log4j            = "ch.qos.logback" % "logback-classic" % "1.2.3"


lazy val root =
  Project(id = "cloudflow-training", base = file("."))
    .settings(
      name := "cloudflow-training",
      skip in publish := true,
      version := "0.1"
    )
  .withId("cloudflow-training")
  .settings(commonSettings)
  .aggregate(
      cloudflowPipeline,
      paymentHttpIngress,
      paymentValidator,
      paymentInvalidLogger,
      paymentWindowingFlink,
      paymentCollectorSpark,
      datamodel
  )

lazy val cloudflowPipeline = appModule("cloudflow-pipeline")
  .enablePlugins(CloudflowApplicationPlugin)
  .settings(commonSettings)
  .settings(
    name := "cloudflow-pipeline"
  )
  .dependsOn(
    paymentHttpIngress,
    paymentValidator,
    paymentInvalidLogger,
    paymentWindowingFlink,
    paymentCollectorSpark)

lazy val paymentHttpIngress = appModule("payment-http-ingress")
  .enablePlugins(CloudflowAkkaStreamsLibraryPlugin)
  .settings(commonSettings)
  .settings(
    name := "payment-http-ingress",
    libraryDependencies ++= Seq(
      akkaHttpSprayJson,
      log4jApi,
      log4jSlf4j,
      log4jCore,
      disruptor,
      scalaTest
    )
  )
  .dependsOn(datamodel)


lazy val paymentValidator = appModule("payment-validator")
  .enablePlugins(CloudflowAkkaStreamsLibraryPlugin)
  .settings(commonSettings)
  .settings(
name := "payment-validator",
libraryDependencies ++= Seq(
log4jApi,
log4jSlf4j,
log4jCore,
disruptor,
scalaTest
)
)
  .dependsOn(datamodel)

lazy val paymentInvalidLogger = appModule("payment-invalid-logger")
  .enablePlugins(CloudflowAkkaStreamsLibraryPlugin)
  .settings(commonSettings)
  .settings(
    name := "payment-invalid-logger",
    libraryDependencies ++= Seq(
      log4jApi,
      log4jSlf4j,
      log4jCore,
      disruptor,
      scalaTest
    )
  )
  .dependsOn(datamodel)

lazy val paymentWindowingFlink = appModule("payment-windowing-flink")
  .enablePlugins(CloudflowFlinkLibraryPlugin)
  .settings(commonSettings)
  .settings(
    name := "payment-windowing-flink",
    libraryDependencies ++= Seq(
      log4jApi,
      log4jSlf4j,
      log4jCore,
      disruptor,
      scalaTest
    )
  )
  .dependsOn(datamodel)

lazy val paymentCollectorSpark = appModule("payment-collector-spark")
  .enablePlugins(CloudflowSparkLibraryPlugin)
  .settings(commonSettings)
  .settings(
    name := "payment-collector-spark",
    libraryDependencies ++= Seq(
      log4jApi,
      log4jSlf4j,
      log4jCore,
      disruptor,
      scalaTest
    )
  )
  .dependsOn(datamodel)


lazy val datamodel = appModule("datamodel")
  .enablePlugins(CloudflowLibraryPlugin)
  .settings(
    commonSettings,
    (sourceGenerators in Compile) += (avroScalaGenerateSpecific in Test).taskValue
  )


lazy val commonSettings = Seq(
  scalaVersion := "2.12.10",
  scalacOptions ++= Seq(
    "-target:jvm-1.8",
    "-deprecation", // Emit warning and location for usages of deprecated APIs.
    "-encoding",
    "utf-8", // Specify character encoding used by source files.
    "-Xlog-reflective-calls",
    "-Xlint",
    "-explaintypes", // Explain type errors in more detail.
    "-feature",      // Emit warning and location for usages of features that should be imported explicitly.
    "-language:_",
    "-unchecked",                       // Enable additional warnings where generated code depends on assumptions.
    "-Xcheckinit",                      // Wrap field accessors to throw an exception on uninitialized access.
    "-Xfatal-warnings",                 // Fail the compilation if there are any warnings.
    "-Xfuture",                         // Turn on future language features.
    "-Xlint:adapted-args",              // Warn if an argument list is modified to match the receiver.
    "-Xlint:by-name-right-associative", // By-name parameter of right associative operator.
    "-Xlint:constant",                  // Evaluation of a constant arithmetic expression results in an error.
    "-Xlint:delayedinit-select",        // Selecting member of DelayedInit.
    "-Xlint:doc-detached",              // A Scaladoc comment appears to be detached from its element.
    "-Xlint:inaccessible",              // Warn about inaccessible types in method signatures.
    "-Xlint:infer-any",                 // Warn when a type argument is inferred to be `Any`.
    "-Xlint:missing-interpolator",      // A string literal appears to be missing an interpolator id.
    "-Xlint:nullary-override",          // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Xlint:nullary-unit",              // Warn when nullary methods return Unit.
    "-Xlint:option-implicit",           // Option.apply used implicit view.
    "-Xlint:package-object-classes",    // Class or object defined in package object.
    "-Xlint:poly-implicit-overload",    // Parameterized overloaded implicit methods are not visible as view bounds.
    "-Xlint:private-shadow",            // A private field (or class parameter) shadows a superclass field.
    "-Xlint:stars-align",               // Pattern sequence wildcard must align with sequence component.
    "-Xlint:type-parameter-shadow",     // A local type parameter shadows a type already in scope.
    "-Xlint:unsound-match",             // Pattern match may not be typesafe.
    "-Yno-adapted-args",                // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
    "-Ypartial-unification",            // Enable partial unification in type constructor inference
    "-Ywarn-dead-code",                 // Warn when dead code is identified.
    "-Ywarn-extra-implicit",            // Warn when more than one implicit parameter section is defined.
    "-Ywarn-inaccessible",              // Warn about inaccessible types in method signatures.
    "-Ywarn-infer-any",                 // Warn when a type argument is inferred to be `Any`.
    "-Ywarn-nullary-override",          // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Ywarn-nullary-unit",              // Warn when nullary methods return Unit.
    "-Ywarn-unused",
    "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
    "-Ywarn-unused:imports",   // Warn if an import selector is not referenced.
    "-Ywarn-unused:locals",    // Warn if a local definition is unused.
    "-Ywarn-unused:params",    // Warn if a value parameter is unused.
    "-Ywarn-unused:patvars",   // Warn if a variable bound in a pattern is unused.
    "-Ywarn-unused:privates"   // Warn if a private member is unused.
  )
)

def appModule(moduleID: String): Project = {
  Project(id = moduleID, base = file(moduleID))
    .settings(
      name := moduleID
    )
    .withId(moduleID)
    .settings(commonSettings)
}