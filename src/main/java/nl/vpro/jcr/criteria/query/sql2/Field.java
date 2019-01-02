package nl.vpro.jcr.criteria.query.sql2;

import lombok.Data;

/**
 * @author Michiel Meeuwissen
 * @since 1.3
 */
@Data
public class Field {

    private  String name;

    public static Field of(String name) {
        Field field = new Field();
        field.setName(name);
        return field;
    }

    public String toSql2() {
        return name;
    }

}
