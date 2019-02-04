package nl.vpro.jcr.criteria.query.sql2;


import lombok.AllArgsConstructor;
import lombok.Data;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import nl.vpro.jcr.criteria.query.criterion.Criterion;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@AllArgsConstructor
@Data
public class IsDescendantNode implements Condition {

    final String path;

    protected IsDescendantNode(Node node) throws RepositoryException {
        this(node.getPath());

    }

    @Override
    public boolean toSql2(StringBuilder builder) {
        if (path != null && ! "/".equals(path) && ! Criterion.ALL_ELEMENTS.equals(path)) {
            builder.append("ISDESCENDANTNODE(a, '").append(path).append("')");
            return true;
        }
        return false;
    }
}
