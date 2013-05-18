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

package net.sourceforge.openutils.mgnlcriteria.jcr.query;

import java.util.Iterator;


/**
 * The result of a jcr query. You can access to the actual result items using getItems(). This bean will also give you
 * information about the total number of available items, the current page number, the total number of pages.
 * @author fgiust
 * @version $Id$
 */
public interface AdvancedResult
{

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
     * @param theclass destination class.
     * @return an iterator over the results
     */
    <K> ResultIterator<K> getItems(Class<K> theclass);

    /**
     * Returns the fist result if available, null otherwise.
     * @return the fist result if available, null otherwise.
     */
    AdvancedResultItem getFirstResult();

    /**
     * @author fgiust
     * @version $Id$
     */
    class EmptyResult implements AdvancedResult
    {

        private ResultIterator<AdvancedResultItem> iterator = new EmptyResultIterator();

        public int getTotalSize()
        {
            return 0;
        }

        public String getSpellCheckerSuggestion()
        {
            return null;
        }

        public int getPage()
        {
            return 0;
        }

        public int getItemsPerPage()
        {
            return 0;
        }

        public ResultIterator<AdvancedResultItem> getItems()
        {
            return iterator;
        }

        public int getNumberOfPages()
        {
            return 0;
        }

        public AdvancedResultItem getFirstResult()
        {
            return null;
        }

        public <K> ResultIterator<K> getItems(Class<K> theclass)
        {
            return null;
        }
    }

    /**
     * @author fgiust
     * @version $Id$
     */
    static final class EmptyResultIterator implements ResultIterator<AdvancedResultItem>
    {

        public void skip(long skipNum)
        {
            // nothing to do
        }

        public long getSize()
        {
            return 0;
        }

        public long getPosition()
        {
            return 0;
        }

        public boolean hasNext()
        {
            return false;
        }

        public AdvancedResultItem next()
        {
            return null;
        }

        public void remove()
        {
            // nothing to do
        }

        /**
         * Adds foreach support.
         */
        public Iterator iterator()
        {
            return this;
        }
    }

}
