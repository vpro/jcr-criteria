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


/**
 * Superclass of binary logical expressions
 * @author Federico Grilli
  */
public class LogicalExpression extends BaseCriterion implements Criterion {

    private static final long serialVersionUID = 4524284746715983618L;

    private final Criterion lhs;

    private final Criterion rhs;

    private final String op;

    protected LogicalExpression(Criterion lhs, Criterion rhs, String op) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.op = op;
    }

    @Override
    public String toXPathString(Criteria criteria) throws JCRQueryException {

        String fragment = '(' + lhs.toXPathString(criteria) + ' ' + getOp() + ' ' + rhs.toXPathString(criteria) + ')';
        log.debug("xpathString is {} ", fragment);
        return fragment;
    }

    public String getOp() {
        return op;
    }

    @Override
    public String toString() {
        return lhs.toString() + ' ' + getOp() + ' ' + rhs.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogicalExpression that = (LogicalExpression) o;

        if (lhs != null ? !lhs.equals(that.lhs) : that.lhs != null) return false;
        if (rhs != null ? !rhs.equals(that.rhs) : that.rhs != null) return false;
        return op != null ? op.equals(that.op) : that.op == null;
    }

    @Override
    public int hashCode() {
        int result = lhs != null ? lhs.hashCode() : 0;
        result = 31 * result + (rhs != null ? rhs.hashCode() : 0);
        result = 31 * result + (op != null ? op.hashCode() : 0);
        return result;
    }
}
