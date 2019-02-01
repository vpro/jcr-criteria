package nl.vpro.jcr.criteria.query.sql2;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
        Iterator<Condition> i = clauses.iterator();
        int length = builder.length();
        String op = getBooleanOperator();
        while(i.hasNext()) {
            if (builder.length() > length) {
                builder.append(op);
            }
            i.next().toSql2(builder);
        }
        return builder.length() > length;
    }

    public boolean hasClauses() {
        return ! clauses.isEmpty();
    }
}
