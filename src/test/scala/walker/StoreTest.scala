package walker

import com.typesafe.config.ConfigFactory

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

final class StoreTest extends AnyFunSuite with Matchers:
  val context = Context( ConfigFactory.load("test.conf") )
  val store = Store(context)

  var walker = Walker(name = "fred")
  var session = Session(walkerId = 0)

  test("store"):
    addWalker()
    updateWalker()
    listWalkers()

    addSession()
    updateSession()
    listSessions()

  def addWalker(): Unit =
    val walkerId = store.addWalker(walker)
    walkerId shouldBe 1
    walker = walker.copy(id = walkerId)
    session = session.copy(walkerId = walkerId)

  def updateWalker(): Unit =
    walker = walker.copy(name = "fred flintstone")
    store.updateWalker(walker)

  def listWalkers(): Unit =
    store.listWalkers().length shouldBe 1

  def addSession(): Unit =
    store.addSession(session)

  def updateSession(): Unit =
    session = session.copy(distance = 4.0, hours = 1, minutes = 20, calories = 300)
    store.updateSession(session)

  def listSessions(): Unit =
    store.listSessions(walker.id).length shouldBe 1