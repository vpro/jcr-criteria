package nl.vpro.jcr.criteria.query.sql2;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public abstract class BooleanCondition implements Condition {

    @Getter
    final List<Condition> clauses = new ArrayList<>();

    protected BooleanCondition(Condition... clauses) {
        this.clauses.addAll(Arrays.asList(clauses));
    }

    abstract String getBooleanOperator();

    @Override
    public boolean toSql2(StringBuilder builder) {

        int location = builder.length();
        int appendCount = 0;
        String op = getBooleanOperator();
        for (Condition clause : clauses) {
            if (appendCount > 0) {
                builder.append(op);
            }
            if (clause.toSql2(builder)) {
                appendCount++;
            }
        }
        if (appendCount > 1) {
            //builder.insert(location, '(');
            //builder.append(')');
        }
        return appendCount > 0;
    }

    public boolean hasClauses() {
        return ! clauses.isEmpty();
    }
}
