import org.junit.runner.RunWith

import org.specs2.mutable.Specification

import org.specs2.runner.JUnitRunner

import play.api.test._


@RunWith(classOf[JUnitRunner])
class MarketingSpec extends Specification { "should display certain elements on the marketing pages" in new WithBrowser {
   //about page
     browser.goTo("http://localhost:" + port + "/about")
      browser.$("h1").first.getText must equalTo("About us")
  }

}