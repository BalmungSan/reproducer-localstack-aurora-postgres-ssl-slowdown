package reproducer

import org.slf4j.LoggerFactory

object Main {
  def main(args: Array[String]): Unit =
    val logger = LoggerFactory.getLogger("reproducer")

    val localstackConfig = LocalstackContainer.start()
    logger.info(s"LOCALSTACK CONFIG: ${localstackConfig}")

    val postgresConfig = RDSCluster.make(logger, localstackConfig)
    logger.info(s"POSTGRES CONFIG: ${postgresConfig}")

    val dataSource = PostgresDataSource.make(postgresConfig)
    logger.info(s"POSTGRES DATASOURCE: ${dataSource}")

    val migrationResult = Flyway.migrate(dataSource)
    logger.info(s"FLYWAY MIGRATION RESULT: ${migrationResult.success}")

    Test.run(logger, dataSource)
  end main
}
