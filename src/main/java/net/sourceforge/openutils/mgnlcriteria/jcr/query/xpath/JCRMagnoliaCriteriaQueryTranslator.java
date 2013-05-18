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

package net.sourceforge.openutils.mgnlcriteria.jcr.query.xpath;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRQueryException;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.TranslatableCriteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.TranslatableCriteria.CriterionEntry;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.TranslatableCriteria.OrderEntry;

import org.apache.commons.lang.StringUtils;


/**
 * @author Federico Grilli
 * @version $Id$
 */
public class JCRMagnoliaCriteriaQueryTranslator
{

    private final TranslatableCriteria criteria;

    public JCRMagnoliaCriteriaQueryTranslator(final TranslatableCriteria criteria) throws JCRQueryException
    {

        this.criteria = criteria;
    }

    public Criteria getRootCriteria()
    {
        return criteria;
    }

    public String getPredicate()
    {
        StringBuilder condition = new StringBuilder(30);

        boolean isfirst = true;

        for (CriterionEntry entry : criteria.getCriterionEntries())
        {
            String xpathString = entry.getCriterion().toXPathString(entry.getCriteria());

            if (StringUtils.isNotBlank(xpathString))
            {
                if (!isfirst && StringUtils.isNotBlank(xpathString))
                {
                    condition.append(" and ");
                }

                condition.append(xpathString);
                isfirst = false;
            }
        }

        return condition.toString();
    }

    public String getOrderBy()
    {
        StringBuilder orderBy = new StringBuilder(30);

        for (OrderEntry oe : criteria.getOrderEntries())
        {
            if (orderBy.length() > 0)
            {
                orderBy.append(", ");
            }
            orderBy.append(oe.getOrder().toXPathString(oe.getCriteria()));
        }

        return orderBy.toString();
    }
}