# Payara OpenTracing JDBC tracing

This is a payara sql trace listener that will create opentracing spans based on jdbc calls and can be used in combination with the standard microprofile opentracing implementation as described [here](https://blog.payara.fish/new-opentracing-features-in-payara-platform-5.194).

To enable the tracing, compile the jar and add it to the classpath of payara. After that, configure the tracer in the fish.payara.sql-trace-listeners as described in the [payara documentation](https://docs.payara.fish/community/docs/5.201/documentation/payara-server/advanced-jdbc/sql-trace-listeners.html).

```
@DataSourceDefinition(
    name = "java:app/MyApp/MyDS",
    className = "org.h2.jdbcx.JdbcDataSource",
    url = "jdbc:h2:mem:test",
    properties = {"fish.payara.sql-trace-listeners=com.joyrex2001.payara.opentracing.jdbc.OpenTracingListener"})
```
