package nl.vpro.jcr.criteria.query.sql2;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import nl.vpro.jcr.criteria.query.TranslatableCriteria;
import nl.vpro.jcr.criteria.query.impl.AbstractCriteriaImpl;

/**
 * @author Michiel Meeuwissen
 * @since 1.3
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
            builder.append(condition.toSql2());
        }
        if (! order.isEmpty()) {
            builder.append(" ORDER BY ");
            builder.append(order.stream().map(Order::toSql2).collect(Collectors.joining(", ")));
        }
        return builder.toString();
    }

    public static Select from(AbstractCriteriaImpl criteria) {
        Select select = new Select();
        if (criteria.getBasePath() != null) {
            select.condition.clauses.add(new IsChild(criteria.getBasePath()));
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
