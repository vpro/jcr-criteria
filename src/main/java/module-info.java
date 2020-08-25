/**
 * @author Michiel Meeuwissen
 * @since 2.3
 */
module nl.vpro.jcr.criteria {


    requires static org.checkerframework.checker.qual;
    requires static lombok;

    requires jcr;
    requires jackrabbit.jcr.commons;
    requires java.validation;
    requires org.apache.commons.lang3;
    requires org.slf4j;

    exports nl.vpro.jcr.criteria.query;
    exports nl.vpro.jcr.criteria.query.criterion;
    exports nl.vpro.jcr.criteria.query.sql2;

}
