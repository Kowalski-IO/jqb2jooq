# jqb2jooq
jQuery QueryBuilder meets JOOQ without the work.

[![Build Status](https://travis-ci.org/Kowalski-IO/jqb2jooq.svg?branch=master)](https://travis-ci.org/Kowalski-IO/jqb2jooq)
[![Coverage Status](https://coveralls.io/repos/github/Kowalski-IO/jqb2jooq/badge.svg?branch=master)](https://coveralls.io/github/Kowalski-IO/jqb2jooq?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.kowalski/jqb2jooq/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.kowalski/jqb2jooq)


What is [jQuery QueryBuilder](http://querybuilder.js.org)?

jqb is a super handy library for your website that allow for the creation of queries and filters.


What is [JOOQ](https://www.jooq.org)?

To shamelessly steal their tagline "JOOQ: The easiest way to write SQL in Java". This sentiment I agree with completely. 
The JOOQ library provides a typesafe way to execute queries and is an absolute joy to use.


This project aims to be the glue between these two awesome products. jqb2jooq provides the means of defining a mapping from
jQuery QueryBuilder filter fields to JOOQ auto-generated fields. Once you define this mapping jqb2jooq will handle the conversion of QueryBuilder json to a JOOQ condition, ready to be included in your JOOQ query.


## Usage

Getting started is just as easy as the famed 5 Minute Wordpress Install (only you don't have to use Wordpress :smirk:).

**First**, add the following dependency to your pom.xml.

```xml
<dependency>
  <groupId>io.kowalski</groupId>
  <artifactId>jqb2jooq</artifactId>
  <version>1.0.0</version>
</dependency>
```
***

**Second**, you will need to define a mapping between jqb filter fields and JOOQ fields.

This is done by creating an enum that implements [RuleTarget.java](src/main/java/io/kowalski/jqb2jooq/RuleTarget.java).

```java
package io.kowalski.jqb2jooq.test;

import io.kowalski.jqb2jooq.RuleTarget;
import org.jooq.Condition;
import org.jooq.Field;

import static io.kowalski.jqb2jooq.test.jooq.Tables.*;

public enum TestFilterTargets implements RuleTarget {

  FULLNAME(EMPLOYEES.FULLNAME),
  DOB(EMPLOYEES.DOB),
  SALARY(PAYROLL.SALARY);

  private final Field field;
  private final Condition[] implicitConditions;

  TestFilterTargets(Field field, Condition... implicitConditions) {
      this.field = field;
      this.implicitConditions = implicitConditions;
  }

  @Override
  public TestFilterTargets parse(String value) {
      return TestFilterTargets.valueOf(value);
  }

  @Override
  public Field getField() {
      return field;
  }

  @Override
  public Condition[] getImplicitConditions() {
      return implicitConditions;
  }

}
```

***

**Last, but not least**, you need to convert the json filter from jqb to a JOOQ condition.

jqb2jooq assumes that the json filter has been deserialized into: 
```java
Map<String, Object>
```

Here is a quick snippet of how to perform said task with [gson](https://github.com/google/gson).
```java
java.lang.reflect.Type mapType = new com.google.gson.reflect.TypeToken<Map<String, Object>>() 
{}.getType();

Map<String, Object> filterMap = new Gson().fromJson(rawJson, mapType);
```
Once you have the filter deserialized all you have to do is...

```java
org.jooq.Condition condition = JQB2JOOQ.parse(TestFilterTargets.class, filterMap);

// ...and immediately put it to work

try (DSLContext dsl = DSL.using(dataSource, SQLDialect.H2)) {
  List<Employees> employees = dsl.select().from(EMPLOYEES)
          .where(condition).fetchInto(Employees.class);
}

```

***

# And that's all she wrote

I hope you enjoy using jqb2jooq! If you notice anything funky please file an issue.

:heart:
