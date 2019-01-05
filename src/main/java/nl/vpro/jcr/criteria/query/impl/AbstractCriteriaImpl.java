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

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

import javax.jcr.Session;
import javax.jcr.query.Query;

import nl.vpro.jcr.criteria.advanced.impl.AdvancedResultImpl;
import nl.vpro.jcr.criteria.advanced.impl.QueryExecutorHelper;
import nl.vpro.jcr.criteria.query.AdvancedResult;
import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.JCRCriteriaFactory;
import nl.vpro.jcr.criteria.query.TranslatableCriteria;
import nl.vpro.jcr.criteria.query.criterion.Criterion;
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
@AllArgsConstructor
public abstract class AbstractCriteriaImpl implements TranslatableCriteria {

    protected String path = Criterion.ALL_ELEMENTS;

    protected List<CriterionEntry> criterionEntries = new ArrayList<>();

    @Singular
    protected List<OrderEntry> orderEntries = new ArrayList<>();

    protected int maxResults;

    protected int offset;

    protected String spellCheckString;

    protected boolean forcePagingWithDocumentOrder;

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
        this.path = path;
        return this;
    }

    @Override
    public String getBasePath() {
        return path;
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

    /**
     * Returns the maxResults.
     * @return the maxResults
     */
    public int getMaxResults() {
        return maxResults;
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
        statement.setRoot(XPathTextUtils.toXPath(path));
        statement.setPredicate(translator.getPredicate());
        statement.setOrderByClause(translator.getOrderBy());
        return new Expression(Query.XPATH, statement.toStatementString());
    }



    @Override
    public Expression toSql2Expression() {
        return new Expression(Query.JCR_SQL2, Select.from(this).toSql2());
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
                Criteria countCriteria = JCRCriteriaFactory.createCriteria();
                for (CriterionEntry c : getCriterionEntries()) {
                    countCriteria.add(c.getCriterion());
                }
                countCriteria.setBasePath(path);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractCriteriaImpl that = (AbstractCriteriaImpl) o;

        if (maxResults != that.maxResults) return false;
        if (offset != that.offset) return false;
        if (forcePagingWithDocumentOrder != that.forcePagingWithDocumentOrder) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (criterionEntries != null ? !criterionEntries.equals(that.criterionEntries) : that.criterionEntries != null)
            return false;
        if (orderEntries != null ? !orderEntries.equals(that.orderEntries) : that.orderEntries != null) return false;
        return spellCheckString != null ? spellCheckString.equals(that.spellCheckString) : that.spellCheckString == null;

    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (criterionEntries != null ? criterionEntries.hashCode() : 0);
        result = 31 * result + (orderEntries != null ? orderEntries.hashCode() : 0);
        result = 31 * result + maxResults;
        result = 31 * result + offset;
        result = 31 * result + (spellCheckString != null ? spellCheckString.hashCode() : 0);
        result = 31 * result + (forcePagingWithDocumentOrder ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "criteria" + criterionEntries + (orderEntries.isEmpty() ? "" : " order by " + orderEntries);
    }

}
