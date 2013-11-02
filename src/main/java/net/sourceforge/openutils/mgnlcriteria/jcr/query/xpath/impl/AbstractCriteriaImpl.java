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

package net.sourceforge.openutils.mgnlcriteria.jcr.query.xpath.impl;

import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.RuntimeRepositoryException;
import info.magnolia.repository.RepositoryConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlcriteria.advanced.impl.QueryExecutorHelper;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.TranslatableCriteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Criterion;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Order;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.xpath.JCRMagnoliaCriteriaQueryTranslator;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.xpath.XPathSelect;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.xpath.utils.XPathTextUtils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A generic Criteria implementation.
 * @author fgrilli
 * @version $Id$
 */
public abstract class AbstractCriteriaImpl implements TranslatableCriteria
{

    protected Logger log = LoggerFactory.getLogger(getClass());

    protected String path = Criterion.ALL_ELEMENTS;

    protected Class< ? > classType;

    protected List<CriterionEntry> criterionEntries = new ArrayList<CriterionEntry>();

    protected List<OrderEntry> orderEntries = new ArrayList<OrderEntry>();

    protected int maxResults;

    protected int offset;

    protected String spellCheckString;

    protected String workspace = RepositoryConstants.WEBSITE;

    protected boolean forcePagingWithDocumentOrder;

    protected AbstractCriteriaImpl()
    {

    }

    /**
     * {@inheritDoc}
     */
    public Collection<CriterionEntry> getCriterionEntries()
    {
        return Collections.unmodifiableCollection(criterionEntries);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<OrderEntry> getOrderEntries()
    {
        return Collections.unmodifiableCollection(orderEntries);
    }

    /**
     * {@inheritDoc}
     */
    public Criteria add(Criterion criterion)
    {
        criterionEntries.add(new CriterionEntry(criterion, this));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Criteria addOrder(Order order)
    {
        orderEntries.add(new OrderEntry(order, this));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Criteria addOrderByScore()
    {
        orderEntries.add(new OrderEntry(Order.desc("@jcr:score"), this));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Criteria setBasePath(String path)
    {
        // check if the specified path is already an xpath query
        if (StringUtils.contains(path, "*"))
        {
            this.path = path;
        }
        else
        {
            // convert the node handle to a xpath query
            if (StringUtils.isEmpty(StringUtils.remove(path, '/')))
            {
                // root node
                this.path = Criterion.ALL_ELEMENTS;
            }
            else
            {
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
    public int getFirstResult()
    {
        return offset;
    }

    /**
     * {@inheritDoc}
     */
    public Criteria setFirstResult(int firstResult)
    {
        this.offset = firstResult;
        return this;
    }

    /**
     * Returns the maxResults.
     * @return the maxResults
     */
    public int getMaxResults()
    {
        return maxResults;
    }

    /**
     * {@inheritDoc}
     */
    public Criteria setMaxResults(int maxResults)
    {
        this.maxResults = maxResults;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Criteria setPaging(int itemsPerPage, int pageNumberStartingFromOne)
    {
        this.maxResults = itemsPerPage;
        this.offset = (Math.max(pageNumberStartingFromOne, 1) - 1) * maxResults;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Criteria setSpellCheckString(String spellCheckString)
    {
        this.spellCheckString = spellCheckString;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Criteria setWorkspace(String workspace)
    {
        this.workspace = workspace;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Criteria setForcePagingWithDocumentOrder(boolean force)
    {
        this.forcePagingWithDocumentOrder = force;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public String toXpathExpression()
    {
        JCRMagnoliaCriteriaQueryTranslator translator = new JCRMagnoliaCriteriaQueryTranslator(this);
        XPathSelect statement = new XPathSelect();
        statement.setRoot(XPathTextUtils.encodeDigitsInPath(this.path));
        statement.setPredicate(translator.getPredicate());
        statement.setOrderByClause(translator.getOrderBy());
        String stmt = statement.toStatementString();
        return stmt;
    }

    /**
     * {@inheritDoc}
     */
    public AdvancedResult execute()
    {
        @SuppressWarnings("deprecation")
        String language = javax.jcr.query.Query.XPATH;
        String stmt = toXpathExpression();

        try
        {
            return QueryExecutorHelper.execute(
                stmt,
                language,
                MgnlContext.getJCRSession(workspace),
                maxResults,
                offset,
                spellCheckString,
                forcePagingWithDocumentOrder && this.orderEntries.isEmpty());
        }
        catch (RepositoryException e)
        {
            throw new RuntimeRepositoryException(e);
        }
    }

}