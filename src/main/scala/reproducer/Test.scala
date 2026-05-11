package reproducer

import javax.sql.DataSource
import java.util.UUID
import org.slf4j.Logger
import scala.util.Random
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.s
import scala.concurrent.duration.Duration

object Test {
  private val cleanTableStatement =
    "TRUNCATE TABLE reproducer"
  private val insertStatement =
    "INSERT INTO reproducer VALUES (?, ?)"
  private val selectStatement =
    "SELECT details FROM reproducer WHERE id = ? LIMIT 1"

  private def singleIteration(dataSource: DataSource): Unit =
    val uuid = UUID.randomUUID()
    val details = Random.nextString(length = 135)

    val connection = dataSource.getConnection()
    connection.setAutoCommit(true)

    connection.createStatement().execute(cleanTableStatement)

    val preparedInsert = connection.prepareStatement(insertStatement)
    preparedInsert.setObject(1, uuid)
    preparedInsert.setString(2, details)
    val rowsInserted = preparedInsert.executeUpdate()
    assert(
      rowsInserted == 1,
      "INSERT FAILED"
    )

    val preparedSelect = connection.prepareStatement(selectStatement)
    preparedSelect.setObject(1, uuid)
    val selectResult = preparedSelect.executeQuery()
    assert(
      selectResult.next(),
      "SELECT RETURNED NO ROWS"
    )
    assert(
      selectResult.getString("details") == details,
      "SELECT RETURNED DIFFERENT DATA"
    )

    connection.close()
  end singleIteration

  def run(logger: Logger, dataSource: DataSource): Unit =
    val startTime = System.currentTimeMillis
    for (_ <- 0 to 100) {
      singleIteration(dataSource)
    }
    val endTime = System.currentTimeMillis
    val testDuration = Duration(endTime - startTime, "millis")

    logger.info(s"TEST DURATION: ${testDuration.toSeconds}s")
  end run
}
