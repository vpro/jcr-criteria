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

import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.Calendar;

import javax.annotation.Nonnull;

import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.JCRQueryException;
import nl.vpro.jcr.criteria.query.sql2.AndCondition;
import nl.vpro.jcr.criteria.query.sql2.Condition;
import nl.vpro.jcr.criteria.query.sql2.Field;
import nl.vpro.jcr.criteria.query.sql2.SimpleExpressionCondition;
import nl.vpro.jcr.criteria.query.xpath.utils.XPathTextUtils;

import static nl.vpro.jcr.utils.Utils.toCalendarIfPossible;


/**
 * @author fgrilli
 */
@EqualsAndHashCode(callSuper = true)
public class BetweenExpression extends BaseCriterion implements Criterion {

    private static final long serialVersionUID = 6686395240415024541L;

    private final String propertyName;

    private final Comparable<?> lo;

    private final boolean lowerInclusive;

    private final Comparable<?> hi;

    private final boolean higherInclusive;

    protected BetweenExpression(
        @NonNull @Nonnull String propertyName,
        @NonNull @Nonnull Comparable<?> lo, boolean lowerInclusive,
        @NonNull @Nonnull  Comparable<?> hi, boolean higherInclusive) {
        this.propertyName = propertyName;
        this.lo = lo;
        this.lowerInclusive = lowerInclusive;
        this.hi = hi;
        this.higherInclusive = higherInclusive;
    }

    @Override
    public String toString() {
        return propertyName + " between " + (lowerInclusive ? "[" : "<") + lo +
            ", " + hi + (higherInclusive ? "]" : ">");
    }

    @Override
    public String toXPathString(Criteria criteria) throws JCRQueryException {
        StringBuilder fragment = new StringBuilder();
        fragment.append(" (").append(propertyName).append(lowerOp().getXpath());

        Object v1 = toCalendarIfPossible(lo, criteria.getTimeZone());
        Object v2 = toCalendarIfPossible(hi, criteria.getTimeZone());
        if (v1 instanceof CharSequence && v2 instanceof CharSequence) {
            fragment.append('\'').append(v1).append("' and ").append(propertyName).append(higherOp().getXpath()).append('\'').append(v2).append('\'');
        } else if (v1 instanceof Number && v2 instanceof Number) {
            fragment.append(v1).append(" and ").append(propertyName).append(higherOp().getXpath()).append(v2);
        } else if (v1 instanceof Calendar && v2  instanceof Calendar) {
            Calendar cal1 = (Calendar) v1;
            Calendar cal2 = (Calendar) v2;
            toXPathString(fragment, cal1, cal2);
        } else {
            throw new IllegalArgumentException("values provided are not of the accepted types String, Number, Calendar (they are  " + lo.getClass() + ", " + hi.getClass() + ")");
        }
        fragment.append(")");
        log.debug("xpathString is {} ", fragment);
        return fragment.toString();
    }

    protected void toXPathString(StringBuilder fragment, Calendar cal1, Calendar cal2) {
         fragment.append(XS_DATETIME_FUNCTION + "('").append(XPathTextUtils.toXsdDate(cal1)).append("')  and ").append(propertyName).append(higherOp().getXpath()).append(XS_DATETIME_FUNCTION).append("('").append(XPathTextUtils.toXsdDate(cal2)).append("') ");
    }

    @Override
    public Condition toSQLCondition(Criteria criteria) {
        return new AndCondition(
            SimpleExpressionCondition.of(Field.of(propertyName), lowerOp(), lo, criteria.getTimeZone()),
            SimpleExpressionCondition.of(Field.of(propertyName), higherOp(), hi, criteria.getTimeZone())
        );
    }

    protected Op lowerOp() {
        return lowerInclusive ? Op.GE : Op.GT;
    }
    protected Op higherOp() {
        return higherInclusive ? Op.LE : Op.LT;
    }


}
