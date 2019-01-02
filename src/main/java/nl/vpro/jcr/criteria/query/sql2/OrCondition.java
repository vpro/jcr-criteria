package nl.vpro.jcr.criteria.query.sql2;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Michiel Meeuwissen
 * @since 1.3
 */
public class OrCondition implements Condition {

    List<Condition> clauses;


    @Override
    public String toSql2() {
        return clauses.stream().map(Condition::toSql2).collect(Collectors.joining(" OR "));

    }
}
