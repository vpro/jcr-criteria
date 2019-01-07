package nl.vpro.jcr.criteria.query.sql2;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public interface Condition {

    boolean toSql2(StringBuilder builder);


}
