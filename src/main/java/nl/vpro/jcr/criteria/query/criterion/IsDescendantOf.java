package nl.vpro.jcr.criteria.query.criterion;

import lombok.EqualsAndHashCode;

import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.sql2.Condition;
import nl.vpro.jcr.criteria.query.sql2.IsDescendant;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 * TODO: untested
 */
@EqualsAndHashCode(callSuper = true)
public class IsDescendantOf extends BaseCriterion implements Criterion {


    private final String path;

    public IsDescendantOf(String path) {
        this.path = path;

    }


     @Override
     public  Condition toSQLCondition(Criteria criteria) {
        return new IsDescendant(path);
    }
}
