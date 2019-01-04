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

import java.util.function.LongSupplier;

import javax.jcr.Session;
import javax.jcr.query.Query;

/**
 * @author fgiust
 */
public interface ExecutableQuery {

    /**
     * Executes the query
     * @return the search result
     */
    default AdvancedResult execute(Session session) {
        return execute(session, Query.XPATH);
        //return execute(session, Query.SQL2); // as soon as sufficiently supported
    }

    AdvancedResult execute(Session session, String language);

	default LongSupplier getCountSupplier(Session session) {
        return getCountSupplier(session, Query.XPATH);
    }

    LongSupplier getCountSupplier(Session session, String language);


}
