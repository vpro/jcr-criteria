package nl.vpro.jcr.criteria.query.sql2;

import lombok.Data;

/**
 * @author Michiel Meeuwissen
 * @since 1.3
 */
@Data
@lombok.AllArgsConstructor
@lombok.Builder
public class Order {

    final Field field;

    final OrderDirection direction;


    public void toSql2(StringBuilder builder) {
        field.toSql2(builder);
        builder.append(" ").append(direction.name());
    }

}
