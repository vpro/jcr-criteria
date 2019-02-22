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
import java.util.function.Supplier;

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

    private final Supplier<QueryResult> jcrQueryResult;
    private final LongSupplier queryCounter;

    private final Integer  itemsPerPage;

    private final int pageNumberStartingFromOne;

    private final Criteria.Expression statement;

    private String spellCheckerSuggestion;

    private final Query spellCheckerQuery;


    private final boolean applyLocalPaging;

    private int offset;

    private Long totalResults;


    /**

     * @param applyLocalPaging don't assume the result iterator is already paginated, do it "manually"
     * @param offset TODO
     */
    AdvancedResultImpl(
        Supplier<QueryResult> jcrQueryResult,
        LongSupplier queryCounter,
        Integer itemsPerPage,
        int pageNumberStartingFromOne,
        Criteria.Expression statement,
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
    public Integer getItemsPerPage() {
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
                QueryResult queryResult = jcrQueryResult.get();
                Method m = queryResult.getClass().getMethod("getTotalSize");
                queryTotalSize = (int) m.invoke(queryResult);
                log.debug("Using  {}", m);
            } catch (InvocationTargetException | IllegalAccessException e) {
                log.error(e.getMessage(), e);
            } catch (NoSuchMethodException e) {
                log.debug("{}: {}", e.getClass().getSimpleName(),  e.getMessage());
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
    public <K> ResultIterator<K> getItems(Function<Row, K> wrapper) {

        if ((applyLocalPaging && ((itemsPerPage != null && itemsPerPage > 0) || offset > 0))) {

            if (offset == 0) {
                offset = (Math.max(pageNumberStartingFromOne, 1) - 1) * itemsPerPage;
            }
            // removing preceding records
            RowIterator rowIterator = getRowIterator();
            rowIterator.skip(offset);

            // removing following records and alter getSize()
            return new ResultIteratorImpl<K>(rowIterator, wrapper) {

                @Override
                public boolean hasNext() {
                    return super.getPosition() - offset < getSize() && super.hasNext();
                }
                @Override
                public long getSize() {
                    return Math.min(super.getSize() - offset, itemsPerPage == null || itemsPerPage == 0 ? super.getSize() : itemsPerPage);
                }
            };
        }

        return new ResultIteratorImpl<K>(getRowIterator(), wrapper);
    }

    protected RowIterator getRowIterator() {
        try {
            return jcrQueryResult.get().getRows();
        } catch (RepositoryException e) {
            throw new JCRQueryException(statement, e);
        }
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





}
