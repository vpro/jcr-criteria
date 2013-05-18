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

package net.sourceforge.openutils.mgnlcriteria.advanced.impl;

import info.magnolia.content2bean.Content2BeanException;
import info.magnolia.content2bean.Content2BeanUtil;
import info.magnolia.jcr.RuntimeRepositoryException;

import javax.jcr.Item;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResultItem;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRQueryException;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.ResultIterator;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.ResultIteratorImpl;
import net.sourceforge.openutils.mgnlcriteria.utils.JcrCompatUtils;
import net.sourceforge.openutils.mgnlcriteria.utils.ToBeanUtils;

import org.apache.jackrabbit.core.query.lucene.QueryResultImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author fgiust
 * @version $Id$
 */
public class AdvancedResultImpl implements AdvancedResult
{

    private final QueryResultImpl jcrQueryResult;

    private final int itemsPerPage;

    private final int pageNumberStartingFromOne;

    private final String statement;

    private String spellCheckerSuggestion;

    private final Query spellCheckerQuery;

    private Logger log = LoggerFactory.getLogger(AdvancedResultImpl.class);

    private final boolean applyLocalPaging;

    private int offset;

    /**
     * @param jcrQueryResult
     * @param itemsPerPage
     * @param pageNumberStartingFromOne
     * @param statement
     * @param hm
     * @param spellCheckerQuery
     */
    public AdvancedResultImpl(
        QueryResultImpl jcrQueryResult,
        int itemsPerPage,
        int pageNumberStartingFromOne,
        String statement,
        Query spellCheckerQuery)
    {
        this(jcrQueryResult, itemsPerPage, pageNumberStartingFromOne, statement, spellCheckerQuery, false);
    }

    /**
     * @param jcrQueryResult
     * @param itemsPerPage
     * @param pageNumberStartingFromOne
     * @param statement
     * @param hm
     * @param spellCheckerQuery
     * @param applyLocalPaging don't assume the result iterator is already paginated, do it "manually"
     */
    public AdvancedResultImpl(
        QueryResultImpl jcrQueryResult,
        int itemsPerPage,
        int pageNumberStartingFromOne,
        String statement,
        Query spellCheckerQuery,
        boolean applyLocalPaging)
    {
        this(jcrQueryResult, itemsPerPage, pageNumberStartingFromOne, statement, spellCheckerQuery, applyLocalPaging, 0);
    }

    /**
     * @param jcrQueryResult
     * @param itemsPerPage
     * @param pageNumberStartingFromOne
     * @param statement
     * @param hm
     * @param spellCheckerQuery
     * @param applyLocalPaging don't assume the result iterator is already paginated, do it "manually"
     * @param offset TODO
     */
    public AdvancedResultImpl(
        QueryResultImpl jcrQueryResult,
        int itemsPerPage,
        int pageNumberStartingFromOne,
        String statement,
        Query spellCheckerQuery,
        boolean applyLocalPaging,
        int offset)
    {
        this.jcrQueryResult = jcrQueryResult;
        this.itemsPerPage = itemsPerPage;
        this.statement = statement;
        this.spellCheckerQuery = spellCheckerQuery;
        this.pageNumberStartingFromOne = pageNumberStartingFromOne;
        this.applyLocalPaging = applyLocalPaging;
        this.offset = offset;
    }

    /**
     * {@inheritDoc}
     */
    public int getItemsPerPage()
    {
        return itemsPerPage;
    }

    /**
     * {@inheritDoc}
     */
    public int getPage()
    {
        return pageNumberStartingFromOne;
    }

    /**
     * {@inheritDoc}
     */
    public int getTotalSize()
    {
        if (jcrQueryResult.getTotalSize() == -1 && (itemsPerPage == 0 || applyLocalPaging))
        {
            try
            {
                return (int) jcrQueryResult.getNodes().getSize();
            }
            catch (RepositoryException e)
            {
                // ignore, the standard total size will be returned
            }
        }
        return jcrQueryResult.getTotalSize();
    }

    /**
     * {@inheritDoc}
     */
    public int getNumberOfPages()
    {
        return itemsPerPage > 0 ? (int) Math.round(Math.ceil(((float) getTotalSize() / (float) itemsPerPage))) : 1;
    }

    /**
     * {@inheritDoc}
     */
    public ResultIterator<AdvancedResultItem> getItems()
    {

        RowIterator rows;
        try
        {
            rows = jcrQueryResult.getRows();
        }
        catch (RepositoryException e)
        {
            JCRQueryException jqe = new JCRQueryException(statement, e);
            throw jqe;
        }

        if ((applyLocalPaging && (itemsPerPage > 0 || offset > 0)))
        {

            if (offset == 0)
            {
                offset = (Math.max(pageNumberStartingFromOne, 1) - 1) * itemsPerPage;
            }

            // removing preceding records
            rows.skip(offset);

            // removing folllowing records and alter getSize()
            return new AccessibleResultItemResultIterator(rows)
            {

                /**
                 * {@inheritDoc}
                 */
                @Override
                public boolean hasNext()
                {
                    return super.getPosition() - offset < getSize() && super.hasNext();
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public long getSize()
                {
                    return Math.min(super.getSize() - offset, itemsPerPage == 0 ? super.getSize() : itemsPerPage);
                }
            };
        }

        return new AccessibleResultItemResultIterator(rows);
    }

    /**
     * {@inheritDoc}
     */
    public String getSpellCheckerSuggestion()
    {
        if (spellCheckerSuggestion == null && spellCheckerQuery != null)
        {
            RowIterator rows;
            try
            {
                rows = spellCheckerQuery.execute().getRows();

                // the above query will always return the root node no matter what string we check
                Row r = rows.nextRow();

                // get the result of the spell checking
                Value v = r.getValue("rep:spellcheck()");
                if (v == null)
                {
                    // no suggestion returned, the spelling is correct or the spell checker
                    // does not know how to correct it.
                }
                else
                {
                    spellCheckerSuggestion = v.getString();
                }
            }
            catch (InvalidQueryException e)
            {
                log.warn("Error getting excerpt using " + spellCheckerQuery.getStatement(), e);
                return null;
            }
            catch (RepositoryException e)
            {
                log.warn("Error getting excerpt using " + spellCheckerQuery.getStatement(), e);
                return null;
            }

        }
        return spellCheckerSuggestion;
    }

    /**
     * {@inheritDoc}
     */
    public AdvancedResultItem getFirstResult()
    {
        ResultIterator<AdvancedResultItem> items = getItems();
        if (items.hasNext())
        {
            return items.next();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public <K> ResultIterator<K> getItems(final Class<K> theclass)
    {
        RowIterator rows;
        try
        {
            rows = jcrQueryResult.getRows();
        }
        catch (RepositoryException e)
        {
            throw new JCRQueryException(statement, e);
        }

        if (applyLocalPaging && itemsPerPage > 0)
        {
            final int offset = (Math.max(pageNumberStartingFromOne, 1) - 1) * itemsPerPage;

            // removing preceding records
            rows.skip(offset);

            // removing folllowing records and alter getSize()
            return new ResultIteratorImpl<K>(rows)
            {

                /**
                 * {@inheritDoc}
                 */
                @Override
                public boolean hasNext()
                {
                    return super.getPosition() - offset < getSize() && super.hasNext();
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public long getSize()
                {
                    return Math.min(super.getSize() - offset, itemsPerPage);
                }

                @SuppressWarnings("unchecked")
                @Override
                protected K wrap(Row row)
                {
                    try
                    {
                        Item jcrNode = JcrCompatUtils.getJCRNode(row);
                        if (jcrNode == null)
                        {
                            return null;
                        }

                        return (K) ToBeanUtils.toBean(new AdvancedResultItemImpl(row, jcrNode), true, theclass);
                    }
                    catch (RepositoryException e)
                    {
                        throw new RuntimeRepositoryException(e);
                    }
                }
            };
        }

        return new ResultIteratorImpl<K>(rows)
        {

            @SuppressWarnings("unchecked")
            @Override
            protected K wrap(Row row)
            {
                try
                {
                    Item jcrNode = JcrCompatUtils.getJCRNode(row);
                    if (jcrNode == null)
                    {
                        return null;
                    }

                    return (K) ToBeanUtils.toBean(new AdvancedResultItemImpl(row, jcrNode), true, theclass);
                }
                catch (RepositoryException e)
                {
                    throw new RuntimeRepositoryException(e);
                }
            }
        };
    }

}
