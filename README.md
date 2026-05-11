# reproducer-localstack-aurora-postgres-ssl-slowdown

Reproducer of a major slowdown, introduced in version `2026.04.0`, when executing queries against an **Aurora Postgres** cluster with `SSL` enabled running in **Localstack**.

Run `sbt run` to reproduce the test slowdown.

-----

Check the `regression` branch to confirm that the slowdown was introduce in version `2026.04.0`.

Check the `workaround` branch to confirm that the slowdown is related to `SSL`.
