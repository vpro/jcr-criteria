package nl.vpro.jcr.criteria.query.sql2;

import nl.vpro.jcr.criteria.query.criterion.Op;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class BooleanSimpleExpressionCondition extends SimpleExpressionCondition<Boolean> {

    public BooleanSimpleExpressionCondition(Field field, Op op, Boolean value) {
        super(field, op, value);
    }

    @Override
    String getValue() {
        return value.toString();
    }
}
