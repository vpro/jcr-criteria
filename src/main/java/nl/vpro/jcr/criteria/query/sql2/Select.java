package nl.vpro.jcr.criteria.query.sql2;

import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import nl.vpro.jcr.criteria.query.TranslatableCriteria;
import nl.vpro.jcr.criteria.query.impl.AbstractCriteriaImpl;
import nl.vpro.jcr.criteria.query.impl.Column;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@Data
public class Select {
    String type = "nt:base";
    final AndCondition condition = new AndCondition();
    final List<Order> order = new ArrayList<>();
    final List<Column> columns = new ArrayList<>();

    {
        columns.add(Column.ALL);
    }

    public String toSql2() {
        StringBuilder builder = new StringBuilder("SELECT ");
        builder.append(columns.stream().map(Column::getSql2).map(s -> String.format(s, "a")).collect(Collectors.joining(",")));
        builder.append(" from ");
        builder.append("[").append(type).append("] as a");
        if (condition.hasClauses()) {
            int length = builder.length();
            if (condition.toSql2(builder)) {
                builder.insert(length, " WHERE ");
            }
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
        select.getColumns().clear();
        select.getColumns().addAll(criteria.getColumns());
        if (criteria.getBasePath() != null) {
            select.condition.clauses.add(new IsDescendantNode(criteria.getBasePath()));
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
                .direction(orderEntry.getOrder().isAscending() ? OrderDirection.ASC : OrderDirection.DESC)
                .field(Field.of(orderEntry.getOrder().getNodeName()))
                .build());
        }
        return select;
    }

}
