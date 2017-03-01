![travisci](https://travis-ci.org/askeron/fluentxmlwrapper.svg?branch=master)
# fluentxmlwrapper

A java library with a simple wrapper for XML Processing with a fluent interface.
In contrast to other similar project the interface follows the principle of clean code and can be used for reading and writing XML documents.

### Maven dependency

```xml
<dependencies>
    ...

    <dependency>
        <groupId>de.drbunsen.common</groupId>
        <artifactId>fluentxmlwrapper</artifactId>
        <version>0.1.0</version>
    </dependency>

    ...
</dependencies>
```

Add the following repository to use snapshots.

```xml
<repository>
    <id>sonatype-snapshots</id>
    <name>Sonatype Snapshots</name>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
</repository>
```