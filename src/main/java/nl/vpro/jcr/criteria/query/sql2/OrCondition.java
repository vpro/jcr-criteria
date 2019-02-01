package nl.vpro.jcr.criteria.query.sql2;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class OrCondition extends BooleanCondition {

    public OrCondition(Condition... clauses) {
        super(clauses);
    }

    @Override
    String getBooleanOperator() {
        return " OR ";

    }
}
