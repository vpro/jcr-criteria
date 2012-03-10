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

import java.io.Serializable;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRQueryException;


/**
 * An object-oriented representation of a query criterion that may be used as a restriction in a <tt>Criteria</tt>
 * query. Built-in criterion types are provided by the <tt>Restrictions</tt> factory class. This interface might be
 * implemented by application classes that define custom restriction criteria.
 * @see Restrictions
 * @see Criteria
 * @author Federico Grilli
 * @version $Id$
 */
public interface Criterion extends Serializable
{

    /**
     * The jcr primary type "@jcr:primaryType".
     */
    String JCR_PRIMARYTYPE = "@jcr:primaryType";

    /**
     * The xpath attribute prefix "@"
     */
    String ATTRIBUTE_SELECTOR = "@";

    /**
     * The jcr prefix "jcr:".
     */
    String JCR_PREFIX = "jcr:";

    /**
     * The jcr root path "/jcr:root".
     */
    String JCR_ROOT = "/jcr:root";

    /**
     * Xpath for all elements "//*".
     */
    String ALL_ELEMENTS = "//*";

    /**
     * XS datetime function "xs:dateTime"
     */
    String XS_DATETIME_FUNCTION = "xs:dateTime";

    /**
     * nt:base item type.
     */
    String NT_BASE = "nt:base";

    /**
     * Render the XPath fragment
     * @param criteria input criteria
     * @return converted XPATH expression
     * @throws JCRQueryException if there is a problem converting the input criteria to a valid xpath expression
     */
    String toXPathString(Criteria criteria) throws JCRQueryException;

}
