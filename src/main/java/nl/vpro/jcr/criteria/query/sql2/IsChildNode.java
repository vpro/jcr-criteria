package nl.vpro.jcr.criteria.query.sql2;


import lombok.AllArgsConstructor;
import lombok.Data;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.jcr.criteria.query.criterion.Criterion;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@AllArgsConstructor
@Data
public class IsChildNode implements Condition {

    @NonNull
    final String path;

    @Override
    public boolean toSql2(StringBuilder builder) {
        if (! Criterion.ALL_ELEMENTS.equals(path)) {
            builder.append("ISCHILDNODE(a, '").append(Utils.escapeQuotedPath(path)).append("')");
            return true;
        }
        return false;
    }
}
