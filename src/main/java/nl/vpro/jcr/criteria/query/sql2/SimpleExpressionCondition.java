package nl.vpro.jcr.criteria.query.sql2;

import lombok.SneakyThrows;

import java.time.ZoneId;
import java.util.Calendar;

import javax.annotation.Nonnull;

import org.apache.jackrabbit.value.ValueFactoryImpl;

import nl.vpro.jcr.criteria.query.criterion.Op;
import nl.vpro.jcr.utils.Utils;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public abstract class SimpleExpressionCondition<T> implements  Condition {


    final Field field;
    final Op op;
    final T value;


    protected SimpleExpressionCondition(@Nonnull  Field field, @Nonnull  Op op, @Nonnull  T value) {
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

    @SneakyThrows
    public static SimpleExpressionCondition<?> of(Field field, Op op, Object v, ZoneId zoneId) {
        v = Utils.toCalendarIfPossible(v, zoneId);
        if (v instanceof CharSequence) {
            return new StringSimpleExpressionCondition(field, op, v.toString());
        } else if (v instanceof Boolean) {
            return new BooleanSimpleExpressionCondition(field, op, (Boolean) v);
        } else if (v instanceof Number) {
            return new NumberSimpleExpressionCondition(field, op, (Number) v);
        } else if (v instanceof Calendar) {
            return new CastFromStringSimpleExpressionCondition("date", field, op, ValueFactoryImpl.getInstance().createValue((Calendar) v).getString());
        } else {
            throw new UnsupportedOperationException("Unrecognized " + v.getClass() + " " + v);
        }
    }
}
