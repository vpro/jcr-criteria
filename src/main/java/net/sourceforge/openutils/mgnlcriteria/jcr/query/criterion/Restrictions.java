/**
 *
 * Criteria API for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlcriteria.html)
 * Copyright(C) 2009-2011, Openmind S.r.l. http://www.openmindonline.it
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion;

import java.util.Calendar;
import java.util.Collection;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.xpath.utils.XPathTextUtils;


/**
 * The <tt>criterion</tt> package may be used by applications as a framework for building new kinds of
 * <tt>Criterion</tt>. However, it is intended that most applications will simply use the built-in criterion types via
 * the static factory methods of this class.
 * @author fgiust
 * @author Federico Grilli
 * @version $Id$
 */
public final class Restrictions
{

    private Restrictions()
    {
        // cannot be instantiated
    }

    /**
     * Apply an "equal" constraint to the named node
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @param value - must be an instance of either {@link String} or {@link Number} or {@link Calendar}.
     * @return Criterion
     */
    public static SimpleExpression eq(String nodeName, Object value)
    {
        return new SimpleExpression(nodeName, value, "=");
    }

    /**
     * Apply a "not equal" constraint to the named node
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @param value - must be an instance of either {@link String} or {@link Number} or {@link Calendar}.
     * @return Criterion
     */
    public static SimpleExpression ne(String nodeName, Object value)
    {
        return new SimpleExpression(nodeName, value, "!=");
    }

    /**
     * Apply a "like" constraint of type {@link MatchMode#ANYWHERE} to the named node <br>
     * <br>
     * <strong>Be warned when using <em>jcr:like</em> function, as it can be very slow.</strong> <br>
     * <br>
     * The following account of why it is so, was taken from <a href=
     * "http://www.nabble.com/Explanation-and-solutions-of-some-Jackrabbit-queries-regarding-performance-td15028655.html"
     * >users@jackrabbit.apache.org mailing list</a> <br>
     * <em>
     * <ul>
     * <li>Question: My xpath is '//*[jcr:like(@propertyName,
     * '%somevalue%')]' and it takes minutes to complete.
     * 
     * <li>Answer: a jcr:like with % will be translated to a WildcardQuery lucene
     * query. In order to prevent extremely slow WildcardQueries, a Wildcard
     * term should not start with one of the wildcards * or ?. So this is not a
     * Jackrabbit implementation detail, but a general Lucene (and I think
     * inverted indexes in general) issue [1]
     * 
     * <li>Conclusion: Avoid % prefixes in jcr:like. Use jcr:contains when
     * searching for a specific word. If jcr:contains is not suitable, you can
     * work around the problem by creating a custom lucene analyzer for the
     * specific propery (see IndexingConfiguration [2] at Index Analyzers).
     * </ul>
     * </em>
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @param value - must be an instance of either {@link String} or {@link Number}.
     * @return Criterion
     */
    public static JCRFunctionExpression like(String nodeName, Object value)
    {
        return new LikeExpression(
            nodeName,
            XPathTextUtils.stringToJCRSearchExp(value.toString()),
            " jcr:like",
            MatchMode.ANYWHERE);

    }

    /**
     * Apply a "like" constraint to the named node <br>
     * <br>
     * <strong>Be warned when using <em>jcr:like</em> function, as it can be very slow.</strong> <br>
     * <br>
     * The following account of why it is so, was taken from <a href=
     * "http://www.nabble.com/Explanation-and-solutions-of-some-Jackrabbit-queries-regarding-performance-td15028655.html"
     * >users@jackrabbit.apache.org mailing list</a> <br>
     * <em>
     * <ul>
     * <li>Question: My xpath is '//*[jcr:like(@propertyName, '%somevalue%')]' and it takes minutes to complete.
     * 
     * <li>Answer: a jcr:like with % will be translated to a WildcardQuery lucene
     * query. In order to prevent extremely slow WildcardQueries, a Wildcard
     * term should not start with one of the wildcards * or ?. So this is not a
     * Jackrabbit implementation detail, but a general Lucene (and I think
     * inverted indexes in general) issue [1]
     * 
     * <li>Conclusion: Avoid % prefixes in jcr:like. Use jcr:contains when
     * searching for a specific word. If jcr:contains is not suitable, you can
     * work around the problem by creating a custom lucene analyzer for the
     * specific propery (see IndexingConfiguration [2] at Index Analyzers).
     * </ul>
     * </em>
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @param value - must be an instance of either {@link String} or {@link Number}.
     * @param matchMode - one of {@link MatchMode#START} or {@link MatchMode#END} or {@link MatchMode#ANYWHERE}
     * @return Criterion
     */
    public static JCRFunctionExpression like(String nodeName, Object value, MatchMode matchMode)
    {
        return new LikeExpression(
            nodeName,
            XPathTextUtils.stringToJCRSearchExp(value.toString()),
            " jcr:like",
            matchMode);
    }

    /**
     * Apply a "contains" constraint to the named node. The value parameter will be escaped.
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @param value - must be an instance of either {@link String} or {@link Number}.
     * @return Criterion
     */
    public static JCRFunctionExpression contains(String nodeName, Object value)
    {
        return contains(nodeName, value, true);
    }

    /**
     * Apply a "contains" constraint to the named node. Use this override with escape set to false if you want to keep
     * the search-engine syntax enabled (you are sure that the search-expression is always syntactically correct).
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @param value - must be an instance of either {@link String} or {@link Number}.
     * @param escape - if true, value will be escaped, avoiding JCRQueryException on query execution
     * @return Criterion
     */
    public static JCRFunctionExpression contains(String nodeName, Object value, boolean escape)
    {
        String exp;
        if (escape)
        {
            exp = XPathTextUtils.stringToJCRSearchExp(value.toString());
        }
        else
        {
            exp = value.toString();
        }
        return new JCRFunctionExpression(nodeName, exp, " jcr:contains");
    }

    /**
     * Apply a "greater than" constraint to the named node
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @param value - must be an instance of either {@link String} or {@link Number} or {@link Calendar}.
     * @return Criterion
     */
    public static SimpleExpression gt(String nodeName, Object value)
    {
        return new SimpleExpression(nodeName, value, ">");
    }

    /**
     * Apply a "less than" constraint to the named node
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @param value - must be an instance of either {@link String} or {@link Number} or {@link Calendar}.
     * @return Criterion
     */
    public static SimpleExpression lt(String nodeName, Object value)
    {
        return new SimpleExpression(nodeName, value, "<");
    }

    /**
     * Apply a "less than or equal" constraint to the named node
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @param value - must be an instance of either {@link String} or {@link Number} or {@link Calendar}.
     * @return Criterion
     */
    public static SimpleExpression le(String nodeName, Object value)
    {
        return new SimpleExpression(nodeName, value, "<=");
    }

    /**
     * Apply a "greater than or equal" constraint to the named node
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @param value - must be an instance of either {@link String} or {@link Number} or {@link Calendar}.
     * @return Criterion
     */
    public static SimpleExpression ge(String nodeName, Object value)
    {
        return new SimpleExpression(nodeName, value, ">=");
    }

    /**
     * Apply a "between" constraint to the named node
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @param lo value - must be an instance of either {@link String} or {@link Number} or {@link Calendar}.
     * @param hi value - must be an instance of either {@link String} or {@link Number} or {@link Calendar}.
     * @return Criterion
     */
    public static Criterion between(String nodeName, Object lo, Object hi)
    {
        return new BetweenExpression(nodeName, lo, hi);
    }

    /**
     * Adds a date contraint: the input date must be included in the given date, excluding time (between 00:00 and 23:59
     * of the given date)
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @param value date (time will be ignored)
     * @return Criterion
     */
    public static Criterion eqDate(String nodeName, Calendar value)
    {
        return betweenDates(nodeName, value, value);
    }

    /**
     * Adds a date contraint: the input date must be included in the given dates (between 00:00 of the first date to
     * 23:59 of the last date)
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @param lo lower date
     * @param hi higher date
     * @return Criterion
     */
    public static Criterion betweenDates(String nodeName, Calendar lo, Calendar hi)
    {
        return between(nodeName, getDayStart(lo), getDayEnd(hi));
    }

    /**
     * Apply an "in" constraint to the named node
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @param values - a String[]
     * @return Criterion
     */
    public static Criterion in(String nodeName, String[] values)
    {
        return new InExpression(nodeName, values);
    }

    /**
     * Apply an "in" constraint to the named node
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @param values - a collection of {@link String}
     * @return Criterion
     */

    public static Criterion in(String nodeName, Collection<String> values)
    {
        return new InExpression(nodeName, values.toArray(new String[values.size()]));
    }

    /**
     * Apply an "is null" constraint to the named node
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @return Criterion
     */
    public static Criterion isNull(String nodeName)
    {
        return new IsNullExpression(nodeName);
    }

    /**
     * Apply an "is not null" constraint to the named node
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @return Criterion
     */
    public static Criterion isNotNull(String nodeName)
    {
        return new IsNotNullExpression(nodeName);
    }

    /**
     * Return the conjuction of two expressions
     * @param lhs left expression
     * @param rhs right expression
     * @return Criterion
     */
    public static LogicalExpression and(Criterion lhs, Criterion rhs)
    {
        return new LogicalExpression(lhs, rhs, "and");
    }

    /**
     * Return the disjuction of two expressions
     * @param lhs left expression
     * @param rhs right expression
     * @return Criterion
     */
    public static LogicalExpression or(Criterion lhs, Criterion rhs)
    {
        return new LogicalExpression(lhs, rhs, "or");
    }

    /**
     * Return the negation of an expression
     * @param expression to be negated
     * @return Criterion
     */
    public static Criterion not(Criterion expression)
    {
        return new NotExpression(expression);
    }

    /**
     * Group expressions together in a single conjunction (A and B and C...)
     * @return Conjunction
     */
    public static Conjunction conjunction()
    {
        return new Conjunction();
    }

    /**
     * Group expressions together in a single disjunction (A or B or C...)
     * @return Conjunction
     */
    public static Disjunction disjunction()
    {
        return new Disjunction();
    }

    private static Calendar getDayStart(Calendar cal)
    {
        Calendar cal2 = Calendar.getInstance();
        cal2.clear();
        cal2.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        return cal2;
    }

    private static Calendar getDayEnd(Calendar cal)
    {
        Calendar cal2 = getDayStart(cal);
        cal2.add(Calendar.DAY_OF_YEAR, 1);
        cal2.add(Calendar.MILLISECOND, -1);
        return cal2;
    }
}