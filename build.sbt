name := """politickle"""

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  // Select Play modules
  //jdbc,      // The JDBC connection pool and the play.api.db API
  //anorm,     // Scala RDBMS Library
  //javaJdbc,  // Java database API
  //javaEbean, // Java Ebean plugin
  //javaJpa,   // Java JPA plugin
  filters, // A set of built-in filters
  javaCore, // The core Java API
  // WebJars pull in client-side web libraries
  "org.webjars" %% "webjars-play" % "2.2.0",
  "org.webjars" % "bootstrap" % "2.3.1",
  "com.typesafe.play" %% "play-slick" % "0.5.0.8",
  "mysql" % "mysql-connector-java" % "5.1.27",
  // Add your own project dependencies in the form:
  // "group" % "artifact" % "version"
  "securesocial" %% "securesocial" % "2.1.2",
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
  "org.twitter4j" % "twitter4j-core" % "3.0.3"
)

resolvers += "twitter4j.org Repository" at "http://twitter4j.org/maven2"

resolvers += Resolver.url("sbt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)

resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/repo"

resolvers += "spy" at "http://files.couchbase.com/maven2/"

resolvers += "repository.jboss.org-public" at "https://repository.jboss.org/nexus/content/groups/public"

resolvers += "maven-restlet" at "http://maven.restlet.org"

resolvers += "Maven Central Server" at "http://repo1.maven.org/maven2"

play.Project.playScalaSettings
