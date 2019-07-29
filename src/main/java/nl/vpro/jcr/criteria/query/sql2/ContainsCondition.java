package nl.vpro.jcr.criteria.query.sql2;

import lombok.Data;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@Data
public class ContainsCondition implements  Condition {
    final Field field;
    final String value;

    public ContainsCondition(@NonNull  String propertyName, @NonNull  String toMatchString) {
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
