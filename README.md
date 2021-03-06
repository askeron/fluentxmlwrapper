![CI](https://github.com/askeron/fluentxmlwrapper/workflows/CI/badge.svg)
[![License](https://img.shields.io/github/license/askeron/fluentxmlwrapper.svg?style=flat)](https://github.com/askeron/fluentxmlwrapper/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/de.drbunsen.common/fluentxmlwrapper.svg?style=flat)](https://mvnrepository.com/artifact/de.drbunsen.common/fluentxmlwrapper)
[![Required Java 8.0](https://img.shields.io/badge/Required-Java%208.0-blue.svg)]()
# fluentxmlwrapper

A java library with a simple wrapper for XML Processing with a fluent interface.
In contrast to other similar project the interface follows the principle of clean code and can be used for reading and writing XML documents.

## Maven dependency

```xml
<dependency>
    <groupId>de.drbunsen.common</groupId>
    <artifactId>fluentxmlwrapper</artifactId>
    <version>0.5.0</version>
</dependency>
```

### Snapshots

Add the following repository to use snapshots.

#### Maven

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

#### Gradle (Groovy)

```
repositories {
    mavenCentral()
    maven {
        url 'https://oss.sonatype.org/content/repositories/snapshots/'
    }
}
```

#### Gradle (Kotlin)

```
repositories {
    mavenCentral()
    maven {
        setUrl("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}
```

## Example

```java
final String newXml = FluentXmlWrapper.of(new File("text.xml"))
        .getElement("devices")
        .addElement("device")
        .setAttribute("id", "41234")
        .addElement("ipaddress").setText("192.168.0.123").getParentElement()
        .addElement("ipaddress").setText("192.168.0.201").getParentElement()
        .toXmlWithDefaultUtf8Header(false);
```

