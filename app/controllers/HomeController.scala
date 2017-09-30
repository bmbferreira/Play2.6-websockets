package controllers
import javax.inject._

import scala.concurrent.ExecutionContext
import play.api.libs.ws.WSClient
import play.api.mvc._
import play.api.mvc._
import akka.stream.scaladsl._

@Singleton
class HomeController @Inject()(cc: ControllerComponents, ws: WSClient)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def index = Action {
    Ok(views.html.index.render())
  }

  def wsWeatherIntervals = WebSocket.accept[String, String] { _ =>
    val url = "https://www.metaweather.com/api/location/742676/"
    def f = ws.url(url).get().map(r => s"${new java.util.Date()}\n ${r.body}")

    val source = Source.unfoldAsync(f)(last => {
      Thread.sleep(5000)
      f.map(next => Some((last, next)))
    })
    Flow.fromSinkAndSource(Sink.ignore, source)
  }
}
