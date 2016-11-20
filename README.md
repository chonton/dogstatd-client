# dogstatd-client
A java client to push UDP messages to a local [dogstatd](http://docs.datadoghq.com/guides/dogstatsd/).

### Requirements
* Minimal latency in the mainline processing
* Some, but not extreme buffering of outgoing messages
* Non-blocking write of UDP message
* Thread-safe sender
* Lack of dogstatd collector will be noted, but not cause failure of mainline processing

### Assumptions
* A local (on the same host) dogstatd collector
* Firing a UDP message per application event will be cheaper than in-process aggregation of events

## Use with Maven
To include dogstatd-client in your maven build, use the following fragment in your pom.
```xml
  <build>
    <plugins>
      <plugin>
        <groupId>org.honton.chas</groupId>
        <artifactId>dogstatd-client</artifactId>
        <version>0.0.1-SNAPSHOT</version>
      </plugin>
    </plugins>
  </build>
```

## Typical Java Use

### In setup code
```java

  static public final Sender METRICS = new Sender();

```

### In mainline code
```java
        METRICS.send(new Gauge("round", i, tag));
```