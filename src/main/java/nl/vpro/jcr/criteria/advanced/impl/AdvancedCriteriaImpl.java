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

package nl.vpro.jcr.criteria.advanced.impl;

import lombok.EqualsAndHashCode;
import lombok.Singular;

import java.util.List;
import java.util.stream.Collectors;

import nl.vpro.jcr.criteria.query.criterion.Criterion;
import nl.vpro.jcr.criteria.query.criterion.Order;
import nl.vpro.jcr.criteria.query.impl.AbstractCriteriaImpl;

import static javax.jcr.nodetype.NodeType.NT_UNSTRUCTURED;


/**
 * @author fgiust
 * @author Michiel Meeuwissen
 */

@EqualsAndHashCode(callSuper = true)
public class AdvancedCriteriaImpl extends AbstractCriteriaImpl  {


    public AdvancedCriteriaImpl() {
        super();
    }

    @lombok.Builder(builderClassName = "Builder")
    private AdvancedCriteriaImpl(
        String basePath,
        String type,
        @Singular
        List<Criterion> criterions,
        @Singular
        List<Order> orders,
        int maxResults,
        int offset,
        String spellCheckString,
        boolean forcePagingWithDocumentOrder,
        String language) {
        super(
            basePath == null ? Criterion.ALL_ELEMENTS : basePath,
            type,
            null, null,
            maxResults, offset, spellCheckString, forcePagingWithDocumentOrder, language);
        this.criterionEntries = criterions.stream().map(c -> new CriterionEntry(c, this)).collect(Collectors.toList());
        this.orderEntries = orders.stream().map(o -> new OrderEntry(o, this)).collect(Collectors.toList());

    }

    /**
     * @since 2.0
     */
    public static class Builder {

        public Builder add(Criterion criterion) {
            return criterion(criterion);
        }
        public Builder add(Order order) {
            return order(order);
        }

        public Builder fromUnstructured() {
            return type(NT_UNSTRUCTURED);
        }

        public Builder asc(String field){
            return order(Order.asc(field));
        }
        public Builder desc(String field){
            return order(Order.desc(field));
        }


    }
}
