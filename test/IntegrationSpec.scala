import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._
import play.api.i18n._

/**
 * add your integration spec here.
 * An integration test will fire up a whole play application in a real (or headless) browser
 */
@RunWith(classOf[JUnitRunner])
class IntegrationSpec extends Specification {

  "Application" should {

    "work from within a browser" in new WithBrowser {

      browser.goTo("http://localhost:" + port)
      browser.pageSource must contain(Messages("global.appName"))
    }

    "allow you to register your first name, last name and email and take you to registrationSuccess page" in new WithBrowser {
      browser.goTo("http://localhost:" + port)

      val firstName: String = "Jane"
      val lastName: String = "Smith"
      val email: String = "jane@hawkinsunlimited.com"

      browser.$("#firstName").text(firstName)
      browser.$("#lastName").text(lastName)
      browser.$("#email").text(email)
      browser.$(".btn-primary").click()

      browser.pageSource must contain("Hello " + firstName + ",")
      browser.pageSource must contain("Thank you for signing up to hear more")
      browser.pageSource must contain("Once we launch we will email you at " + email)
    }

    "fail to register you without your first name and prompt you with the missing information" in new WithBrowser {
      browser.goTo("http://localhost:" + port)

      val lastName: String = "Smith"
      val email: String = "jane@hawkinsunlimited.com"

      browser.$("#lastName").text(lastName)
      browser.$("#email").text(email)
      browser.$(".btn-primary").click()

      browser.pageSource must contain("This field is required")
    }

  }
}