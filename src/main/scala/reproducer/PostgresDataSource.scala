package reproducer

import java.util.Properties
import javax.sql.DataSource
import org.postgresql.ds.PGSimpleDataSource
import software.amazon.jdbc.ds.AwsWrapperDataSource
import org.postgresql.ssl.LibPQFactory

object PostgresDataSource {
  def make(config: PostgresConfig): DataSource =
    // Use the AWS advanced-jdbc-wrapper DataSource.
    val dataSource = new AwsWrapperDataSource()
    val targetDataSourceProps = new Properties()
    dataSource.setTargetDataSourceProperties(targetDataSourceProps)
    targetDataSourceProps.setProperty(
      "wrapperPlugins",
      "initialConnection,auroraConnectionTracker,failover2,efm2"
    )

    // Specify PostgreSQL as the underlying driver.
    dataSource.setTargetDataSourceClassName(
      classOf[PGSimpleDataSource].getCanonicalName
    )
    dataSource.setJdbcProtocol("jdbc:postgresql:")
    targetDataSourceProps.setProperty("wrapperDialect", "rds-pg")

    // Configure connection details.
    dataSource.setServerName(config.host)
    dataSource.setServerPort(config.port.toString)
    dataSource.setDatabase(config.dbName)
    dataSource.setUser(config.username)
    dataSource.setPassword(config.password)

    // Configure SSL.
    targetDataSourceProps.setProperty("ssl", "true")
    targetDataSourceProps.setProperty("sslmode", "require")
    targetDataSourceProps.setProperty(
      "sslfactory",
      classOf[LibPQFactory].getCanonicalName
    )
    targetDataSourceProps.setProperty("channelBinding", "prefer")

    // Return the configured DataSource.
    dataSource
  end make
}
