/**
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

import java.util.Calendar;

import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.JCRQueryException;
import nl.vpro.jcr.criteria.query.xpath.utils.XPathTextUtils;


/**
 * @author fgrilli
 * @version $Id$
 */
public class BetweenExpression extends BaseCriterion implements Criterion {

    private static final long serialVersionUID = 6686395240415024541L;

    private final String propertyName;

    private final Object lo;

    private final Object hi;

    protected BetweenExpression(String propertyName, Object lo, Object hi) {
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

        if (lo instanceof String && hi instanceof String) {
            fragment.append("'").append(lo).append("' and ").append(propertyName).append(" <= '").append(hi).append("'");
        } else if (lo instanceof Number && hi instanceof Number) {
            fragment.append(lo).append(" and ").append(propertyName).append(" <= ").append(hi);
        } else if (lo instanceof Calendar && hi instanceof Calendar) {
            Calendar cal = (Calendar) lo;
            Calendar cal2 = (Calendar) hi;

            fragment.append(XS_DATETIME_FUNCTION + "('").append(XPathTextUtils.toXsdDate(cal)).append("')  and ").append(propertyName).append(" <= ").append(XS_DATETIME_FUNCTION).append("('").append(XPathTextUtils.toXsdDate(cal2)).append("') ");
        } else {
            String msg = "values provided are not of the accepted types String, Number, Calendar";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        fragment.append(")");
        log.debug("xpathString is {} ", fragment);
        return fragment.toString();
    }
}
