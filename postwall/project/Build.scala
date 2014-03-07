import sbt._
import Keys._

import com.typesafe.sbt.SbtStartScript

object BuildSettings {
    import Resolvers._

    val akkaV = "2.2.3"
    val sprayV = "1.2-RC2"

    val buildOrganization = "com.postwall"
    val buildVersion = "1.0"
    val buildScalaVersion = "2.10.3"

    val globalSettings = Seq(
        organization := buildOrganization,
        version := buildVersion,
        scalaVersion := buildScalaVersion,
        //scalacOptions += "-deprecation",
        scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),
        fork in test := true,
        //libraryDependencies ++= Seq(slf4jSimpleTest, scalatest, jettyServerTest),
        libraryDependencies ++= Seq(
            "io.spray"            %   "spray-can"     % sprayV,
            "io.spray"            %   "spray-routing" % sprayV,
            "io.spray"            %   "spray-testkit" % sprayV,
            "io.spray"            %% "spray-json"     % "1.2.5",
            "net.liftweb"         %% "lift-json" % "2.5.1",
            "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
            "com.typesafe.akka"   %%  "akka-testkit"  % akkaV,
            "org.specs2"          %%  "specs2"        % "2.2.3" % "test",
            "org.slf4j"           %   "slf4j-nop"     % "1.6.4",
            "com.github.scala-incubator.io" %% "scala-io-core" % "0.4.2",
            "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.2",
            "io.github.cloudify"  %% "spdf"           % "1.0.0",
            "org.apache.commons"  % "commons-email"   % "1.3.2",
            "org.json4s"          %% "json4s-native"  % "3.2.4",
            "joda-time" % "joda-time" % "2.3",
            "org.joda" % "joda-convert" % "1.5",
            "com.newrelic.agent.java" % "newrelic-agent" % "3.1.1",
            "com.novus" %% "salat" % "1.9.5",
            "org.mongodb" %% "casbah" % "2.6.5"
            //"org.reactivemongo"   %% "reactivemongo"  % "0.10.0"
          ),
        resolvers := Seq(jbossRepo, akkaRepo, sonatypeRepo, sprayRepo, typesafeRepo))

    val projectSettings = Defaults.defaultSettings ++ globalSettings
}

object Resolvers {
    val sonatypeRepo = "Sonatype Release" at "http://oss.sonatype.org/content/repositories/releases"
    val jbossRepo = "JBoss" at "http://repository.jboss.org/nexus/content/groups/public/"
    val akkaRepo = "Akka" at "http://repo.akka.io/repository/"
    val sprayRepo = "spray repo" at "http://repo.spray.io/"
    val typesafeRepo = "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/"
}

object PostwallBuild extends Build {
    import BuildSettings._
    import Resolvers._

    override lazy val settings = super.settings ++ globalSettings

    lazy val root = Project("postwall",
                            file("."),
                            settings = projectSettings ++
                            Seq(
                                SbtStartScript.stage in Compile := Unit
                            )) aggregate(common, api, processing)

    lazy val api = Project("postwall-api",
                           file("api"),
                           settings = projectSettings ++
                           SbtStartScript.startScriptForClassesSettings) dependsOn(common % "compile->compile;test->test")

    lazy val processing = Project("postwall-processing",
                              file("processing"),
                              settings = projectSettings ++
                              SbtStartScript.startScriptForClassesSettings) dependsOn(common % "compile->compile;test->test")

    lazy val common = Project("postwall-common",
                           file("common"),
                           settings = projectSettings)
}

