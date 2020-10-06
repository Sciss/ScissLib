def basicJavaOpts = Seq("-source", "1.8")

lazy val commonSettings = Seq(
  version          := "1.1.5",
  organization     := "de.sciss",
  description      := "A Java library covering GUI building, application framework, and audio file I/O",
  homepage         := Some(url(s"https://github.com/Sciss/${name.value}")),
  licenses         := Seq("LGPL v2.1+" -> url("http://www.gnu.org/licenses/lgpl-2.1.txt")),
  scalaVersion     := "2.12.12", // not used
  crossPaths       := false,  // this is just a Java project
  autoScalaLibrary := false,  // this is just a Java project
  javacOptions     := Seq("-encoding", "utf8", "-Xlint:unchecked"),
  // note: --release cannot be used in conjunction with -source or -target (YEAH, GREAT THINKING THERE)
  javacOptions ++= (if (scala.util.Properties.isJavaAtLeast("9")) Seq("--release", "8") else basicJavaOpts ++ Seq("-target", "1.8")), // JDK >8 breaks API; skip scala-doc
  javacOptions in (Compile, doc) := basicJavaOpts,  // doesn't eat `-encoding`
)

lazy val root = project.in(file("."))
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(
    name := "ScissLib",
  )

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishTo := {
    Some(if (isSnapshot.value)
      "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
    else
      "Sonatype Releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
    )
  },
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  pomExtra := { val n = name.value
<scm>
  <url>git@github.com:Sciss/{n}.git</url>
  <connection>scm:git:git@git.iem.at:sciss/{n}.git</connection>
</scm>
<developers>
  <developer>
    <id>sciss</id>
    <name>Hanns Holger Rutz</name>
    <url>http://www.sciss.de</url>
  </developer>
</developers>
  }
)
