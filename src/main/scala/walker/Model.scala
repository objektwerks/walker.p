package walker

import com.typesafe.scalalogging.LazyLogging

import ox.supervised

import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.beans.property.ObjectProperty

final class Model(store: Store) extends LazyLogging:
  def assertInFxThread(message: String, suffix: String = " should be in fx thread!"): Unit =
    require(Platform.isFxApplicationThread, message + suffix)
  def assertNotInFxThread(message: String, suffix: String = " should not be in fx thread!"): Unit =
    require(!Platform.isFxApplicationThread, message + suffix)

  val registered = ObjectProperty[Boolean](true)
  val loggedin = ObjectProperty[Boolean](true)

  val selectedWalkerId = ObjectProperty[Long](0)
  val selectedSessionId = ObjectProperty[Long](0)

  selectedWalkerId.onChange { (_, _, newWalkerId) =>
    sessions(newWalkerId)
  }

  val observableWalkers = ObservableBuffer[Walker]()
  val observableSessions = ObservableBuffer[Session]()

  def walkers(): Unit =
    supervised:
      assertNotInFxThread("list walkers")
      observableWalkers.clear
      observableWalkers ++= store.listWalkers()

  def add(walker: Walker)(runLast: => Unit): Unit =
    supervised:
      assertNotInFxThread(s"add walker: $walker")
      val id = store.addWalker(walker)
      observableWalkers.insert(0, walker.copy(id = id))
      selectedWalkerId.value = id
      logger.info(s"Added walker: $walker")

  def update(previousWalker: Walker, updatedWalker: Walker): Unit =
    supervised:
      assertNotInFxThread(s"update walker from: $previousWalker to: $updatedWalker")
      store.updateWalker(updatedWalker)
      val index = observableWalkers.indexOf(previousWalker)
      if index > -1 then observableWalkers.update(index, updatedWalker)      
      logger.info(s"Updated walker from: $previousWalker to: $updatedWalker")

  def sessions(walkerId: Long): Unit =
    supervised:
      assertNotInFxThread("list sessions")
      observableSessions.clear
      observableSessions ++= store.listSessions(walkerId)

  def add(session: Session)(runLast: => Unit): Unit =
    supervised:
      assertNotInFxThread(s"add session: $session")
      fetcher.fetch(
        SaveSession(objectAccount.get.license, session),
        (event: Event) => event match
          case fault @ Fault(_, _) => onFetchFault("add session", session, fault)
          case SessionSaved(id) =>
            observableSessions.insert(0, session.copy(id = id))
            observableSessions.sort()
            selectedSessionId.set(id)
            logger.info(s"Added session: $session")
            runLast
          case _ => ()
      )

  def update(selectedIndex: Int, session: Session)(runLast: => Unit): Unit =
    supervised:
      assertNotInFxThread(s"update session from: $selectedIndex to: $session")
      fetcher.fetch(
        SaveSession(objectAccount.get.license, session),
        (event: Event) => event match
          case fault @ Fault(_, _) => onFetchFault("update session", session, fault)
          case SessionSaved(id) =>
            if selectedIndex > -1 then
              observableSessions.update(selectedIndex, session)      
              logger.info(s"Updated session from: $selectedIndex to: $session")
              runLast
            else
              logger.error(s"Update of session from: $selectedIndex to: $session failed due to invalid index: $selectedIndex")
          case _ => ()
      )