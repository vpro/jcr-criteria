package nl.vpro.jcr.criteria.query.sql2;


import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Michiel Meeuwissen
 * @since 1.3
 */
@AllArgsConstructor
@Data
class IsChild implements Condition {

    String path;
    @Override
    public boolean toSql2(StringBuilder builder) {
        if (path != null && ! "/".equals(path)) {
            builder.append("ISCHILDNODE(a, '").append(path).append("')");
            return true;
        }
        return false;
    }
}
