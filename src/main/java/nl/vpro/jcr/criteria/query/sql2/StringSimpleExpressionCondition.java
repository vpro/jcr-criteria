package nl.vpro.jcr.criteria.query.sql2;

import nl.vpro.jcr.criteria.query.criterion.Op;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class StringSimpleExpressionCondition  extends SimpleExpressionCondition<String> {

    public StringSimpleExpressionCondition(Field field, Op op, String value) {
        super(field, op, value);
    }

    @Override
    String getValue() {
        return "'" + value + "'";
    }
}
