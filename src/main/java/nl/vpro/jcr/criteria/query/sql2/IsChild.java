package nl.vpro.jcr.criteria.query.sql2;


import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Michiel Meeuwissen
 * @since 1.3
 */
@AllArgsConstructor
@Data
public class IsChild implements Condition {

    String path;
    @Override
    public void toSql2(StringBuilder builder) {
        builder.append("ISCHILDNODE(a, '").append(path).append("')");
    }
}
