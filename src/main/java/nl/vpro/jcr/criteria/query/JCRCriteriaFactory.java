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

package nl.vpro.jcr.criteria.query;

import nl.vpro.jcr.criteria.advanced.impl.AdvancedCriteriaImpl;


/**
 * Factory for criteria queries. Since mgnlcriteria 2.x always use the simple createCriteria() factory method.
 * @author fgrilli
 * @author fgiust
 * @author diego_schivo
 * @author Michiel Meeuwissen
 */
public final class JCRCriteriaFactory {

    private JCRCriteriaFactory() {
    }

    /**
     * Creates a query criteria for dynamic query composition. All the details can be set on the Criteria instance
     * returned.
     * @return an AdvancedCriteriaImpl
     */
    public static AdvancedCriteriaImpl createCriteria() {
        return new AdvancedCriteriaImpl();
    }


    /**
     * @since 2.0
     */
    public static AdvancedCriteriaImpl.Builder builder() {
        return AdvancedCriteriaImpl.builder();
    }

}
