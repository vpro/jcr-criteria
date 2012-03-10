/**
 *
 * Criteria API for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlcriteria.html)
 * Copyright(C) 2009-2011, Openmind S.r.l. http://www.openmindonline.it
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

import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Criterion;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Order;


/**
 * <tt>Criteria</tt> is a simplified API for retrieving JCR Nodes by composing <tt>Criterion</tt> objects. This is a
 * very convenient approach for functionality like "search" screens where there is a variable number of conditions to be
 * placed upon the result set.<br>
 * The <tt>JCRCriteriaFactory</tt> is a factory for <tt>Criteria</tt>. <tt>Criterion</tt> instances are usually obtained
 * via the factory methods on <tt>Restrictions</tt>. eg.
 * 
 * <pre>
 * Calendar begin = Calendar.getInstance();
 * begin.set(1999, Calendar.JANUARY, 1);
 * Calendar end = Calendar.getInstance();
 * end.set(2001, Calendar.DECEMBER, 31);
 *
 * Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(ContentRepository.WEBSITE)
 *     .setBasePath("/pets")
 *     .add(Restrictions.contains("@title", "Lucky"))
 *     .add(Restrictions.eq("@petType", "dog"))
 *     .add(Restrictions.betweenDates("@birthDate", begin, end))
 *     .addOrder(Order.desc("@title"));
 * </pre>
 * 
 * will be translated into the following xpath statement
 * 
 * <pre>
 *  //pets//*[((jcr:contains(@title, 'Lucky')) and (@petType='dog')
 *    and (@birthDate &gt;=xs:dateTime('1999-01-01T00:00:00.000+00:00')
 *    and @birthDate &lt;=xs:dateTime('2001-12-31T23:59:59.999+00:00')))]
 *    order by @title descending
 * </pre>
 * 
 * Furthermore, you may want to have only a subset of the whole result set returned, much like in a MySQL limit clause.
 * In this case, you will use the <code>setFirstResult</code> and <code>setMaxResults</code> methods. Here is an
 * example.
 * 
 * <pre>
 * Criteria criteria = JCRCriteriaFactory
 *     .createCriteria()
 *     .setWorkspace(ContentRepository.WEBSITE)
 *     .setBasePath("/pets")
 *     .add(Restrictions.betweenDates("@birthDate", begin, end))
 *     .addOrder(Order.asc("@birthDate"))
 *     .setFirstResult(5)
 *     .setMaxResults(5);
 *</pre>
 * 
 * Notice the <code>setFirstResult(int)</code> and <code>setMaxResults(int)</code> methods. Now executing the query will
 * return a subset of no more than five results, starting from the 6th item (counting starts from 0). If you dont
 * specify these two calls, the entire result set will be returned. If you only call <code>setMaxResults(int)</code>,
 * the result set will be the subset of elements <code>[0, maxResults]</code> (firstResultValue is 0 by default).<br>
 * Another way to paginate results is by using the <code>setPaging</code> method:
 * 
 * <pre>
 * Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(ContentRepository.WEBSITE)
 *     .setBasePath("/pets")
 *     .add(Restrictions.betweenDates("@birthDate", begin, end))
 *     .addOrder(Order.asc("@birthDate"))
 *     .setPaging(5, 2);
 *</pre>
 * 
 * <br>
 * A word of warning about implementations returned by <code>JCRCriteriaFactory</code>. They are <strong>NOT</strong>
 * thread-safe, therefore client code wishing to use one of them as a shared global variable <strong>MUST</strong>
 * coordinate access to it. These objects are actually meant to be instantiated and used within a method scope (e.g. a
 * service method), where no concurrent issues arise. <br>
 * <br>
 * This API was inspired by Hibernate's Criteria API. <br>
 * <br>
 * @see JCRCriteriaFactory#createCriteria()
 * @see Order
 * @author Federico Grilli
 * @author fgiust
 * @version $Id$
 */
public interface Criteria extends ExecutableQuery
{

    /**
     * Add a {@link Criterion restriction} to constrain the results to be retrieved.
     * @param criterion The {@link Criterion criterion} object representing the restriction to be applied.
     * @return this (for method chaining)
     */
    Criteria add(Criterion criterion);

    /**
     * Add an {@link Order ordering} to the result set. Only <strong>one Order criterion per query</strong> can be
     * applied. Any Order added after the first one will be ignored.
     * @param order The {@link Order order} object representing an ordering to be applied to the results.
     * @return this (for method chaining)
     */
    Criteria addOrder(Order order);

    /**
     * Set a limit upon the number of objects to be retrieved.
     * @param maxResults the maximum number of results
     * @return this (for method chaining)
     */
    Criteria setMaxResults(int maxResults);

    /**
     * Set the first result to be retrieved.
     * @param firstResult the first result to retrieve, numbered from <tt>0</tt>
     * @return this (for method chaining)
     */
    Criteria setFirstResult(int firstResult);

    /**
     * Sets the base path of the query, i.e. the branch in the repository tree where the search will take place
     * @param path the handle of a node, or a xpath query prefix in the form //handle/of/a/node//*
     * @return this (for method chaining)
     */
    Criteria setBasePath(String path);

    /**
     * @param itemsPerPage maximum number of results per page (i.e. page size)
     * @param pageNumberStartingFromOne page number to retrieve (1, 2, 3, ...)
     * @return this (for method chaining)
     */
    Criteria setPaging(int itemsPerPage, int pageNumberStartingFromOne);

    /**
     * Sets the original input string for spell checking.
     * @param spellCheckString the actual input string for spell checking
     * @return this (for method chaining)
     */
    Criteria setSpellCheckString(String spellCheckString);

    /**
     * Sets the name of the workspace where the search will take place
     * @param workspace the name of a workspace
     * @return this (for method chaining)
     */
    Criteria setWorkspace(String workspace);

    /**
     * Returns the generated xpath expression
     * @return the generated xpath expression
     */
    String toXpathExpression();

    /**
     * <p>
     * Enable paging while keeping results sorted in document order.
     * </p>
     * <p>
     * Document order is only applied by jackrabbit after the paginated result has been retrieved.
     * </p>
     * <p>
     * This means that if you have 20 nodes and you want to retrieve them in 2 pages containing 10 elements, only the
     * order of elements in a single page is kept (but the "first" 10 noted in the first page will not be the nodes you
     * are expecting in document order). Setting this flag to true forces the retrieval of the full list of nodes and a
     * post-pagination which will mimic the behaviour you get when an "order by" is specified.
     * </p>
     * <p>
     * Warning: this has surely a performance hit, since jackrabbit applied document ordering by retrieving any single
     * node (while normally pagination is applied directly on the luce index).
     * </p>
     * @param force true to force paging while keeping results sorted in document order
     * @return this (for method chaining)
     */
    Criteria setForcePagingWithDocumentOrder(boolean force);
}