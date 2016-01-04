[![Build Status](https://travis-ci.org/vpro/jcr-criteria.svg?)](https://travis-ci.org/vpro/jcr-criteria)

# JCR-criteria

This is a way to create JCR-queries, using an interface which was inspired by the criteria api.

For example
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
 LOG.debug("JCR query : " + criteria.toXpathExpression());
 AdvancedResultItem item = ar.getFirstResult();
```



## openutils-mgnlcriteria


This was branched from  

http://www.openmindlab.com/lab/products/mgnlcriteria.html

The Magnolia dependency is completely removed and this is now a generic jrc-criteria implementation.


## Installation
Download  the most recent jar from: https://oss.sonatype.org/content/repositories/snapshots/nl/vpro/jcr-criteria/

Or you can add this to your pom.xml
```xml
<dependency>
    <groupId>nl.vpro</groupId>
    <artifactId>jcr-criteria</artifactId>
    <version>1.0</version>
</dependency>
```
