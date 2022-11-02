import org.scalatest.funsuite.AnyFunSuite
import com.typesafe.config.{Config, ConfigFactory}

import java.util.regex.Pattern


// import java.text.{DateFormat, SimpleDateFormat}

object TestCases
  class TestCases extends AnyFunSuite {
    test("Test to check if configuration file is present") {
      val config: Config = ConfigFactory.load("application.conf");
      assert(!config.isEmpty())
    }
    test("Test to check if pattern is present") {
      val config: String = ConfigFactory.load("application.conf").getString("parameters.pattern");
      assert(!config.isEmpty())
  }
    test("To check if the port is present"){
      val config: String = ConfigFactory.load("application.conf").getString("parameters.port");
      assert(!config.isEmpty)
    }
    test("Test to check for injected pattern") {
      val line: String = "22:55:03.653 [scala-execution-context-global-15] INFO  HelperUtils.Parameters$ - d2T9S-URX8h!Q^:2ILpL,[OrS&yjjbe0W7jH6lce0cg2B7J2_0/4_Qu6!m~6u}~^X<]]%~&X"
      val pattern1 = Pattern.compile("([a-c][e-g][0-3]|[A-Z][5-9][f-w]){5,15}")
      // Create another matcher variable to match the designated regex pattern
      val matcher1 = pattern1.matcher(line)
      assert(matcher1.find())
    }

    test("Test to check for injected pattern not matching") {
      val line: String = "22:55:01.825 [scala-execution-context-global-15] INFO  HelperUtils.Parameters$ - c0#hEbmBR0iETGX`^nbDLw\\uSDl>jaWpy`l1"
      val pattern1 = Pattern.compile("([a-c][e-g][0-3]|[A-Z][5-9][f-w]){5,15}")
      // Create another matcher variable to match the designated regex pattern
      val matcher1 = pattern1.matcher(line)
      assert(!matcher1.find())
    }

  }
