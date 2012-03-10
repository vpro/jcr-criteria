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

import info.magnolia.cms.core.Content;


/**
 * An extension of Content exposing additional informations obtained from a Row item of a QueryResult.
 * @author fgiust
 * @version $Id$
 */
public interface AdvancedResultItem extends Content
{

    /**
     * Returns the excerpt.
     * @return the excerpt
     */
    String getExcerpt();

    /**
     * Returns the excerpt for a specific property.
     * @param selector property to use for the excerpt
     * @return the excerpt
     */
    String getExcerpt(String selector);

    /**
     * Returns the score.
     * @return the score
     */
    double getScore();

    /**
     * Returns the score for a specific property.
     * @param selector property to use for the score
     * @return the score
     */
    double getScore(String selector);
}
