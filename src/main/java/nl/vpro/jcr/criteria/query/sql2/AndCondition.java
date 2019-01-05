package nl.vpro.jcr.criteria.query.sql2;

/**
 * @author Michiel Meeuwissen
 * @since 1.3
 */
public class AndCondition extends BooleanCondition {

    @Override
    String getBooleanOperator() {
        return " AND ";
    }

}
