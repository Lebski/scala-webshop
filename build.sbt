name := "shop"

version := "1.0"

lazy val `shop` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)


scalaVersion := "2.12.2"

libraryDependencies ++= Seq(jdbc, ehcache, ws, specs2 % Test, guice)

unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")

libraryDependencies += "com.github.t3hnar" %% "scala-bcrypt" % "4.0"

libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.0" % "test"
)

libraryDependencies ++= Seq(
  "com.jason-goodwin" %% "authentikat-jwt" % "0.4.5"
)