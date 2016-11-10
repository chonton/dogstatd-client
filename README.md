# dogstatd-client
A java client to push UDP messages to a local [dogstatd](http://docs.datadoghq.com/guides/dogstatsd/).

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