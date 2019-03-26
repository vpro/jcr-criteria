package nl.vpro.jcr.criteria.query.sql2;


import lombok.AllArgsConstructor;
import lombok.Data;

import javax.annotation.Nonnull;

import static nl.vpro.jcr.criteria.query.sql2.Utils.escapeQuotedPath;

/**
 * @author Michiel Meeuwissen
 * @since 2.1
 */
@AllArgsConstructor
@Data
public class IsSameNode implements Condition {

    @Nonnull
    final String path;

    @Override
    public boolean toSql2(StringBuilder builder) {
        builder.append("ISSAMENODE(a, '").append(escapeQuotedPath(path)).append("')");
        return true;
    }
}
