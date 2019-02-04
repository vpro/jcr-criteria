package nl.vpro.jcr.criteria.query.sql2;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class ContainsCondition implements  Condition {
    final Field field;
    final String value;

    public ContainsCondition(String propertyName, String toMatchString) {
        this.field = Field.of(propertyName);
        this.value = toMatchString;
    }

    @Override
    public boolean toSql2(StringBuilder builder) {
        builder.append(" CONTAINS(");
        field.toSql2(builder);
        builder
            .append(", '").append(value)
            .append("')");
        return true;
    }
}
