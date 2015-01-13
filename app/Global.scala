import models.SimpleClient
import models.displayTimes

import play.api.Application
import play.api.GlobalSettings

object Global extends GlobalSettings {

  private var cassandra: SimpleClient = _
  private var controller: controllers.Admin = _

  override def onStart(app: Application) {

    cassandra = new SimpleClient(app.configuration.getString("cassandra.node")
      .getOrElse(throw new IllegalArgumentException("No 'cassandra.node' config found.")))
    controller = new controllers.Admin(new displayTimes(cassandra))
  }

  override def getControllerInstance[A](clazz: Class[A]): A = {
    // as simple as possible, nothing else needed for now...
    if(clazz == classOf[controllers.Admin])
      controller.asInstanceOf[A]
    else
      throw new IllegalArgumentException(s"Controller of class $clazz not yet supported")
  }

  override def onStop(app: Application) {
    cassandra.close
  }

}
