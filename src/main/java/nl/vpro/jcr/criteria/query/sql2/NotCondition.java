package nl.vpro.jcr.criteria.query.sql2;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class NotCondition implements Condition {

    private final Condition wrapped;

    public NotCondition(Condition wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public boolean toSql2(StringBuilder builder) {
        builder.append("(not (");
        wrapped.toSql2(builder);
        builder.append("))");
        return true;
    }
}
