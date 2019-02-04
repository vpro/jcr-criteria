package nl.vpro.jcr.criteria.query.criterion;

import lombok.EqualsAndHashCode;

import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.sql2.Condition;
import nl.vpro.jcr.criteria.query.sql2.IsChild;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@EqualsAndHashCode(callSuper = true)
public class IsChildOf extends BaseCriterion implements Criterion {


    private final String path;

    public IsChildOf(String path) {
        this.path = path;

    }

     @Override
     public  Condition toSQLCondition(Criteria criteria) {
         return new IsChild(path);
    }
}
