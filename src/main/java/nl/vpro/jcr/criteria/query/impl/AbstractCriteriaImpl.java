/*
 * Criteria API for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlcriteria.html)
 * Copyright(C) 2009-2013, Openmind S.r.l. http://www.openmindonline.it
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package nl.vpro.jcr.criteria.query.impl;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

import javax.jcr.Session;
import javax.jcr.query.Query;

import nl.vpro.jcr.criteria.advanced.impl.AdvancedCriteriaImpl;
import nl.vpro.jcr.criteria.advanced.impl.AdvancedResultImpl;
import nl.vpro.jcr.criteria.advanced.impl.QueryExecutorHelper;
import nl.vpro.jcr.criteria.query.AdvancedResult;
import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.JCRCriteriaFactory;
import nl.vpro.jcr.criteria.query.TranslatableCriteria;
import nl.vpro.jcr.criteria.query.criterion.Criterion;
import nl.vpro.jcr.criteria.query.criterion.Junction;
import nl.vpro.jcr.criteria.query.criterion.Order;
import nl.vpro.jcr.criteria.query.sql2.Select;
import nl.vpro.jcr.criteria.query.xpath.JCRMagnoliaCriteriaQueryTranslator;
import nl.vpro.jcr.criteria.query.xpath.XPathSelect;
import nl.vpro.jcr.criteria.query.xpath.utils.XPathTextUtils;

/**
 * A generic Criteria implementation.
 *
 * @author fgrilli
 * @author Michiel Meeuwissen
 */
@Slf4j
@EqualsAndHashCode(doNotUseGetters = true)
@AllArgsConstructor
public abstract class AbstractCriteriaImpl implements TranslatableCriteria {

    @Getter
    protected String basePath = Criterion.ALL_ELEMENTS;

    @Getter
    @Setter
    protected String type;

    protected List<CriterionEntry> criterionEntries = new ArrayList<>();

    protected List<OrderEntry> orderEntries = new ArrayList<>();

    @Getter
    protected int maxResults;

    protected int offset;

    protected String spellCheckString;

    protected boolean forcePagingWithDocumentOrder;

    /**
     * Language to produce queries in (Supported are {@link Query#XPATH}, {@link Query#JCR_SQL2} and <code>null</code>, which means to prefer SQL2, but fall back to XPATH)
     */
    @Getter
    @Setter
    protected String language = null;

    protected AbstractCriteriaImpl() {
    }

    @Override
    public Collection<CriterionEntry> getCriterionEntries() {
        return Collections.unmodifiableCollection(criterionEntries);
    }

    @Override
    public Collection<OrderEntry> getOrderEntries() {
        return Collections.unmodifiableCollection(orderEntries);
    }


    @Override
    public Criteria add(Criterion criterion) {
        if (criterion instanceof Junction) {
            ((Junction) criterion).setOuter(false);
        }
        criterionEntries.add(new CriterionEntry(criterion, this));
        return this;
    }

    @Override
    public Criteria addOrder(Order order) {
        orderEntries.add(new OrderEntry(order, this));
        return this;
    }

    @Override
    public Criteria addOrderByScore() {
        orderEntries.add(new OrderEntry(Order.desc("@jcr:score"), this));
        return this;
    }

    @Override
    public Criteria setBasePath(String path) {
        if (!XPathTextUtils.isValidNodePath(path)) {
            throw new IllegalArgumentException("Path " + path + " is not a valid node path");
        }
        this.basePath = path;
        return this;
    }


    /**
     * Returns the firstResult.
     * @return the firstResult
     */
    public int getFirstResult() {
        return offset;
    }

    @Override
    public Criteria setFirstResult(int firstResult) {
        this.offset = firstResult;
        return this;
    }


    @Override
    public Criteria setMaxResults(int maxResults) {
        this.maxResults = maxResults;
        return this;
    }

    @Override
    public Criteria setPaging(int itemsPerPage, int pageNumberStartingFromOne) {
        this.maxResults = itemsPerPage;
        this.offset = (Math.max(pageNumberStartingFromOne, 1) - 1) * maxResults;
        return this;
    }

    @Override
    public Criteria setSpellCheckString(String spellCheckString) {
        this.spellCheckString = spellCheckString;
        return this;
    }

    @Override
    public Criteria setForcePagingWithDocumentOrder(boolean force) {
        this.forcePagingWithDocumentOrder = force;
        return this;
    }

    @Override
    @Deprecated
    public Expression toXpathExpression() {
        JCRMagnoliaCriteriaQueryTranslator translator = new JCRMagnoliaCriteriaQueryTranslator(this);
        XPathSelect statement = new XPathSelect();
        statement.setType(type);
        if (! Criterion.ALL_ELEMENTS.equals(basePath) && basePath != null) {
            statement.setRoot(XPathTextUtils.toXPath(basePath));
        }
        statement.setPredicate(translator.getPredicate());
        statement.setOrderByClause(translator.getOrderBy());
        return Expression.xpath(statement.toStatementString());
    }



    @Override
    public Expression toSql2Expression() {
        return Expression.sql2(
            Select.from(this).toSql2());
    }




    @Override
    public AdvancedResult execute(Session session) {
        Expression expr = toExpression(language);
        return QueryExecutorHelper.execute(
            expr,
            getCountSupplier(session),
            session,
            maxResults,
            offset,
            spellCheckString,
            forcePagingWithDocumentOrder && this.orderEntries.isEmpty());
    }

    @Override
    public LongSupplier getCountSupplier(Session session) {
        return () -> {
            long startTime = System.nanoTime();
            try {
                AdvancedCriteriaImpl countCriteria = JCRCriteriaFactory.createCriteria();
                for (CriterionEntry c : getCriterionEntries()) {
                    countCriteria.add(c.getCriterion());
                }
                countCriteria.setBasePath(basePath);
                countCriteria.setType(type);
                countCriteria.setSpellCheckString(spellCheckString);

                Expression expr = countCriteria.toExpression(language);
                final AdvancedResultImpl result = QueryExecutorHelper.execute(
                    expr,
                    () -> -1,
                    session,
                    0,
                    0,
                    spellCheckString,
                    forcePagingWithDocumentOrder && this.orderEntries.isEmpty());

                long totalSize = result.getTotalSize();
                return totalSize;
            } finally {
                long duration = TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
                if (duration > 50) {
                    log.info("Total size was not available, determining it took {} ms", duration);
                } else {
                    log.debug("Total size was not available, determining it took {} ms", duration);
                }
            }
        };
    }


    @Override
    public String toString() {
        return "criteria" + (type != null ? " " + type : "") + "" + criterionEntries + (orderEntries.isEmpty() ? "" : " order by " + orderEntries);
    }

}
