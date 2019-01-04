package nl.vpro.jcr.criteria.query.sql2;

/**
 * @author Michiel Meeuwissen
 * @since 1.3
 */
public class LikeCondition implements  Condition {
    final Field field;
    final String value;

    public LikeCondition(String propertyName, String toMatchString) {
        this.field = Field.of(propertyName);
        this.value = toMatchString;
    }

    @Override
    public String toSql2() {
        return field.toSql2() + " LIKE '" + value + "'";

    }
}
