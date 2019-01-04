package nl.vpro.jcr.criteria.query.sql2;

import lombok.Data;

import nl.vpro.jcr.criteria.query.criterion.Criterion;

/**
 * @author Michiel Meeuwissen
 * @since 1.3
 */
@Data
public class Field {

    private  String name;

    private boolean attribute;

    public static Field of(String name) {
        Field field = new Field();
        if (name.startsWith(Criterion.ATTRIBUTE_SELECTOR)) {
            field.setName(name.substring(Criterion.ATTRIBUTE_SELECTOR.length()));
            field.setAttribute(true);
        } else {
            field.setName(name);
        }
        return field;
    }

    public void toSql2(StringBuilder builder) {
        builder.append("[").append(name).append("]");
    }

}
