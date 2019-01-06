package nl.vpro.jcr.criteria.query.sql2;

import nl.vpro.jcr.criteria.query.criterion.SimpleExpression;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
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
    public boolean toSql2(StringBuilder builder) {
        field.toSql2(builder);
        builder.append(" ").append(op.getXpath()).append(" ").append(getValue());
        return true;
    }

    public static SimpleExpressionCondition<?> of(Field field, SimpleExpression.Op op, Object v) {
        if (v instanceof String) {
            return new StringSimpleExpressionCondition(field, op, (String) v);
        } else if (v instanceof Boolean){
            return new BooleanSimpleExpressionCondition(field, op, (Boolean) v);
        } else {
            throw new IllegalArgumentException("Unrecognized " + v.getClass() + " " + v);
        }
    }
}
