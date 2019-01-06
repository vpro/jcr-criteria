package nl.vpro.jcr.criteria.query.sql2;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class NullCondition implements  Condition {
    Field field;

    public NullCondition(String propertyName) {
        this.field = Field.of(propertyName);
    }

    @Override
    public boolean toSql2(StringBuilder builder) {
        field.toSql2(builder);
        builder.append(" IS NULL");
        return true;

    }
}
