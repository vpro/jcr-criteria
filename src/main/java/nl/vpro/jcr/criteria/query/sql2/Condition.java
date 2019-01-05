package nl.vpro.jcr.criteria.query.sql2;

/**
 * @author Michiel Meeuwissen
 * @since 1.3
 */
public interface Condition {

    boolean toSql2(StringBuilder builder);


}
