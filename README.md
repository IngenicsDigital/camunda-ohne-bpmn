# Camunda without BPMN

This projects demonstrates how to create an example process using Camunda's "BPMN Model API" and deploy/run it within their workflow-engine.

## Project overview

The standard approach for creating a process for a bpmn-workflow-engine like camunda would be to model it graphically using a corresponding [tool](https://docs.camunda.io/docs/components/modeler/bpmn/). 
As an alternative one can use the [BPMN Model API](https://docs.camunda.org/manual/latest/user-guide/model-api/bpmn-model-api/), which enables you to define
the process programmatically in a fluent style. As an example a process "order pizza" is modeled that way and verified by unit tests.
The process was taken from [bpmn-examples](https://camunda.com/de/bpmn/bpmn-examples/) (Two Step Escalation / Solution 2).

The project is based on Spring Boot and H2 (In-Memory) and 
was initially set up using https://start.camunda.com/ . 

## Project execution

* install Maven
* configure [maven-toolchains-plugin](https://maven.apache.org/plugins/maven-toolchains-plugin/):
* their should be an entry in your `~/.m2/toolchains.xml` referencing JDK 11
```xml
<?xml version="1.0" encoding="UTF-8"?>
<toolchains>
  <toolchain>
    <type>jdk</type>
    <provides>
      <version>11</version>
    </provides>
    <configuration>
      <jdkHome>/devel/tools/jdk/jdk-11.0.2</jdkHome>
    </configuration>
  </toolchain>
 </toolchains> 
```
* mvn package
