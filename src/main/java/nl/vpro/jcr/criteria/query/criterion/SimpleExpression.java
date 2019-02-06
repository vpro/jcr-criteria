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

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.JCRQueryException;
import nl.vpro.jcr.criteria.query.sql2.Condition;
import nl.vpro.jcr.criteria.query.sql2.Field;
import nl.vpro.jcr.criteria.query.sql2.NotCondition;
import nl.vpro.jcr.criteria.query.sql2.SimpleExpressionCondition;
import nl.vpro.jcr.criteria.query.xpath.utils.XPathTextUtils;
import nl.vpro.jcr.utils.Utils;

/**
 * superclass for "simple" comparisons (with XPATH binary operators)
 * @author Federico Grilli
 */
@EqualsAndHashCode(callSuper = true)
public class SimpleExpression extends BaseCriterion implements Criterion {

    private static final long serialVersionUID = -1104419394978535803L;

    private final String propertyName;

    private final Object value;

    private final Op op;

    protected SimpleExpression(@Nonnull String propertyName, @Nonnull Object value, @Nonnull Op op) {
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
        Object v = Utils.toCalendarIfPossible(value, criteria.getTimeZone());
        if (v instanceof CharSequence) {
            fragment.append(propertyName).append(getOp());
            // Generally, if you enclose values in single quotes, you just need to replace any literal single quote
            // character with '' (two consecutive single quote characters).
            String escValue = StringUtils.replace((String) v, "'", "''");
            fragment.append("'")
                .append(escValue)
                .append("'");
        } else if (v instanceof Number) {
            fragment.append(propertyName).append(getOp());
            fragment.append(v);
        } else if (v instanceof Character) {
            fragment.append(propertyName).append(getOp());
            fragment.append("'").append(v).append("'");
        } else if (v instanceof Boolean) {
            fragment.append(propertyName).append(getOp());
            fragment.append("'").append(v).append("'");
            if (getOp() == Op.NE) {
                fragment.append("or not(")
                    .append(propertyName)
                    .append(")");
            }
        } else if (v instanceof Calendar) {
            fragment.append(propertyName)
                .append(getOp());
            Calendar cal = (Calendar) v;
            fragment.append(XS_DATETIME_FUNCTION + "('").append(XPathTextUtils.toXsdDate(cal)).append("')");
        } else if (v != null) {
            fragment.append(propertyName).append(getOp());
            // just use the toString() of the given object
            fragment.append("'").append(v).append("'");
        }
        log.debug("xpathString is {} ", fragment);
        return fragment.toString();
    }

    @Override
    public Condition toSQLCondition(Criteria criteria) throws JCRQueryException {
        if (op == Op.NE) {
            return new NotCondition(SimpleExpressionCondition.of(Field.of(propertyName), Op.EQ, value, criteria.getTimeZone()));
        } else {
            return SimpleExpressionCondition.of(Field.of(propertyName), op, value, criteria.getTimeZone());
        }
    }

}
