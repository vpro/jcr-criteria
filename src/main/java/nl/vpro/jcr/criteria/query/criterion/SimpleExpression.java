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

import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.JCRQueryException;
import nl.vpro.jcr.criteria.query.sql2.Condition;
import nl.vpro.jcr.criteria.query.sql2.Field;
import nl.vpro.jcr.criteria.query.sql2.SimpleExpressionCondition;
import nl.vpro.jcr.criteria.query.xpath.utils.XPathTextUtils;

/**
 * superclass for "simple" comparisons (with XPATH binary operators)
 * @author Federico Grilli
 */
public class SimpleExpression extends BaseCriterion implements Criterion {

    private static final long serialVersionUID = -1104419394978535803L;

    private final String propertyName;

    private final Object value;

    private final Op op;

    protected SimpleExpression(String propertyName, Object value, Op op) {
        this.propertyName = propertyName;
        this.value = value;
        this.op = op;
        if (this.op.getXpath() == null) {
            throw new IllegalArgumentException("" + op + " cannot be used for " + this);
        }
    }

    @Override
    public String toString() {
        return propertyName + getOp() + value;
    }

    protected final Op getOp() {
        return op;
    }

    @Override
    public String toXPathString(Criteria criteria) throws JCRQueryException {
        StringBuilder fragment = new StringBuilder();
        if (value instanceof String) {
            fragment.append(propertyName).append(getOp());
            // Generally, if you enclose values in single quotes, you just need to replace any literal single quote
            // character with '' (two consecutive single quote characters).
            String escValue = StringUtils.replace((String) value, "'", "''");
            fragment.append("'")
                .append(escValue);
        } else if (value instanceof Number) {
            fragment.append(propertyName).append(getOp());
            fragment.append(value);
        } else if (value instanceof Character) {
            fragment.append(propertyName).append(getOp());
            fragment.append("'").append(value).append("'");
        } else if (value instanceof Boolean) {
            boolean boolValue = (boolean) value;
            switch(getOp()) {
                case ne:
                    boolValue = ! boolValue;
                    break;
                case eq:
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            if (boolValue) {
                fragment.append(propertyName)
                    .append("='true'");
            } else {
                fragment.append(propertyName)
                    .append("='false'");
            }
        } else if (value instanceof Calendar) {
            fragment.append(propertyName).append(getOp());
            Calendar cal = (Calendar) value;
            fragment.append(XS_DATETIME_FUNCTION + "('").append(XPathTextUtils.toXsdDate(cal)).append("')'");
        } else if (value != null) {
            fragment.append(propertyName).append(getOp());
            // just use the toString() of the given object
            fragment.append("'").append(value);
        }
        log.debug("xpathString is {} ", fragment);
        return fragment.toString();
    }

    @Override
    public Condition toSQLCondition(Criteria criteria) throws JCRQueryException {
        return SimpleExpressionCondition.of(Field.of(propertyName), op, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleExpression that = (SimpleExpression) o;

        if (propertyName != null ? !propertyName.equals(that.propertyName) : that.propertyName != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        return op != null ? op.equals(that.op) : that.op == null;
    }

    @Override
    public int hashCode() {
        int result = propertyName != null ? propertyName.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (op != null ? op.hashCode() : 0);
        return result;
    }
}
