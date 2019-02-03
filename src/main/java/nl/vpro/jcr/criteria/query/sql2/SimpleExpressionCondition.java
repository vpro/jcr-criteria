package nl.vpro.jcr.criteria.query.sql2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import nl.vpro.jcr.criteria.query.criterion.Op;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public abstract class SimpleExpressionCondition<T> implements  Condition {


    final Field field;
    final Op op;
    final T value;


    protected SimpleExpressionCondition(Field field, Op op, T value) {
        this.field = field;
        this.op = op;
        this.value = value;
    }



    abstract String getValue();

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public boolean toSql2(StringBuilder builder) {
        field.toSql2(builder);
        builder.append(" ").append(op.getXpath()).append(" ").append(getValue());
        return true;
    }

    public static SimpleExpressionCondition<?> of(Field field, Op op, Object v) {
        if (v instanceof String) {
            return new StringSimpleExpressionCondition(field, op, (String) v);
        } else if (v instanceof Boolean) {
            return new BooleanSimpleExpressionCondition(field, op, (Boolean) v);
        } else if (v instanceof Number) {
            return new NumberSimpleExpressionCondition(field, op, (Number) v);
        } else if (v instanceof Calendar) {
            return new StringSimpleExpressionCondition(field, op, v.toString());
        } else if (v instanceof LocalDate) {
            return new CastFromStringSimpleExpressionCondition("date", field, op, ((LocalDate) v).atStartOfDay().atOffset(ZoneOffset.UTC).format(FORMAT));
        } else if (v instanceof LocalDateTime) {
            return new CastFromStringSimpleExpressionCondition("date", field, op, ((LocalDateTime) v).atOffset(ZoneOffset.UTC).format(FORMAT));
        } else {
            throw new UnsupportedOperationException("Unrecognized " + v.getClass() + " " + v);
        }
    }
}
