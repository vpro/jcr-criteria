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
    public String toSql2() {
        return "ISCHILDNODE(a, '" + path + "')";
    }
}
