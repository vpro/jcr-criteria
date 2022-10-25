package nl.vpro.jcr.criteria.query.sql2;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.checkerframework.checker.nullness.qual.NonNull;

import static nl.vpro.jcr.criteria.query.sql2.Utils.escapeQuotedPath;

/**
 * @since 2.11
 */
@AllArgsConstructor
@Data
public class Name implements Condition {

    @NonNull
    final String name;

    @Override
    public boolean toSql2(StringBuilder builder) {
        builder.append("NAME(a) = '").append(escapeQuotedPath(name)).append("'");
        return true;
    }
}
