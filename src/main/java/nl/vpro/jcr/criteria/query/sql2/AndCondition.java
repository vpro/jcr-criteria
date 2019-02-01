package nl.vpro.jcr.criteria.query.sql2;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class AndCondition extends BooleanCondition {

    public AndCondition(Condition... clauses) {
        super(clauses);
    }

    @Override
    String getBooleanOperator() {
        return " AND ";
    }

}
