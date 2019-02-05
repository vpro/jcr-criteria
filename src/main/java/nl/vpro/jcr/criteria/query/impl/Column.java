package nl.vpro.jcr.criteria.query.impl;

import lombok.Getter;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class Column {

    public static Column ALL = new Column("", "*");
    public static Column EXCERPT = new Column("rep:excerpt(%s)", "excerpt(%s)");


    @Getter
    private final String xpath;
    @Getter
    private final String sql2;


    public Column(String xpath, String sql2) {
        this.xpath = xpath;
        this.sql2 = sql2;
    }
}
