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

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.util.Collection;

import nl.vpro.jcr.criteria.query.criterion.Criterion;
import nl.vpro.jcr.criteria.query.criterion.Order;


/**
 * Exposes the Criterion and Order entries of a Criteria instance.
 * @author fgiust
 */
public interface TranslatableCriteria extends Criteria {

    String getType();

    /**
     * Gets the Order entries of this Criteria instance.
     * @return a collection of Order entries
     */
    Collection<OrderEntry> getOrderEntries();

    /**
     * Gets the Order entries of this Criteria instance.
     * @return a collection of Criterion entries
     */
    Collection<CriterionEntry> getCriterionEntries();


    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    final class CriterionEntry implements Serializable {

        private static final long serialVersionUID = 1L;

        @Getter
        @EqualsAndHashCode.Include
        private final Criterion criterion;

        @Getter
        private final Criteria criteria;

        public CriterionEntry(Criterion criterion, Criteria criteria) {
            this.criteria = criteria;
            this.criterion = criterion;
        }
        @Override
        public String toString() {
            return criterion.toString();
        }
    }

    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    final class OrderEntry implements Serializable {

        private static final long serialVersionUID = 1L;

        @Getter
        @EqualsAndHashCode.Include
        private final Order order;

        @Getter
        private final Criteria criteria;

        public OrderEntry(Order order, Criteria criteria) {
            this.criteria = criteria;
            this.order = order;
        }


        @Override
        public String toString() {
            return order.toString();
        }
    }
}
