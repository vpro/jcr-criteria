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

import java.time.ZoneId;
import java.util.Optional;
import java.util.function.LongSupplier;

import javax.jcr.Node;
import javax.jcr.Session;

/**
 * @author fgiust
 */
public interface ExecutableQuery {

    /**
     * Executes the query
     * @return the search result
     */
    AdvancedResult execute(Session session);

    /**
     * @since 2.0
     */
    default Optional<Node> findFirst(Session session) {
        AdvancedResult result = execute(session);
        ResultIterator<AdvancedResultItem> items = result.getItems();
        if (items.hasNext()) {
            return Optional.of(items.next());
        }
        return Optional.empty();
    }


    LongSupplier getCountSupplier(Session session);

    default ZoneId getTimeZone() {
        return ZoneId.systemDefault();
    }


}
