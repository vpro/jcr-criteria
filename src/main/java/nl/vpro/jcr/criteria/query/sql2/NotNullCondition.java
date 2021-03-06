package nl.vpro.jcr.criteria.query.sql2;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class NotNullCondition implements  Condition {
    final Field field;

    public NotNullCondition(String propertyName) {
        this.field = Field.of(propertyName);
    }

    @Override
    public boolean toSql2(StringBuilder builder) {
        field.toSql2(builder);
        builder.append(" IS NOT NULL");
        return true;

    }
}
