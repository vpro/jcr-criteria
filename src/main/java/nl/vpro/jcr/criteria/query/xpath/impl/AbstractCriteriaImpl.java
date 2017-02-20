/*
 * Criteria API for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlcriteria.html)
 * Copyright(C) 2009-2013, Openmind S.r.l. http://www.openmindonline.it
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package nl.vpro.jcr.criteria.query.xpath.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.IntSupplier;

import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.vpro.jcr.criteria.advanced.impl.AdvancedResultImpl;
import nl.vpro.jcr.criteria.advanced.impl.QueryExecutorHelper;
import nl.vpro.jcr.criteria.query.AdvancedResult;
import nl.vpro.jcr.criteria.query.Criteria;
import nl.vpro.jcr.criteria.query.JCRCriteriaFactory;
import nl.vpro.jcr.criteria.query.TranslatableCriteria;
import nl.vpro.jcr.criteria.query.criterion.Criterion;
import nl.vpro.jcr.criteria.query.criterion.Order;
import nl.vpro.jcr.criteria.query.xpath.JCRMagnoliaCriteriaQueryTranslator;
import nl.vpro.jcr.criteria.query.xpath.XPathSelect;
import nl.vpro.jcr.criteria.query.xpath.utils.XPathTextUtils;

/**
 * A generic Criteria implementation.
 *
 * @author fgrilli
 * @author Michiel Meeuwissen
 */
public abstract class AbstractCriteriaImpl implements TranslatableCriteria {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected String path = Criterion.ALL_ELEMENTS;

    protected List<CriterionEntry> criterionEntries = new ArrayList<>();

    protected List<OrderEntry> orderEntries = new ArrayList<>();

    protected int maxResults;

    protected int offset;

    protected String spellCheckString;

    protected boolean forcePagingWithDocumentOrder;

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
    public String toXpathExpression() {
        JCRMagnoliaCriteriaQueryTranslator translator = new JCRMagnoliaCriteriaQueryTranslator(this);
        XPathSelect statement = new XPathSelect();
        statement.setRoot(XPathTextUtils.toXPath(path));
        statement.setPredicate(translator.getPredicate());
        statement.setOrderByClause(translator.getOrderBy());
        return statement.toStatementString();
    }

    @Override
    public AdvancedResult execute(Session session) {
        @SuppressWarnings("deprecation")
        String language = javax.jcr.query.Query.XPATH;
        String stmt = toXpathExpression();
        return QueryExecutorHelper.execute(
            stmt,
            language,
            getCountSupplier(session),
            session,
            maxResults,
            offset,
            spellCheckString,
            forcePagingWithDocumentOrder && this.orderEntries.isEmpty());
    }

    @Override
    @SuppressWarnings("deprecation")
    public IntSupplier getCountSupplier(Session session) {
        return () -> {
            long startTime = System.nanoTime();
            try {
                Criteria countCriteria = JCRCriteriaFactory.createCriteria();
                for (CriterionEntry c : getCriterionEntries()) {
                    countCriteria.add(c.getCriterion());
                }
                countCriteria.setBasePath(path);
                countCriteria.setSpellCheckString(spellCheckString);

                String stmt = countCriteria.toXpathExpression();
                final AdvancedResultImpl result = QueryExecutorHelper.execute(
                    stmt,
                    javax.jcr.query.Query.XPATH,
                    () -> -1,
                    session,
                    0,
                    0,
                    spellCheckString,
                    forcePagingWithDocumentOrder && this.orderEntries.isEmpty());

                return result.getTotalSize();
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
