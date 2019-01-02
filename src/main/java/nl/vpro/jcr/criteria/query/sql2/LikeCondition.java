package nl.vpro.jcr.criteria.query.sql2;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class LikeCondition implements  Condition {
    Field field;
    String value;

    public LikeCondition(String propertyName, String toMatchString) {
        this.field = Field.of(propertyName);
        this.value = toMatchString;
    }

    @Override
    public String toSql2() {
        return field.toSql2() + " LIKE '" + value + "'";

    }
}
