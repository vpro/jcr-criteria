package nl.vpro.jcr.criteria.query.criterion;

import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.JCRQueryException;
import nl.vpro.jcr.criteria.query.sql2.Condition;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class IsChildOf extends BaseCriterion implements Criterion {


    private final String path;

    public IsChildOf(String path) {
        this.path = path;

    }

    @Override
    public String toXPathString(Criteria criteria) throws JCRQueryException {
        return null;

    }

     @Override
     public  Condition toSQLCondition(Criteria criteria) {
        throw new UnsupportedOperationException("" + getClass().getName() + " does net yet support SQL2");
    }
}
