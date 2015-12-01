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

package nl.vpro.jcr.criteria.query.xpath.impl;

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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.IntSupplier;


/**
 * A generic Criteria implementation.
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
        // check if the specified path is already an xpath query
        if (StringUtils.contains(path, "*")) {
            this.path = path;
        } else {
            // convert the node handle to a xpath query
            if (StringUtils.isEmpty(StringUtils.remove(path, '/'))) {
                // root node
                this.path = Criterion.ALL_ELEMENTS;
            } else {
                // the handle already starts with a single '/', so prepend another one
                this.path = "/" + StringUtils.defaultString(StringUtils.removeEnd(path, "/")) + "//*";
            }
        }
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
        statement.setRoot(XPathTextUtils.encodeDigitsInPath(this.path));
        statement.setPredicate(translator.getPredicate());
        statement.setOrderByClause(translator.getOrderBy());
        String stmt = statement.toStatementString();
        return stmt;
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


                return countCriteria.execute(session).getTotalSize();
            } finally {
                log.info("Total size was not available, determining it costed {} ms", TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS));
            }
        };
    }

}
