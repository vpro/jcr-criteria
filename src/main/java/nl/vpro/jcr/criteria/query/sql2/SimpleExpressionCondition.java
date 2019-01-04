package nl.vpro.jcr.criteria.query.sql2;

import nl.vpro.jcr.criteria.query.criterion.SimpleExpression;

/**
 * @author Michiel Meeuwissen
 * @since 1.3
 */
public abstract class SimpleExpressionCondition<T> implements  Condition {
    final Field field;
    final SimpleExpression.Op op;
    final T value;

    protected SimpleExpressionCondition(Field field, SimpleExpression.Op op, T value) {
        this.field = field;
        this.op = op;
        this.value = value;
    }

    abstract String getValue();

    @Override
    public String toSql2() {
        return field.toSql2() + " " + op.getXpath() + " " + getValue();
    }

    public static SimpleExpressionCondition<?> of(Field field, SimpleExpression.Op op, Object v) {
        if (v instanceof String) {
            return new StringSimpleExpressionCondition(field, op, (String) v);
        } else {
            throw new IllegalArgumentException("Unrecognized " + v);
        }
    }
}
