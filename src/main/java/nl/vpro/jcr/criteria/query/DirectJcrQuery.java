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

import javax.jcr.Session;

import nl.vpro.jcr.criteria.advanced.impl.QueryExecutorHelper;


/**
 * @author fgiust
 */
public class DirectJcrQuery implements ExecutableQuery  {

    private final Criteria.Expression expression;

    private String spellCheckString;

    private int maxResults;

    private int offset;


    public DirectJcrQuery(Criteria.Expression expression) {
        this.expression = expression;
    }

    /**
     * Sets the spellCheckString.
     * @param spellCheckString the spellCheckString to set
     * @return the DirectJcrQuery instance for chaining
     */
    public DirectJcrQuery setSpellCheckString(String spellCheckString) {
        this.spellCheckString = spellCheckString;
        return this;
    }

    /**
     * Sets the maxResults.
     * @param maxResults the maxResults to set
     * @return the DirectJcrQuery instance for chaining
     */
    public DirectJcrQuery setMaxResultsPerPage(int maxResults) {
        this.maxResults = maxResults;
        return this;
    }

    /**
     * Sets the offset.
     * @param offset the offset to set
     * @return the DirectJcrQuery instance for chaining
     */
    public DirectJcrQuery setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    /**
     * Utility method for setting offset easier. If this method is called you should not use setOffset/setMaxResults
     * directly anymore.
     * @param itemsPerPage number of items per page
     * @param pageNumberStartingFromOne page number (starting from 1)
     * @return the DirectJcrQuery instance for chaining
     */
    public DirectJcrQuery setPaging(int itemsPerPage, int pageNumberStartingFromOne) {
        return setMaxResultsPerPage(itemsPerPage)
            .setOffset((Math.max(pageNumberStartingFromOne, 1) - 1) * maxResults);
    }

    @Override
    public AdvancedResult execute(Session session, String language) {
        if (language != null && ! language.equals(expression.getLanguage())) {
            throw new IllegalArgumentException("This direct JCR criteria can only be executed as " + expression.getLanguage());
        }

        return QueryExecutorHelper.execute(
            expression,
            () -> { throw new UnsupportedOperationException(); },
            session,
            maxResults,
            offset,
            spellCheckString,
            false);
    }
}
