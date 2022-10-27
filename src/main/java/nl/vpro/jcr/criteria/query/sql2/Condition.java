package nl.vpro.jcr.criteria.query.sql2;

/**
 *
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public interface Condition {


    /**
     * @return {@code true} if something appended
     */
    boolean toSql2(StringBuilder builder);


}
