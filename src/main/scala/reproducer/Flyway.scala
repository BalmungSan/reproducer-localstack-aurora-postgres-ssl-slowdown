package reproducer

import javax.sql.DataSource
import org.flywaydb.core.api.output.MigrateResult

object Flyway {
  def migrate(dataSource: DataSource): MigrateResult =
    org.flywaydb.core.Flyway.configure
      .dataSource(dataSource)
      .createSchemas(true)
      .executeInTransaction(true)
      .cleanDisabled(false)
      .load
      .migrate()
  end migrate
}
