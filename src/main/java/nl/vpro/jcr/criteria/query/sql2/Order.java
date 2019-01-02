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


    public String toSql2() {
        return field.toSql2() + " " + direction.name();
    }

}
