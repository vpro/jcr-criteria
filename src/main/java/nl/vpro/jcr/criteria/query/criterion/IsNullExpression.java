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

import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.JCRQueryException;
import nl.vpro.jcr.criteria.query.sql2.NullCondition;


/**
 * @author fgrilli
 */
@EqualsAndHashCode(callSuper = true)
public class IsNullExpression extends BaseCriterion implements Criterion {

    private static final long serialVersionUID = -1600960388638847909L;

    private final String nodeName;

    public IsNullExpression(String nodeName) {
        this.nodeName = nodeName;
    }

    @Override
    public String toString() {
        return nodeName;
    }

    @Override
    public String toXPathString(Criteria criteria) throws JCRQueryException {
        StringBuilder fragment = new StringBuilder();
        fragment.append("not(").append(nodeName).append(")");
        log.debug("xpathString is {} ", fragment);
        return fragment.toString();
    }

    @Override
    public NullCondition toSQLCondition(Criteria criteria) {
        return new NullCondition(nodeName);
    }

}
