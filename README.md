# dogstatd-client
A java client to push UDP messages to a local [dogstatd](http://docs.datadoghq.com/guides/dogstatsd/).

[Javadoc](https://chonton.github.io/dogstatd-client/0.0.3/apidocs/) and
[build reports](https://chonton.github.io/dogstatd-client/0.0.3/project-reports) are available.

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
      <dependency>
        <groupId>org.honton.chas</groupId>
        <artifactId>dogstatd-client</artifactId>
        <version>0.0.3</version>
      </dependency>
```

## Typical Java Use

### In setup code
```java
  // you only need a single instance, Sender is thread safe and send method does not block caller
  static public final Sender METRICS = new Sender();
```

### Sending some example metrics
```java
  METRICS.send(new Histogram("histogram.name", latency);
  
  METRICS.send(new Gauge("round", i, tag));
  
  METRICS.send(new Counter("pi", 3.14));
  
  METRICS.send(new Event("title", "message", "tag1", "tag2"));
```

## 0.2 to 0.3
- Added public constructors for Event
- Added public constructors for ServiceCheck
- Metric changed to abstract
- Tags changed to package protected
- Validator changed to package protected
