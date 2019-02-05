package nl.vpro.jcr.criteria.query.sql2;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class LikeCondition implements  Condition {
    final Field field;
    final String value;

    public LikeCondition(String propertyName, String toMatchString) {
        this.field = Field.of(propertyName);
        this.value = toMatchString;
    }

    @Override
    public boolean toSql2(StringBuilder builder) {
        field.toSql2(builder);
        builder.append(" LIKE '").append(value).append("'");
        return true;

    }
}
