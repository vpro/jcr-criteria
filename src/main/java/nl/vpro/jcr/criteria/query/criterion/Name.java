package nl.vpro.jcr.criteria.query.criterion;

import lombok.EqualsAndHashCode;

import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.sql2.*;

/**
 * @since 2.11
 */
@EqualsAndHashCode(callSuper = true)
public class Name extends BaseCriterion implements Criterion {
    private final String name;

    public Name(final String name) {
        this.name = name;
    }

     @Override
     public  Condition toSQLCondition(final Criteria criteria) {
        return new nl.vpro.jcr.criteria.query.sql2.Name(name);
    }
}
