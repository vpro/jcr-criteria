package nl.vpro.jcr.criteria.query.sql2;


import lombok.AllArgsConstructor;
import lombok.Data;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.jcr.criteria.query.criterion.Criterion;

import static nl.vpro.jcr.criteria.query.sql2.Utils.escapeQuotedPath;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@AllArgsConstructor
@Data
public class IsDescendantNode implements Condition {

    @NonNull
    final String path;

    @Override
    public boolean toSql2(StringBuilder builder) {
        if (! "/".equals(path) && ! Criterion.ALL_ELEMENTS.equals(path)) {
            builder.append("ISDESCENDANTNODE(a, '").append(escapeQuotedPath(path)).append("')");
            return true;
        }
        return false;
    }
}
