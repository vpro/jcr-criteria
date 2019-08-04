[![Maven Central](https://img.shields.io/maven-central/v/nl.vpro/jcr-criteria.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22nl.vpro%22%20AND%20a:%22jcr-criteria%22)
[![javadoc](http://www.javadoc.io/badge/nl.vpro/jcr-criteria.svg?color=blue)](http://www.javadoc.io/doc/nl.vpro/jcr-criteria)
[![Build Status](https://travis-ci.org/vpro/jcr-criteria.svg?)](https://travis-ci.org/vpro/jcr-criteria)
[![snapshots](https://img.shields.io/nexus/s/https/oss.sonatype.org/nl.vpro/jcr-criteria.svg)](https://oss.sonatype.org/content/repositories/staging/nl/vpro/jcr-criteria/)
[![codecov](https://codecov.io/gh/vpro/jcr-criteria/branch/master/graph/badge.svg)](https://codecov.io/gh/vpro/jcr-criteria)

# jcr-criteria

This is a way to create and execute JCR queries using Java code, using an interface which was inspired by the Criteria API as used by Hibernate/JPA. This code is based on the [Criteria API for Magnolia CMS](http://www.openmindlab.com/lab/products/mgnlcriteria.html) (openutils-mgnlcriteria) module which was developed by Openmind.

In contrast to `openutils-mgnlcriteria` there is no dependency on any Magnolia CMS code in `jcr-criteria`, so this is a generic JCR Criteria API implementation. It can still be used with Magnolia CMS, but should work with other JCR-based projects as well.

## Usage

You can download the most recent jar from https://oss.sonatype.org/content/repositories/snapshots/nl/vpro/jcr-criteria/ or you can add this to your `pom.xml`:

```xml
<dependency>
    <groupId>nl.vpro</groupId>
    <artifactId>jcr-criteria</artifactId>
    <version>2.1</version>
</dependency>
```

Example:

```java
import nl.vpro.jcr.criteria.query.AdvancedResult;
import nl.vpro.jcr.criteria.query.AdvancedResultItem;
import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.JCRCriteriaFactory;
import nl.vpro.jcr.criteria.query.criterion.Criterion;
import nl.vpro.jcr.criteria.query.criterion.Restrictions;

..


Criteria criteria = JCRCriteriaFactory.createCriteria()
            .setBasePath(basePath)
            .setPaging(1, 1)
            .addOrder(Order.ascending(field))
            .add(Restrictions.eq(Criterion.JCR_PRIMARYTYPE, NodeTypes.Page.NAME))
            .add(Restrictions.in("@" + NodeTypes.Renderable.TEMPLATE, templates))
            .add(Restrictions.gt(field, begin));

 AdvancedResult ar = criteria.execute(MgnlContext.getJCRSession(RepositoryConstants.WEBSITE));
 log.debug("JCR query : " + criteria.toXpathExpression());
 AdvancedResultItem item = ar.getFirstResult();
```
It can also be done, if you prefer, using the builder pattern
```java

static import nl.vpro.jcr.criteria.query.criterion.Restrictions.*;

ExecutableQuery criteria = JCRCriteriaFactory.builder()
            .basePath(basePath)
            .order(Order.ascending(field))
            .add(eq(Criterion.JCR_PRIMARYTYPE, NodeTypes.Page.NAME))
            .add(in(attr(NodeTypes.Renderable.TEMPLATE), templates))
            .add(gt(field, begin))
            .build()
            ;
AdvancedResult result = criteria.execute(session);
```

## Java 11 and higher
This project is shipped (since 2.2) with a `module-info.java` which is though the only file compiled with java 11. The rest is compiled with java 8, so it is java 8 compatible, but it does participate in jigsaw if you happen to use java 11 already.
