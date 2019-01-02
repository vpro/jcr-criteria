package nl.vpro.jcr.criteria.query.sql2;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Michiel Meeuwissen
 * @since 1.3
 */
@Data
public class AndCondition implements Condition {

    final List<Condition> clauses = new ArrayList<>();


    @Override
    public String toSql2() {
        return clauses.stream().map(Condition::toSql2).collect(Collectors.joining(" AND "));
    }

    public boolean hasClauses() {
        return ! clauses.isEmpty();
    }
}
