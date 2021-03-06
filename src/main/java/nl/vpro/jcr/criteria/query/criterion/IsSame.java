package nl.vpro.jcr.criteria.query.criterion;

import lombok.EqualsAndHashCode;

import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.sql2.Condition;
import nl.vpro.jcr.criteria.query.sql2.IsSameNode;

/**
 * @author Michiel Meeuwissen
 * @since 2.1
 */
@EqualsAndHashCode(callSuper = true)
public class IsSame extends BaseCriterion implements Criterion {


    private final String path;

    public IsSame(String path) {
        this.path = path;

    }


     @Override
     public  Condition toSQLCondition(Criteria criteria) {
        return new IsSameNode(path);
    }
}
