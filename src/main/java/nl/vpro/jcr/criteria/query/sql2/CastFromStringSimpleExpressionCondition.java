package nl.vpro.jcr.criteria.query.sql2;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.jcr.criteria.query.criterion.Op;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class CastFromStringSimpleExpressionCondition extends SimpleExpressionCondition<String> {

    @NonNull
    private final String castTo;

    public CastFromStringSimpleExpressionCondition(
        @NonNull  String castTo, @NonNull Field field, @NonNull Op op, String value) {
        super(field, op, value);
        this.castTo = castTo;
    }

    @Override
    String getValue() {
        return "cast('" + value + "' as " + castTo + ")";
    }
}
