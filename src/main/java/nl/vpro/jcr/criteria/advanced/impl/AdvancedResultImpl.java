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

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.LongSupplier;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import nl.vpro.jcr.criteria.query.*;


/**
 * @author fgiust
 */
@Slf4j
@ToString
public class AdvancedResultImpl implements AdvancedResult {

    private final QueryResult jcrQueryResult;

    private RowIterator rowIterator;

    private final LongSupplier queryCounter;

    private final int itemsPerPage;

    private final int pageNumberStartingFromOne;

    private final String statement;

    private String spellCheckerSuggestion;

    private final Query spellCheckerQuery;


    private final boolean applyLocalPaging;

    private int offset;

    private Long totalResults;


    AdvancedResultImpl(
        QueryResult jcrQueryResult,
        LongSupplier queryCounter,
        int itemsPerPage,
        int pageNumberStartingFromOne,
        String statement,
        Query spellCheckerQuery) throws RepositoryException {
        this(jcrQueryResult, queryCounter, itemsPerPage, pageNumberStartingFromOne, statement, spellCheckerQuery, false);
    }

    /**
     * @param applyLocalPaging don't assume the result iterator is already paginated, do it "manually"
     */
    AdvancedResultImpl(
        QueryResult jcrQueryResult,
        LongSupplier queryCounter,
        int itemsPerPage,
        int pageNumberStartingFromOne,
        String statement,
        Query spellCheckerQuery,
        boolean applyLocalPaging) throws RepositoryException {
        this(jcrQueryResult, queryCounter, itemsPerPage, pageNumberStartingFromOne, statement, spellCheckerQuery, applyLocalPaging, 0);
    }

    /**

     * @param applyLocalPaging don't assume the result iterator is already paginated, do it "manually"
     * @param offset TODO
     */
    AdvancedResultImpl(
        QueryResult jcrQueryResult,
        LongSupplier queryCounter,
        int itemsPerPage,
        int pageNumberStartingFromOne,
        String statement,
        Query spellCheckerQuery,
        boolean applyLocalPaging,
        int offset) {
        this.jcrQueryResult = jcrQueryResult;
        this.queryCounter = queryCounter;
        this.itemsPerPage = itemsPerPage;
        this.statement = statement;
        this.spellCheckerQuery = spellCheckerQuery;
        this.pageNumberStartingFromOne = pageNumberStartingFromOne;
        this.applyLocalPaging = applyLocalPaging;
        this.offset = offset;
    }

    @Override
    public int getItemsPerPage() {
        return itemsPerPage;
    }


    @Override
    public int getPage() {
        return pageNumberStartingFromOne;
    }


    @Override
    public long getTotalSize() {
        if (! totalSizeDetermined()) {
            long queryTotalSize = -1;
            try { // jcrQueryResult instanceof JackrabbitQueryResult) {
                Method m = jcrQueryResult.getClass().getMethod("getTotalSize");
                queryTotalSize = (int) m.invoke(jcrQueryResult);
            } catch (InvocationTargetException | IllegalAccessException e) {
                log.error(e.getMessage(), e);
            } catch (NoSuchMethodException e) {
                log.debug(e.getMessage());
            }
            if (queryTotalSize == -1 && (itemsPerPage == 0 || applyLocalPaging)) {
                totalResults = getRowIterator().getSize();
            } else {
                if (queryTotalSize == -1) {
                    totalResults = queryCounter.getAsLong();
                } else {
                    totalResults = queryTotalSize;
                }
            }
            if (totalResults < 0) {
                log.warn("Total results could not be determined");
            }
        }
        return totalResults;
    }

    boolean totalSizeDetermined() {
        return totalResults != null;
    }

    @Override
    public int getNumberOfPages()  {
        return itemsPerPage > 0 ? (int) Math.round(Math.ceil(((float) getTotalSize() / (float) itemsPerPage))) : 1;
    }

    @Override
    public ResultIterator<AdvancedResultItem> getItems() {

        if ((applyLocalPaging && (itemsPerPage > 0 || offset > 0))) {

            if (offset == 0) {
                offset = (Math.max(pageNumberStartingFromOne, 1) - 1) * itemsPerPage;
            }
            // removing preceding records
            getRowIterator().skip(offset);

            // removing following records and alter getSize()
            return new ResultIteratorImpl<AdvancedResultItem>(rowIterator, AdvancedResultItemImpl::new) {

                @Override
                public boolean hasNext() {
                    return super.getPosition() - offset < getSize() && super.hasNext();
                }
                @Override
                public long getSize() {
                    return Math.min(super.getSize() - offset, itemsPerPage == 0 ? super.getSize() : itemsPerPage);
                }
            };
        }

        return new ResultIteratorImpl<>(getRowIterator(), AdvancedResultItemImpl::new);
    }

    protected RowIterator getRowIterator() {
        if (rowIterator == null) {
            try {
                rowIterator = jcrQueryResult.getRows();
            } catch (RepositoryException e) {
                throw new JCRQueryException(statement, e);
            }
        }
        return rowIterator;
    }

    @Override
    public String getSpellCheckerSuggestion() {
        if (spellCheckerSuggestion == null && spellCheckerQuery != null) {
            RowIterator rows;
            try {
                rows = spellCheckerQuery.execute().getRows();

                // the above query will always return the root node no matter what string we check
                Row r = rows.nextRow();

                // get the result of the spell checking
                Value v = r.getValue("rep:spellcheck()");
                if (v == null) {
                    // no suggestion returned, the spelling is correct or the spell checker
                    // does not know how to correct it.
                } else {
                    spellCheckerSuggestion = v.getString();
                }
            } catch (RepositoryException e) {
                log.warn("Error getting excerpt using " + spellCheckerQuery.getStatement(), e);
                return null;
            }

        }
        return spellCheckerSuggestion;
    }

    @Override
    public AdvancedResultItem getFirstResult() {
        ResultIterator<AdvancedResultItem> items = getItems();
        if (items.hasNext()) {
            return items.next();
        }
        return null;
    }



    @Override
    public <K> ResultIterator<K> getItems(Function<Row, K> wrapper) {
        RowIterator rows;
        try {
            rows = jcrQueryResult.getRows();
        } catch (RepositoryException e)  {
            throw new JCRQueryException(statement, e);
        }

        if (applyLocalPaging && itemsPerPage > 0)  {
            final int offset = (Math.max(pageNumberStartingFromOne, 1) - 1) * itemsPerPage;

            // removing preceding records
            rows.skip(offset);

            // removing following records and alter getSize()
            return new ResultIteratorImpl<K>(rows, wrapper)  {
                @Override
                public boolean hasNext() {
                    return super.getPosition() - offset < getSize() && super.hasNext();
                }

                @Override
                public long getSize() {
                    return Math.min(super.getSize() - offset, itemsPerPage);
                }

            };
        }

        return new ResultIteratorImpl<K>(rows, wrapper);

    }

}
