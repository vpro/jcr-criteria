/**
 *
 * Criteria API for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlcriteria.html)
 * Copyright(C) 2009-2011, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion;

import java.util.Calendar;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRQueryException;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.xpath.utils.XPathTextUtils;

import org.apache.commons.lang.StringUtils;


/**
 * superclass for "simple" comparisons (with XPATH binary operators)
 * @author Federico Grilli
 * @version $Id$
 */
public class SimpleExpression extends BaseCriterion implements Criterion
{

    private static final long serialVersionUID = -1104419394978535803L;

    private final String propertyName;

    private final Object value;

    private final String op;

    protected SimpleExpression(String propertyName, Object value, String op)
    {
        this.propertyName = propertyName;
        this.value = value;
        this.op = op;
    }

    @Override
    public String toString()
    {
        return propertyName + getOp() + value;
    }

    protected final String getOp()
    {
        return op;
    }

    public String toXPathString(Criteria criteria) throws JCRQueryException
    {
        StringBuilder fragment = new StringBuilder();
        fragment.append(" (");

        if (value instanceof String)
        {
            fragment.append(propertyName).append(getOp());
            // Generally, if you enclose values in single quotes, you just need to replace any literal single quote
            // character with '' (two consecutive single quote characters).
            String escValue = StringUtils.replace((String) value, "'", "''");
            fragment.append("'" + escValue + "') ");
        }
        else if (value instanceof Number)
        {
            fragment.append(propertyName).append(getOp());
            fragment.append(value + ") ");
        }
        else if (value instanceof Character)
        {
            fragment.append(propertyName).append(getOp());
            fragment.append("'" + Character.toString((Character) value) + "') ");
        }
        else if (value instanceof Boolean)
        {
            if ((Boolean) value)
            {
                fragment.append(propertyName).append(getOp());
                fragment.append(value + ") ");
            }
            else
            {
                // false should also match a missing boolean property
                fragment.append("(");
                fragment.append(propertyName).append(getOp());

                fragment.append(value + ") or not(").append(propertyName + " ))");
            }
        }
        else if (value instanceof Calendar)
        {
            fragment.append(propertyName).append(getOp());
            Calendar cal = (Calendar) value;

            fragment.append(XS_DATETIME_FUNCTION + "('" + XPathTextUtils.toXsdDate(cal) + "')) ");
        }
        else if (value != null)
        {
            fragment.append(propertyName).append(getOp());

            // just use the toString() of the given object
            fragment.append("'" + value + "') ");
        }
        log.debug("xpathString is {} ", fragment);
        return fragment.toString();
    }
}
