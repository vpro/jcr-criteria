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

package net.sourceforge.openutils.mgnlcriteria.jcr.query;

import javax.jcr.Session;

import net.sourceforge.openutils.mgnlcriteria.advanced.impl.AdvancedCriteriaImpl;


/**
 * Factory for criteria queries. Since mgnlcriteria 2.x always use the simple createCriteria() factory method.
 * @author fgrilli
 * @author fgiust
 * @author diego_schivo
 * @version $Id$
 */
public final class JCRCriteriaFactory
{

    private JCRCriteriaFactory()
    {
    }

    /**
     * Creates a query criteria for dynamic query composition. All the details can be set on the Criteria instance
     * returned.
     * @return an AdvancedCriteriaImpl
     */
    public static Criteria createCriteria()
    {
        return new AdvancedCriteriaImpl();
    }

    /**
     * Creates a jcr query using the specified statement.
     * @param hm the HierarchyManager
     * @param query the statement of the query
     * @param language the language of the query
     * @return a DirectJcrQuery
     */
    public static DirectJcrQuery createDirectJcrQuery(Session session, String query, String language)
    {
        return new DirectJcrQuery(session, query, language);
    }

}
