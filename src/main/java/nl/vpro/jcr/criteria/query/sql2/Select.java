package nl.vpro.jcr.criteria.query.sql2;

import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.vpro.jcr.criteria.query.TranslatableCriteria;
import nl.vpro.jcr.criteria.query.impl.AbstractCriteriaImpl;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@Data
public class Select {
    String type = "nt:base";
    final AndCondition condition = new AndCondition();
    final List<Order> order = new ArrayList<>();

    public String toSql2() {
        StringBuilder builder = new StringBuilder("SELECT * from ");
        builder.append("[").append(type).append("] as a");
        if (condition.hasClauses()) {
            builder.append(" WHERE ");
            condition.toSql2(builder);
        }
        if (! order.isEmpty()) {
            builder.append(" ORDER BY ");
            Iterator<Order> iterator = order.iterator();
            while (iterator.hasNext()) {
                iterator.next().toSql2(builder);
                if (iterator.hasNext()) {
                    builder.append(", ");
                }

            }
        }
        return builder.toString();
    }

    public static Select from(AbstractCriteriaImpl criteria) {
        Select select = new Select();
        if (criteria.getBasePath() != null) {
            select.condition.clauses.add(new IsChild(criteria.getBasePath()));
        }
        if (criteria.getType() != null) {
            select.type = criteria.getType();
        }
        for (TranslatableCriteria.CriterionEntry e : criteria.getCriterionEntries()) {
            select.condition.clauses.add(e.getCriterion().toSQLCondition(criteria));
        }
        for (TranslatableCriteria.OrderEntry orderEntry : criteria.getOrderEntries()) {
            select.getOrder().add(Order
                .builder()
                .direction(orderEntry.getOrder().isAscending() ? OrderDirection.asc : OrderDirection.desc)
                .field(Field.of(orderEntry.getOrder().getNodeName()))
                .build());
        }
        return select;
    }

}
