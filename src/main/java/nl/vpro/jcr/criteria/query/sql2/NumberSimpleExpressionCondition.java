package nl.vpro.jcr.criteria.query.sql2;

import nl.vpro.jcr.criteria.query.criterion.Op;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class NumberSimpleExpressionCondition extends SimpleExpressionCondition<Number> {

    public NumberSimpleExpressionCondition(Field field, Op op, Number value) {
        super(field, op, value);
    }

    @Override
    String getValue() {
        return value.toString();
    }
}
