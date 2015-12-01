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

package nl.vpro.jcr.criteria.query;

import javax.jcr.query.Row;
import java.util.Iterator;
import java.util.function.Function;


/**
 * The result of a jcr query. You can access to the actual result items using getItems(). This bean will also give you
 * information about the total number of available items, the current page number, the total number of pages.
 * @author fgiust
 * @version $Id$
 */
public interface AdvancedResult  {

    /**
     * An empty result.
     */
    AdvancedResult EMPTY_RESULT = new EmptyResult();

    /**
     * Gets the maximum number of results per page
     * @return the maximum number of results per page
     */
    int getItemsPerPage();

    /**
     * Gets the page number (1, 2, 3...)
     * @return the page number (1, 2, 3...)
     */
    int getPage();

    /**
     * Gets the total number of results that would be retrieved without pagination. Note that jackrabbit may return -1
     * if the query doesn't have a sort condition, in order to optimize execution. Always add an order by clause (e.g.
     * "order by @jcr:score") if you need to get the total size.
     * @return the total number of results that would be retrieved without pagination.
     */
    int getTotalSize();

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
    ResultIterator<AdvancedResultItem> getItems();

    /**
     * Gets an iterator over the results, transforming objects using content2bean while iterating
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
     * @author fgiust
     * @version $Id$
     */
    class EmptyResult implements AdvancedResult {

        private ResultIterator<AdvancedResultItem> iterator = new EmptyResultIterator();

        @Override
        public int getTotalSize() {
            return 0;
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
        public int getItemsPerPage() {
            return 0;
        }

        @Override
        public ResultIterator<AdvancedResultItem> getItems() {
            return iterator;
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
            return null;
        }
    }

    /**
     * @author fgiust
     * @version $Id$
     */
    final class EmptyResultIterator implements ResultIterator<AdvancedResultItem> {

        @Override
        public void skip(long skipNum) {
            // nothing to do
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
        public AdvancedResultItem next() {
            return null;
        }

        @Override
        public void remove() {
            // nothing to do
        }

        /**
         * Adds foreach support.
         */
        @Override
        public Iterator<AdvancedResultItem> iterator() {
            return this;
        }
    }

}
