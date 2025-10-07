package walker

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import java.awt.{Taskbar, Toolkit}
import java.awt.Taskbar.Feature

import scalafx.application.JFXApp3

object App extends JFXApp3 with LazyLogging:
  logger.info("Walker starting ...")

  val conf = ConfigFactory.load("app.conf")
  val context = Context(conf)
  val store = Store(context)
  val model = Model(store)

  override def start(): Unit =
    val view = View(context, model)
    stage = new JFXApp3.PrimaryStage:
      scene = view.scene
      title = context.windowTitle
      minWidth = context.windowWidth
      minHeight = context.windowHeight
      icons += context.appIcon

    if Taskbar.isTaskbarSupported() then
      val taskbar = Taskbar.getTaskbar()
      if taskbar.isSupported(Feature.ICON_IMAGE) then
        val appIcon = Toolkit.getDefaultToolkit.getImage(this.getClass().getResource("/image/icon.png"))
        taskbar.setIconImage(appIcon)
    
    model.walkers()

    stage.show()
    logger.info("Walker started at url: {}", context.url)

  override def stopApp(): Unit = logger.info("Walker stopped.")