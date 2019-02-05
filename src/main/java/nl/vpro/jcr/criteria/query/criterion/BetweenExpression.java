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

import java.util.Calendar;

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

    private final Comparable<?> hi;

    protected BetweenExpression(String propertyName, Comparable<?> lo, Comparable<?> hi) {
        this.propertyName = propertyName;
        this.lo = lo;
        this.hi = hi;
    }

    @Override
    public String toString() {
        return propertyName + " between " + lo + " and " + hi;
    }

    @Override
    public String toXPathString(Criteria criteria) throws JCRQueryException {
        StringBuilder fragment = new StringBuilder();
        fragment.append(" (").append(propertyName).append(" >= ");

        Object v1 = toCalendarIfPossible(lo, criteria.getTimeZone());
        Object v2 = toCalendarIfPossible(hi, criteria.getTimeZone());
        if (v1 instanceof CharSequence && v2 instanceof CharSequence) {
            fragment.append("'").append(v1).append("' and ").append(propertyName).append(" <= '").append(v2).append("'");
        } else if (v1 instanceof Number && v2 instanceof Number) {
            fragment.append(v1).append(" and ").append(propertyName).append(" <= ").append(v2);
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
         fragment.append(XS_DATETIME_FUNCTION + "('").append(XPathTextUtils.toXsdDate(cal1)).append("')  and ").append(propertyName).append(" <= ").append(XS_DATETIME_FUNCTION).append("('").append(XPathTextUtils.toXsdDate(cal2)).append("') ");
    }

    @Override
    public Condition toSQLCondition(Criteria criteria) {
        return new AndCondition(
            SimpleExpressionCondition.of(Field.of(propertyName), Op.GE, lo, criteria.getTimeZone()),
            SimpleExpressionCondition.of(Field.of(propertyName), Op.LE, hi, criteria.getTimeZone())
        );
    }

}
