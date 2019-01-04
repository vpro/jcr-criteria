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
    public void toSql2(StringBuilder builder) {
        field.toSql2(builder);
        builder.append(" LIKE '").append(value).append("'");

    }
}
