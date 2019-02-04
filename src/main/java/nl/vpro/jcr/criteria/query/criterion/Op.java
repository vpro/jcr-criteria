package nl.vpro.jcr.criteria.query.criterion;

import lombok.Getter;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public enum Op {
    EQ("="),
    NE("!="),
    LT("<"),
    LE("<="),
    GT(">"),
    GE(">="),
    CONTAINS(null),
    LIKE(null);

    @Getter
    private final String xpath;

    Op(String xpath) {
        this.xpath = xpath;
    }

    @Override
    public String toString() {
        return this.xpath;
    }

    public static Op valueOfIgnoreCase(String value) {
        return value == null ? null : Op.valueOf(value.toUpperCase());
    }
}
