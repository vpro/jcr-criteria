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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.JCRQueryException;


/**
 * A sequence of a logical expressions combined by some associative logical operator
 * @author Federico Grilli
 * @version $Id$
 */
public class Junction implements Criterion {

    private static final long serialVersionUID = 4745761472724863693L;

    private final List<Criterion> criteria = new ArrayList<>();

    private final String op;

    protected Junction(String op) {
        this.op = op;
    }

    /**
     * Adds a criterion to this Junction
     * @param criterion Criterion to add
     * @return Junction
     */
    public Junction add(Criterion criterion) {
        criteria.add(criterion);
        return this;
    }

    public String getOp() {
        return op;
    }

    @Override
    public String toXPathString(Criteria crit) throws JCRQueryException {

        if (criteria.size() == 0) {
            return "";
        }

        StringBuilder buffer = new StringBuilder().append('(');
        Iterator<Criterion> iter = criteria.iterator();

        boolean isfirst = true;
        while (iter.hasNext()) {
            String xPathString = (iter.next()).toXPathString(crit);
            if (StringUtils.isNotBlank(xPathString)) {
                if (!isfirst && StringUtils.isNotBlank(xPathString)) {
                    buffer.append(' ').append(op).append(" ");
                }
                buffer.append(xPathString);
                isfirst = false;
            }

        }
        return buffer.append(')').toString();
    }

    @Override
    public String toString() {
        return '(' + StringUtils.join(criteria.iterator(), ' ' + op + ' ') + ')';
    }

    /**
     * @return false if this Criterion contains at least one expression, false otherwise
     */
    public boolean isEmpty() {
        return criteria.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Junction junction = (Junction) o;

        if (criteria != null ? !criteria.equals(junction.criteria) : junction.criteria != null) return false;
        return op != null ? op.equals(junction.op) : junction.op == null;

    }

    @Override
    public int hashCode() {
        int result = criteria != null ? criteria.hashCode() : 0;
        result = 31 * result + (op != null ? op.hashCode() : 0);
        return result;
    }
}
