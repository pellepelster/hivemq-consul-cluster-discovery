## HiveMQ Consul Cluster Discovery Plugin

This plugin allows your HiveMQ cluster nodes to discover each other dynamically by exchanging their information via
Consul.

### How it works

On startup each broker registers a Consul service with hostname and port. Then each broker regularly checks the list
of registered services within Consul and tries to form a cluster. Additionaly each Consul service is equipped with a
TTL health check which is updated by each broker so stale entries can be detected.

### Building

To build the jar execute:

```sh
./gradle shadowJar
```

### Installation

1. Copy the jar file from the build dir `buid/libs/hivemq-consul-cluster-discovery-<version>-SNAPSHOT-all.jar` to your `[HIVEMQ_HOME]/plugins` folder
2. Copy the `examples/consuldiscovery.properties` file to your `[HIVEMQ_HOME]/conf` folder
3. Modify the `consuldiscovery.properties.properties file for your needs
4. Done

### Usage

1. Start more than one HiveMQ with clustering enabled
2. The HiveMQs will form a cluster.


### Configuration

The Consul discovery plugin uses its own configuration file 'consuldiscovery.properties' which must be placed in HiveMQ's config folder.

==== General Configuration

|===
| Config name | Required | Description

| config key | description | default |
|------------|--------------|--------|
| consul-hostname | consul hostname | consul |
| consul-port | consul port | 8500 |
| consul-ttl | Time to live for the services the plugin registers in Consul | 120 |
| consul-update-interval | The interval at which the plugin updates the TTL check for the Consul service. This value should obviously be smaller than `consul-ttl`  | 60 |

===== ACL token Configuration

The Consul token that will be used can be passed to the plugin by using the environment variable `CONSUL_TOKEN`.

### Development and Testing

A sample docker environment consisting of two HiveMQ workers and a Consul server in development mode is provided for
testing and development purposes. To build the needed Docker images run

```sh
./gradlew docker
```

afterwards you can start the envrionment by

```sh
docker-compose up
```

### Logging

HiveMQ is rather silent by default, to see some log output for the plugins, you have to overwrite the Logback config for
HiveMQ by placing the following logback.xml in the HiveMQ conf folder:

```xml
<configuration scan="true" scanPeriod="60 seconds">

<appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
        <pattern>%-30(%d %level)- %msg%n%ex</pattern>
    </encoder>
</appender>

<appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>${hivemq.home}/log/hivemq.log</file>
    <append>true</append>

    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <!-- daily rollover -->
        <fileNamePattern>${hivemq.home}/log/hivemq.%d{yyyy-MM-dd}.log</fileNamePattern>

        <!-- keep 30 days' worth of history -->
        <maxHistory>30</maxHistory>
    </rollingPolicy>
    <encoder>
        <pattern>%-30(%d %level)- %msg%n%ex</pattern>
    </encoder>
</appender>

<appender name="MIGRATIONS-FILE" class="ch.qos.logback.core.FileAppender">
    <file>${hivemq.home}/log/migration.log</file>
    <append>true</append>
    <encoder>
        <pattern>%-30(%d %level)- %msg%n%ex</pattern>
    </encoder>
</appender>

<logger name="migrations" level="DEBUG" additivity="false">
    <appender-ref ref="MIGRATIONS-FILE"/>
</logger>

<root>
    <appender-ref ref="FILE"/>
</root>

<root level="INFO">
    <appender-ref ref="FILE"/>
</root>

<root level="INFO">
    <appender-ref ref="CONSOLE"/>
</root>

<logger name="org.quartz" level="WARN">
    <appender-ref ref="FILE"/>
    <appender-ref ref="CONSOLE"/>
</logger>

<logger name="jetbrains.exodus" level="WARN"/>

<logger name="com.google.common.util.concurrent.Futures.CombinedFuture" level="OFF"/>

<logger name="io.pelle.hivemq.plugin.ConsulDiscoveryPluginModule" level="DEBUG"/>

</configuration>
```
