/*
 *
 * Criteria API for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlcriteria.html)
 * Copyright(C) 2009-2013, Openmind S.r.l. http://www.openmindonline.it
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

package nl.vpro.jcr.criteria.query.criterion;

import lombok.SneakyThrows;

import java.time.*;
import java.util.*;

import javax.jcr.Node;
import javax.validation.constraints.Size;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.jcr.criteria.query.xpath.utils.XPathTextUtils;


/**
 * The <tt>criterion</tt> package may be used by applications as a framework for building new kinds of
 * <tt>Criterion</tt>. However, it is intended that most applications will simply use the built-in criterion types via
 * the static factory methods of this class.
 *
 * Since 2.0 most methods do not expect generic {@link Object} any more but only types that are actually supported.
 *
 * @author fgiust
 * @author Federico Grilli
 * @author Michiel Meeuwissen
 */
public final class Restrictions {

    private Restrictions() {
        // cannot be instantiated
    }

    /**
     * Apply an "equal" constraint to the named node
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode), you
     * @param value - must be an instance of either {@link String} or {@link Number} or {@link Calendar}.
     * @return Criterion
     */
    @NonNull
    public static SimpleExpression eq(@NonNull String nodeName, @NonNull CharSequence value) {
        return new SimpleExpression(nodeName, value, Op.EQ);
    }


    /**
     * See {@link #eq(String, CharSequence)}
     */
    @NonNull
    public static SimpleExpression eq(@NonNull String nodeName, @NonNull UUID value) {
        return new SimpleExpression(nodeName, value.toString(), Op.EQ);
    }

    /**
     * See {@link #eq(String, CharSequence)}
     */
    @NonNull
    public static SimpleExpression eq(@NonNull String nodeName, @NonNull Number value) {
        return new SimpleExpression(nodeName, value, Op.EQ);
    }
    /**
     * See {@link #eq(String, CharSequence)}
     */
    @NonNull
    public static SimpleExpression eq(@NonNull String nodeName, @NonNull Calendar value) {
        return new SimpleExpression(nodeName, value, Op.EQ);
    }
      /**
     * See {@link #eq(String, CharSequence)}
     */
    @NonNull
    public static SimpleExpression eq(@NonNull String nodeName, @NonNull Instant value) {
        return new SimpleExpression(nodeName, value, Op.EQ);
    }

    /**
     * See {@link #eq(String, CharSequence)}
     */
     @NonNull
    public static SimpleExpression eq(@NonNull String nodeName, @NonNull Boolean value) {
        return new SimpleExpression(nodeName, value, Op.EQ);
    }

    /**
     * See {@link #eq(String, CharSequence)}
     * @since 2.0
     */
    @NonNull
    public static SimpleExpression eq(@NonNull String nodeName, @NonNull LocalDateTime value) {
        return new SimpleExpression(nodeName, value, Op.EQ);
    }

    /**
     * See {@link #eq(String, CharSequence)}
     * @since 2.0
     */
    @NonNull
    public static SimpleExpression eq(@NonNull String nodeName, @NonNull LocalDate value) {
        return new SimpleExpression(nodeName, value, Op.EQ);
    }

    /**
     * See {@link #eq(String, CharSequence)}, but wraps {@link #attr} arround the first argument.
     * @since 2.0
     */
    @NonNull
    public static SimpleExpression attrEq(@NonNull String attName, @NonNull CharSequence value) {
        return eq(attr(attName), value);
    }

    /**
     * See {@link #eq(String, CharSequence)}
     *  @since 2.0
     */
    @NonNull
    public static SimpleExpression attrEq(@NonNull String nodeName, @NonNull UUID value) {
        return eq(attr(nodeName), value);
    }

    /**
     * See {@link #attrEq(String, CharSequence)}
     * @since 2.0
     */
    @NonNull
    public static SimpleExpression attrEq(@NonNull String attName, @NonNull Number value) {
        return eq(attr(attName), value);
    }
    /**
     * See {@link #attrEq(String, CharSequence)}
     * @since 2.0
     */
    @NonNull
    public static SimpleExpression attrEq(@NonNull String attName, @NonNull Boolean value) {
        return eq(attr(attName), value);
    }
    /**
     * See {@link #attrEq(String, CharSequence)}
     * @since 2.0
     */
    @NonNull
    public static SimpleExpression attrEq(@NonNull String attName, @NonNull Calendar value) {
        return eq(attr(attName), value);
    }

     /**
     * See {@link #attrEq(String, CharSequence)}
     * @since 2.0
     */
    @NonNull
    public static SimpleExpression attrEq(@NonNull String attName, @NonNull Instant value) {
        return eq(attr(attName), value);
    }

    /**
     * See {@link #attrEq(String, CharSequence)}
     * @since 2.0
     */
    @NonNull
    public static SimpleExpression attrEq(@NonNull String attName, @NonNull LocalDateTime value) {
        return eq(attr(attName), value);
    }
    /**
     * See {@link #eq(String, CharSequence)}
     * @since 2.0
     */
    @NonNull
    public static SimpleExpression attrEq(@NonNull String nodeName, @NonNull LocalDate value) {
        return eq(attr(nodeName), value);
    }



    /**
     * Synonym to {@link #isNotNull(String)} (String)}
     */
    @NonNull
    public static Criterion has(@NonNull String name) {
        return isNotNull(name);
    }
    /**
     * Synonym to {@link #isNull(String)}
     * @since 2.0
     */
    @NonNull
    public static Criterion hasnt(@NonNull String name) {
        return isNull(name);
    }


    /**
     * @since 2.0
     */
    @NonNull
    public static SimpleExpression isTrue(@NonNull String name) {
        return eq(name, Boolean.TRUE);
    }

    /**
     * @since 2.0
     */
    @NonNull
    public static SimpleExpression attrIsTrue(@NonNull String name) {
        return isTrue(attr(name));
    }


    /**
     * @since 2.0
     */
    @NonNull
    public static Criterion isFalsy(@NonNull String name) {
        return or(eq(name, Boolean.FALSE), hasnt(name));
    }

     /**
     * @since 2.0
     */
    @NonNull
    public static Criterion attrIsFalsy(@NonNull String name) {
        return isFalsy(attr(name));
    }


    /**
     * Creates a generic expression, with an {@link Op} parameter and an {@link Object} value.
     * @since 2.0
     */
    public static SimpleExpression op(@NonNull Op op, @NonNull String nodeName, @NonNull Object value) {
        return new SimpleExpression(nodeName, value, op);
    }

    /**
     * Creates a generic expression, with an {@link Op} parameter and an {@link Object} value.
     * @since 2.0
     */
    @NonNull
    public static SimpleExpression attrOp(@NonNull Op op, @NonNull String attrName, @NonNull Object value) {
        return op(op, attr(attrName), value);
    }

    /**
     * Apply a "not equal" constraint to the named node
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @return Criterion
     */
    @NonNull
    public static SimpleExpression ne(@NonNull String nodeName, @NonNull CharSequence value) {
        return new SimpleExpression(nodeName, value, Op.NE);
    }
    /**
     * See {@link #ne(String, CharSequence)}
     */
    @NonNull
    public static SimpleExpression ne(@NonNull String nodeName, @NonNull Number value) {
        return new SimpleExpression(nodeName, value, Op.NE);
    }
    /**
     * See {@link #ne(String, CharSequence)}
     */
    @NonNull
    public static SimpleExpression ne(@NonNull String nodeName, @NonNull Calendar value) {
        return new SimpleExpression(nodeName, value, Op.NE);
    }
     /**
     * See {@link #ne(String, CharSequence)}
     */
    @NonNull
    public static SimpleExpression ne(@NonNull String nodeName, @NonNull Instant value) {
        return new SimpleExpression(nodeName, value, Op.NE);
    }
    /**
     * See {@link #ne(String, CharSequence)}
     */
    @NonNull
    public static SimpleExpression ne(@NonNull String nodeName, @NonNull LocalDateTime value) {
        return new SimpleExpression(nodeName, value, Op.NE);
    }
    /**
     * See {@link #ne(String, CharSequence)}
     */
    @NonNull
    public static SimpleExpression ne(@NonNull String nodeName, @NonNull Boolean value) {
        return new SimpleExpression(nodeName, value, Op.NE);
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
     * @return Criterion
     */
    @NonNull
    public static JCRFunctionExpression like(@NonNull String nodeName, @NonNull CharSequence value) {
        return like(nodeName, value, MatchMode.ANYWHERE);
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
     * @param matchMode - one of {@link MatchMode#START} or {@link MatchMode#END} or {@link MatchMode#ANYWHERE}
     * @return Criterion
     */
    @NonNull
    public static JCRFunctionExpression like(@NonNull String nodeName, @NonNull CharSequence value, @NonNull MatchMode matchMode) {
        return new LikeExpression(
            nodeName,
            XPathTextUtils.stringToJCRSearchExp(value.toString()),
            " jcr:like",
            matchMode);
    }


    /**
     * See {@link #like(String, CharSequence)}
     * @since 2.0
     */
    @NonNull
    public static JCRFunctionExpression attrLike(@NonNull String attName, @NonNull CharSequence value) {
        return like(attr(attName), value);
    }

    /**
     * @since 2.0
     */
    @NonNull
    public static JCRFunctionExpression attrLike(@NonNull String attName, @NonNull CharSequence value, @NonNull MatchMode matchMode) {
        return like(attr(attName), value, matchMode);
    }

    /**
     * Apply a "contains" constraint to the named node. Use this override with escape set to false if you want to keep
     * the search-engine syntax enabled (you are sure that the search-expression is always syntactically correct).
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @param escape - if true, value will be escaped, avoiding JCRQueryException on query execution
     * @return Criterion
     */
    @NonNull
    @Deprecated
    public static JCRFunctionExpression contains(@NonNull  String nodeName, @NonNull  CharSequence value, boolean escape) {
        CharSequence exp;
        if (escape) {
            exp = XPathTextUtils.stringToJCRSearchExp(value.toString());
        } else {
            exp = value;
        }
        return new JCRFunctionExpression(nodeName, exp, " jcr:contains");
    }

    /**
     * Apply a "contains" constraint to the named node. The value parameter will be escaped.
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @return Criterion
     */
    public static Criterion contains(@NonNull  String nodeName, @NonNull  CharSequence value) {
        return new ContainsExpression(nodeName, value);
    }

    /**
     * Apply a "greater than" constraint to the named node
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @return Criterion
     */
    @NonNull
    public static SimpleExpression gt(@NonNull String nodeName, @NonNull CharSequence value) {
        return new SimpleExpression(nodeName, value, Op.GT);
    }
    /**
     * See {@link #gt(String, CharSequence)}
     */
    @NonNull
    public static SimpleExpression gt(@NonNull String nodeName, @NonNull Number value) {
        return new SimpleExpression(nodeName, value, Op.GT);
    }

    /**
     * See {@link #gt(String, CharSequence)}
     */
    @NonNull
    public static SimpleExpression gt(@NonNull String nodeName, @NonNull Calendar value) {
        return new SimpleExpression(nodeName, value, Op.GT);
    }

    /**
     * See {@link #gt(String, CharSequence)}
     * @since 2.0
     */
    @NonNull
    public static SimpleExpression gt(@NonNull String nodeName, @NonNull LocalDateTime value) {
        return new SimpleExpression(nodeName, value, Op.GT);
    }



    /**
     * Apply a "less than" constraint to the named node
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode). This can be done via {@link #attr}.
     * @return Criterion
     */
    @NonNull
    public static SimpleExpression lt(@NonNull String nodeName, @NonNull CharSequence value) {
        return new SimpleExpression(nodeName, value, Op.LT);
    }

    /**
     * See {@link #lt(String, CharSequence)}
     */
    @NonNull
    public static SimpleExpression lt(@NonNull String nodeName, @NonNull Number value) {
        return new SimpleExpression(nodeName, value, Op.LT);
    }

    /**
     * See {@link #lt(String, CharSequence)}
     */
    @NonNull
    public static SimpleExpression lt(@NonNull String nodeName, @NonNull Calendar value) {
        return new SimpleExpression(nodeName, value, Op.LT);
    }

    /**
     * See {@link #lt(String, CharSequence)}
     * @since 2.0
     */
    @NonNull
    public static SimpleExpression lt(@NonNull String nodeName, @NonNull  LocalDateTime value) {
        return new SimpleExpression(nodeName, value, Op.LT);
    }

    /**
     * Apply a "less than or equal" constraint to the named node
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @return Criterion
     */
    @NonNull
    public static SimpleExpression le(@NonNull String nodeName, @NonNull CharSequence value) {
        return new SimpleExpression(nodeName, value, Op.LE);
    }

    /**
     * See {@link #le(String, CharSequence)}
     */
    @NonNull
    public static SimpleExpression le(@NonNull String nodeName, @NonNull Number value) {
        return new SimpleExpression(nodeName, value, Op.LE);
    }
    /**
     * See {@link #le(String, CharSequence)}
     */
    @NonNull
    public static SimpleExpression le(@NonNull String nodeName, @NonNull Calendar value) {
        return new SimpleExpression(nodeName, value, Op.LE);
    }
    /**
     * See {@link #le(String, CharSequence)}
     */
    @NonNull
    public static SimpleExpression le(@NonNull String nodeName, @NonNull LocalDateTime value) {
        return new SimpleExpression(nodeName, value, Op.LE);
    }

    /**
     * Apply a "greater than or equal" constraint to the named node
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @return Criterion
     */
    @NonNull
    public static SimpleExpression ge(@NonNull String nodeName, @NonNull CharSequence value) {
        return new SimpleExpression(nodeName, value, Op.GE);
    }
    @NonNull
    public static SimpleExpression ge(String nodeName, Number value) {
        return new SimpleExpression(nodeName, value, Op.GE);
    }
    @NonNull
    public static SimpleExpression ge(String nodeName, Calendar value) {
        return new SimpleExpression(nodeName, value, Op.GE);
    }
    @NonNull
    public static SimpleExpression ge(String nodeName, LocalDateTime value) {
        return new SimpleExpression(nodeName, value, Op.GE);
    }

    /**
     * Apply a "between" constraint to the named node
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @param lo value - must be an instance of either {@link String} or {@link Number} or {@link Calendar}.
     * @param hi value - must be an instance of either {@link String} or {@link Number} or {@link Calendar}.
     * @return Criterion
     */
    @NonNull
    public static Criterion between(String nodeName, CharSequence lo, CharSequence hi) {
        return between(nodeName, lo, true, hi, true);
    }

    /**
     * @since 2.0
     */
    @NonNull
    public static Criterion between(String nodeName, CharSequence lo, boolean lowerInclusive,  CharSequence hi, boolean higherInclusive) {
        return new BetweenExpression(nodeName, lo.toString(), lowerInclusive, hi.toString(), higherInclusive);
    }
    @NonNull
    public static Criterion between(String nodeName, Number lo, Number hi) {
        return between(nodeName, lo, true, hi, true);
    }
    /**
     * @since 2.0
     */
    @NonNull
    public static Criterion between(String nodeName, Number lo, boolean lowerInclusive, Number hi, boolean higherInclusive) {
        return new BetweenExpression(nodeName, (Comparable) lo, lowerInclusive, (Comparable) hi, higherInclusive);
    }
    @NonNull
    public static Criterion between(String nodeName, Calendar lo, Calendar hi) {
        return between(nodeName, lo, true, hi, true);
    }
    /**
     * @since 2.0
     */
    @NonNull
    public static Criterion between(String nodeName, Calendar lo, boolean lowerInclusive, Calendar hi, boolean higherInclusive) {
        return new BetweenExpression(nodeName, lo, lowerInclusive, hi, higherInclusive);
    }
    @NonNull
    public static Criterion between(String nodeName, LocalDateTime lo, LocalDateTime hi) {
        return between(nodeName, lo, true, hi, true);
    }
    /**
     * @since 2.0
     */
    @NonNull
    public static Criterion between(String nodeName, LocalDateTime lo, boolean lowerInclusive, LocalDateTime hi, boolean higherInclusive) {
        return new BetweenExpression(nodeName, lo, lowerInclusive, hi, higherInclusive);
    }
    @NonNull
    public static Criterion between(String nodeName, LocalDate lo, LocalDate hi) {
        return between(nodeName, lo, true, hi, true);
    }
    /**
     * @since 2.0
     */
    @NonNull
    public static Criterion between(String nodeName, LocalDate lo, boolean lowerInclusive, LocalDate hi, boolean higherInclusive) {
        return new BetweenExpression(nodeName, lo, lowerInclusive, hi, higherInclusive);
    }
    @NonNull
    public static Criterion between(String nodeName, Instant lo, Instant hi) {
        return between(nodeName, lo, true, hi, true);
    }
    /**
     * @since 2.0
     */
    @NonNull
    public static Criterion between(String nodeName, Instant lo, boolean lowerInclusive, Instant hi, boolean higherInclusive) {
        return new BetweenExpression(nodeName, lo, lowerInclusive, hi, higherInclusive);
    }


    /**
     * Adds a date contraint: the input date must be included in the given date, excluding time (between 00:00 and 23:59
     * of the given date)
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @param value date (time will be ignored)
     * @return Criterion
     */
    @NonNull
    public static Criterion eqDate(String nodeName, Calendar value) {
        return betweenDates(nodeName, value, value);
    }
    @NonNull
    public static Criterion eqDate(String nodeName, LocalDate value) {
        return between(nodeName, value, value);
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
    @NonNull
    public static Criterion betweenDates(String nodeName, Calendar lo, Calendar hi) {
        return between(nodeName, getDayStart(lo), getDayEnd(hi));
    }

    /**
     * Apply an "in" constraint to the named node
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @param values - a String[]
     * @return Criterion
     */
    @NonNull
    public static Criterion in(String nodeName, String... values) {
        return new InExpression(nodeName, values);
    }


     /**
     * Apply an "in" constraint on the jcr:uuid attribute
     * @param values - a UUID[]
     * @return Criterion
     */
    @NonNull
    public static Criterion in(UUID... values) {
        return new InExpression(Criterion.JCR_UUID, Arrays.stream(values).map(UUID::toString).toArray(String[]::new), false);
    }


    /**
     * Apply an "in" constraint on the jcr:uuid attribute
     * @param values - a collection of UUID's
     * @return Criterion
     */
    @NonNull
    public static Criterion in(Collection<UUID> values) {
        return new InExpression(Criterion.JCR_UUID, values.stream().map(UUID::toString).toArray(String[]::new), false);
    }

    /**
     * Apply an "in" constraint to the named node
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @param values - a collection of {@link String}
     * @return Criterion
     */
    @NonNull
    public static Criterion in(String nodeName, Collection<String> values) {
        return new InExpression(nodeName, values.toArray(new String[0]));
    }

    /**
     * Apply an "is null" constraint to the named node
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @return Criterion
     */
    @NonNull
    public static Criterion isNull(String nodeName) {
        return new IsNullExpression(nodeName);
    }

    /**
     * Apply an "is not null" constraint to the named node
     * @param nodeName - String a qualified (eg. nt:somenode) or unqualified (eg. somenode) node name. When a node is an
     * attribute it must be preceded by the '@'character (eg. @nt:somenode)
     * @return Criterion
     */
    @NonNull
    public static Criterion isNotNull(String nodeName) {
        return new IsNotNullExpression(nodeName);
    }

    /**
     * Return the conjuction of two or more expressions
     * @return Criterion
     */
    @NonNull
    public static Conjunction and(@Size(min = 2) Criterion... clauses) {
        return new Conjunction(true, clauses);
    }

    /**
     * Return the disjuction of two expressions

     * @return Criterion
     */
    @NonNull
    public static Disjunction or(@Size(min = 2) Criterion... clauses) {
        return new Disjunction(true, clauses);
    }

    /**
     * Return the negation of an expression
     * @param expression to be negated
     * @return Criterion
     */
    @NonNull
    public static Criterion not(Criterion expression) {
        return new NotExpression(expression);
    }

    /**
     * Apply a restriction on the node primary type. Shortcut for add(Restrictions.eq("@jcr:primaryType", value)).
     * @param nodetypes - list of accepted nodetypes.
     * @return Criterion
     */
    @NonNull
    public static Criterion hasNodeType(String... nodetypes) {
        // TODO
        return new InExpression(Criterion.JCR_PRIMARYTYPE, nodetypes, false);
    }

    /**
     * Group expressions together in a single conjunction (A and B and C...)
     * @return Conjunction
     * @deprecated {@link #and(Criterion...)}
     */
    @NonNull
    @Deprecated
    public static Conjunction conjunction() {
        return new Conjunction(true);
    }

    /**
     * Group expressions together in a single disjunction (A or B or C...)
     * @deprecated {@link #or(Criterion...)}
     * @return Conjunction
     */
    @NonNull
    @Deprecated
    public static Disjunction disjunction() {
        return new Disjunction(true);
    }

    /**
     * @since 2.0
     */
    @NonNull
    public static String attr(@NonNull String attName) {
        return Criterion.ATTRIBUTE_SELECTOR + attName;
    }


    private static Calendar getDayStart(Calendar cal) {
        Calendar cal2 = Calendar.getInstance();
        cal2.clear();
        cal2.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        return cal2;
    }

    private static Calendar getDayEnd(Calendar cal) {
        Calendar cal2 = getDayStart(cal);
        cal2.add(Calendar.DAY_OF_YEAR, 1);
        cal2.add(Calendar.MILLISECOND, -1);
        return cal2;
    }

    /**
     * @since 2.0
     */
    @SneakyThrows
    public static Criterion isDescendantOf(Node issueNode) {
        return isDescendantOf(issueNode.getPath());
    }

    /**
     * @since 2.1
     */
    public static Criterion isDescendantOf(String path) {
        return new IsDescendantOf(path);
    }


      /**
     * @since 2.1
     */
    public static Criterion isSame(String path) {
        return new IsSame(path);
    }


      /**
     * @since 2.1
     */
      @SneakyThrows
    public static Criterion isSame(Node node) {
        return isSame(node.getPath());
    }




    /**
     * @since 2.0
     */
    @SneakyThrows
    public static Criterion isChildOf(Node issueNode) {
        return isChildOf(issueNode.getPath());
    }

    /**
     * @since 2.1
     */
    public static Criterion isChildOf(String path) {
        return new IsChildOf(path);
    }

    /**
     * @since 2.11
     */
    public static Criterion name(final String name) {
        return new Name(name);
    }
}
