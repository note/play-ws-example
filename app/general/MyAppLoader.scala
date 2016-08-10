package general

import controllers.WebSocketController
import router.Routes
import play.api.ApplicationLoader.Context
import play.api.routing.Router
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext}

class MyAppLoader extends ApplicationLoader {
  override def load(context: Context): Application = new MyAppComponents(context).application
}

class MyAppComponents(val context: Context) extends BuiltInComponentsFromContext(context) {
  implicit lazy val system = actorSystem
  val wsController = new WebSocketController(system)

  override def router: Router = new Routes(httpErrorHandler, wsController)
}
