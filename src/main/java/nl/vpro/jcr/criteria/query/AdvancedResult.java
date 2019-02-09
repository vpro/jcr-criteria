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

import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.jcr.query.Row;

import nl.vpro.jcr.criteria.advanced.impl.AdvancedResultItemImpl;


/**
 * The result of a jcr query. You can access to the actual result items using getItems(). This bean will also give you
 * information about the total number of available items, the current page number, the total number of pages.
 * @author fgiust
 */
public interface AdvancedResult extends Iterable<AdvancedResultItem> {

    /**
     * An empty result.
     */
    AdvancedResult EMPTY_RESULT = new EmptyResult();

    /**
     * Gets the maximum number of results per page
     * @return the maximum number of results per page or <code>null</code> if not defined
     */
    Integer getItemsPerPage();

    /**
     * Gets the page number (1, 2, 3...) <em>Note that this not <bold>not</bold> start at 0.</em>
     * @return the page number (1, 2, 3...)
     */
    int getPage();

    /**
     * Gets the total number of results that would be retrieved without pagination. Note that jackrabbit may return -1
     * if the query doesn't have a sort condition, in order to optimize execution. Always add an order by clause (e.g.
     * "order by @jcr:score") if you need to get the total size.
     * @return the total number of results that would be retrieved without pagination.
     */
    long getTotalSize();

    /**
     * Gets the total number of pages
     * @return total number of pages
     */
    int getNumberOfPages();

    /**
     * Gets the suggestion from the spell checker. Note that spell checker must be configured in jackrabbit for this to
     * work. See http://wiki.apache.org/jackrabbit/Search for details.
     * @return the suggestion from the spell checker
     */
    String getSpellCheckerSuggestion();

    /**
     * Gets an iterator over the results
     * @return an iterator over the results
     */
    default ResultIterator<AdvancedResultItem> getItems() {
        return getItems(AdvancedResultItemImpl::new);
    }

    /**
     * Gets an iterator over the results, transforming objects using the given function while iterating
     * @param <K> destination class.
     * @return an iterator over the results
     */
    <K> ResultIterator<K> getItems(Function<Row, K> wrapper);

    /**
     * Returns the fist result if available, null otherwise.
     * @return the fist result if available, null otherwise.
     */
    AdvancedResultItem getFirstResult();


    /**
     * @since 2.0
     */
    @Override
    default ResultIterator<AdvancedResultItem> iterator() {
        return getItems();
    }

    /**
     * @since 2.0
     */
    default Stream<AdvancedResultItem> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED), false);
    }

    /**
     * @author fgiust
     */
    class EmptyResult implements AdvancedResult {

        @Override
        public long getTotalSize() {
            return 0L;
        }

        @Override
        public String getSpellCheckerSuggestion() {
            return null;
        }

        @Override
        public int getPage() {
            return 0;
        }

        @Override
        public Integer getItemsPerPage() {
            return null;
        }


        @Override
        public int getNumberOfPages() {
            return 0;
        }

        @Override
        public AdvancedResultItem getFirstResult() {
            return null;
        }

        @Override
        public <K> ResultIterator<K> getItems(Function<Row, K> wrapper) {
            return new EmptyResultIterator<K>();
        }
    }

    /**
     * @author fgiust
     */
    final class EmptyResultIterator<K> implements ResultIterator<K> {

        @Override
        public void skip(long skipNum) {
            if (skipNum > 0) {
                throw new NoSuchElementException();
            }
        }

        @Override
        public long getSize() {
            return 0;
        }

        @Override
        public long getPosition() {
            return 0;
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public K next() {
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new IllegalStateException();
        }

    }

}
