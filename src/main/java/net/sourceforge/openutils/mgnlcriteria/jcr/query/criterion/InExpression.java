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

import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRQueryException;


/**
 * @author fgrilli
 * @version $Id$
 */
public class InExpression implements Criterion
{

    private static final long serialVersionUID = -8445602953808764036L;

    private String nodeName;

    private String[] values;

    public InExpression(String nodeName, String[] values)
    {
        this.nodeName = nodeName;
        this.values = values;
    }

    public String toXPathString(Criteria criteria) throws JCRQueryException
    {
        StringBuilder inClause = new StringBuilder("( ");

        for (int i = 0; i < values.length; i++)
        {
            String containsPredicate = Restrictions.contains(nodeName, values[i]).toXPathString(criteria);
            inClause.append(containsPredicate);
            // if this is not the last value, append an 'or'
            if ((i + 1) != values.length)
            {
                inClause.append(" or ");
            }
        }
        inClause.append(") ");
        return inClause.toString();
    }
}
