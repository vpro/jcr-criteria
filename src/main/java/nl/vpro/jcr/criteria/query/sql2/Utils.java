package nl.vpro.jcr.criteria.query.sql2;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
class Utils {


    static String escapeQuotedPath(String path) {
        return path.replaceAll("'", "''");
    }

}
