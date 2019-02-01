package nl.vpro.jcr.criteria.query.sql2;

import nl.vpro.jcr.criteria.query.criterion.Op;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class CastFromStringSimpleExpressionCondition extends SimpleExpressionCondition<String> {
    private final String castTo;

    public CastFromStringSimpleExpressionCondition(String castTo, Field field, Op op, String value) {
        super(field, op, value);
        this.castTo = castTo;
    }

    @Override
    String getValue() {
        return "cast('" + value + "' as " + castTo + ")";
    }
}
