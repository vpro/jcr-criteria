package nl.vpro.jcr.criteria.query.criterion;

import lombok.Getter;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public enum Op {
    eq("="),
    ne("!="),
    lt("<"),
    le("<="),
    gt(">"),
    ge(">="),
    contains(null),
    like(null);

    @Getter
    private final String xpath;

    Op(String xpath) {
        this.xpath = xpath;
    }

    @Override
    public String toString() {
        return this.xpath;
    }
}
