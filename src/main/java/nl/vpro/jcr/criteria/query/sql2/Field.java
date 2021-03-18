package nl.vpro.jcr.criteria.query.sql2;

import lombok.Data;
import lombok.EqualsAndHashCode;

import nl.vpro.jcr.criteria.query.criterion.Criterion;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@Data
@EqualsAndHashCode
public class Field {

    private final String name;

    private final boolean attribute;

    private final boolean needsBrackets;

    public static Field of(String name) {
        String n;
        boolean a;
        boolean needsBrackets = true;
        if (name.length() > 0 && name.charAt(0) == Criterion.ATTRIBUTE_SELECTOR) {
            n = name.substring(1);
            a = true;
        } else {
            n = name;
            a = false;
        }
        if (n.contains("*")) {
            needsBrackets = false;
        }
        Field field = new Field(n, a, needsBrackets);
        return field;
    }

    public void toSql2(StringBuilder builder) {
        if (needsBrackets) {
            builder.append('[');
        }
        builder.append(name);
        if (needsBrackets) {
            builder.append(']');
        }
    }

}
