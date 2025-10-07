package walker

import com.zaxxer.hikari.HikariDataSource

import javax.sql.DataSource

import scalikejdbc.*

final class Store(context: Context):
  private val dataSource: DataSource =
    val ds = new HikariDataSource()
    ds.setDataSourceClassName(context.dataSourceClassName)
    ds.addDataSourceProperty("url", context.url)
    ds.addDataSourceProperty("user", context.user)
    ds.addDataSourceProperty("password", context.password)
    ds.setMaximumPoolSize(context.maximumPoolSize)
    ds

  ConnectionPool.singleton(DataSourceConnectionPool(dataSource))

  def listWalkers(accountId: Long): List[Walker] =
    DB readOnly { implicit session =>
      sql"select * from walker where account_id = $accountId order by name"
        .map(rs =>
          Walker(
            rs.long("id"),
            rs.string("name"), 
          )
        )
        .list()
    }

  def addWalker(walker: Walker): Long =
    DB localTx { implicit session =>
      sql"""
        insert into walker(name) values(${walker.name})
        """
        .updateAndReturnGeneratedKey()
    }

  def updateWalker(walker: Walker): Long =
    DB localTx { implicit session =>
      sql"""
        update walker set name = ${walker.name}
        where id = ${walker.id}
        """
        .update()
      walker.id
    }

  def listSessions(walkerId: Long): List[Session] =
    DB readOnly { implicit session =>
      sql"select * from session where walker_id = $walkerId order by datetime desc"
        .map(rs =>
          Session(
            rs.long("id"),
            rs.long("walker_id"),
            rs.int("weight"),
            rs.string("weight_unit"),
            rs.double("distance"),
            rs.string("distance_unit"),
            rs.int("hours"),
            rs.int("minutes"),
            rs.int("calories"),
            rs.long("datetime")
          )
        )
        .list()
    }

  def addSession(sess: Session): Long =
    DB localTx { implicit session =>
      sql"""
        insert into session(walker_id, weight, weight_unit, distance, distance_unit, hours, minutes, calories, datetime)
        values(${sess.walkerId}, ${sess.weight}, ${sess.weightUnit}, ${sess.distance}, ${sess.distanceUnit}, ${sess.hours},
        ${sess.minutes}, ${sess.calories}, ${sess.datetime})
        """
        .updateAndReturnGeneratedKey()
    }

  def updateSession(sess: Session): Long =
    DB localTx { implicit session =>
      sql"""
        update session set weight = ${sess.weight}, weight_unit = ${sess.weightUnit}, distance = ${sess.distance},
        distance_unit = ${sess.distanceUnit}, hours = ${sess.hours}, minutes = ${sess.minutes}, calories = ${sess.calories},
        datetime = ${sess.datetime} where id = ${sess.id}
        """
        .update()
      sess.id
    }