import sbt._
import de.element34.sbteclipsify._

class VoldemortKickstartProject(info: ProjectInfo) extends DefaultProject(info) with Eclipsify {

  val sbtEclipsify = "de.element34" % "sbt-eclipsify" % "0.7.0"
  val commonsIo = "commons-io" % "commons-io" % "1.4"
  val commonsLang = "commons-lang" % "commons-lang" % "2.4"
  val log4j = "log4j" % "log4j" % "1.2.13"
  val joptSimple = "net.sf.jopt-simple" % "jopt-simple" % "3.2"
  val httpComponentsClient = "org.apache.httpcomponents" % "httpcomponents-client" % "4.0.1"
  //val httpComponentsCore = "org.apache.httpcomponents" % "httpcomponents-core" % "4.0.1"
  val typica = "com.google.code.typica" % "typica" % "1.7.2"

}
