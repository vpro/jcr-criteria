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

import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.JCRQueryException;
import nl.vpro.jcr.criteria.query.sql2.Condition;
import nl.vpro.jcr.criteria.query.sql2.LikeCondition;


/**
 * @author fgrilli
 */
public class LikeExpression extends JCRFunctionExpression {

    private static final long serialVersionUID = 1810624472706401714L;

    private MatchMode matchMode;

    public LikeExpression(String propertyName, Object value, String function, MatchMode matchMode) {
        super(propertyName, value, function);
        if (matchMode == null) {
            throw new IllegalArgumentException("MatchMode can't be null");
        }
        this.matchMode = matchMode;
    }

    @Override
    public String toXPathString(Criteria criteria) throws JCRQueryException {
        StringBuilder fragment = new StringBuilder();
        fragment.append(" (").append(function).append("(");
        fragment.append(propertyName);
        fragment.append(", '").append(matchMode.toMatchString(value.toString())).append("') ) ");
        log.debug("xpathString is {} ", fragment);
        return fragment.toString();
    }

    @Override
    public Condition toSQLCondition(Criteria criteria) {
        return new LikeCondition(propertyName, matchMode.toMatchString(value.toString()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LikeExpression that = (LikeExpression) o;

        return matchMode != null ? matchMode.equals(that.matchMode) : that.matchMode == null;

    }

    @Override
    public int hashCode() {
        return matchMode != null ? matchMode.hashCode() : 0;
    }
}
