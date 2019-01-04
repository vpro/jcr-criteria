package nl.vpro.jcr.criteria.query.sql2;

import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Michiel Meeuwissen
 * @since 1.3
 */
@Data
public class AndCondition implements Condition {

    final List<Condition> clauses = new ArrayList<>();


    @Override
    public void toSql2(StringBuilder builder) {
        Iterator<Condition> i = clauses.iterator();
        while(i.hasNext()) {
            i.next().toSql2(builder);
            if (i.hasNext()) {
                builder.append(" AND ");
            }
        }
    }

    public boolean hasClauses() {
        return ! clauses.isEmpty();
    }
}
