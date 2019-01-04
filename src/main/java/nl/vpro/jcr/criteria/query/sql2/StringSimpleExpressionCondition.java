package nl.vpro.jcr.criteria.query.sql2;

import nl.vpro.jcr.criteria.query.criterion.SimpleExpression;

/**
 * @author Michiel Meeuwissen
 * @since 1.3
 */
public class StringSimpleExpressionCondition  extends SimpleExpressionCondition<String> {

    public StringSimpleExpressionCondition(Field field, SimpleExpression.Op op, String value) {
        super(field, op, value);
    }

    @Override
    String getValue() {
        return "'" + value + "'";
    }
}
