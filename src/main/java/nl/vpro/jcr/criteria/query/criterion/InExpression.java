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

import java.time.ZoneId;
import java.util.Arrays;

import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.JCRQueryException;
import nl.vpro.jcr.criteria.query.sql2.*;


/**
 * @author fgrilli
 */
@EqualsAndHashCode
public class InExpression implements Criterion  {

    private static final long serialVersionUID = -8445602953808764036L;

    private final String nodeName;

    private final CharSequence [] values;

    private final boolean useContains;

    public InExpression(String nodeName, CharSequence[] values, boolean useContains) {
        this.nodeName = nodeName;
        this.values = values;
        this.useContains = useContains;
    }

    public InExpression(String nodeName, CharSequence[] values) {
        this(nodeName, values, true);
    }

    @Override
    public String toXPathString(Criteria criteria) throws JCRQueryException {
        StringBuilder inClause = new StringBuilder("( ");

        for (int i = 0; i < values.length; i++) {
            String predicate = useContains
                ? Restrictions.contains(nodeName, values[i]).toXPathString(criteria)
                : Restrictions.eq(nodeName, values[i]).toXPathString(criteria);

            inClause.append(predicate);
            // if this is not the last value, append an 'or'
            if ((i + 1) != values.length) {
                inClause.append(" or ");
            }
        }
        inClause.append(") ");
        return inClause.toString();
    }

    @Override
    public Condition toSQLCondition(Criteria criteria) {
        if (values.length == 1) {
            return toSQLCondition(values[0], criteria.getTimeZone());
        } else {
            OrCondition orCondition = new OrCondition();
            for (CharSequence cs : values) {
                orCondition.getClauses().add(toSQLCondition(cs, criteria.getTimeZone()));
            }
            return orCondition;
        }

    }

    protected Condition toSQLCondition(CharSequence cs, ZoneId zoneId) {
        if (useContains) {
            return new ContainsCondition(nodeName, cs.toString());
        } else {
            return SimpleExpressionCondition.of(Field.of(nodeName), Op.EQ, cs, zoneId);
        }
    }





    @Override
    public String toString() {
        return nodeName + "in " + Arrays.asList(values);
    }
}
