package general

import akka.stream.ActorMaterializer
import controllers.WebSocketController
import router.Routes
import play.api.ApplicationLoader.Context
import play.api.routing.Router
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext}
import services.Service

class MyAppLoader extends ApplicationLoader {
  override def load(context: Context): Application = new MyAppComponents(context).application
}

class MyAppComponents(val context: Context) extends BuiltInComponentsFromContext(context) {
  implicit lazy val system = actorSystem
  val serviceA = actorSystem.actorOf(Service.props(1200))
  val wsController = new WebSocketController()(serviceA, system, materializer)

  override def router: Router = new Routes(httpErrorHandler, wsController)
}
