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

import info.magnolia.cms.security.AccessDeniedException;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResultItem;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.ResultIteratorImpl;
import net.sourceforge.openutils.mgnlcriteria.utils.JcrCompatUtils;


/**
 * Wraps the RowIterator returned by an AdvancedResult instance, adapting each Row to AdvancedResultItem.
 * @author fgiust
 * @version $Id$
 */
public class AdvancedResultItemResultIterator extends ResultIteratorImpl<AdvancedResultItem>
{

    /**
     * @param rowIterator
     * @param hm
     */
    public AdvancedResultItemResultIterator(RowIterator rowIterator)
    {
        super(rowIterator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AdvancedResultItem wrap(Row row)
    {
        try
        {
            Item jcrNode = JcrCompatUtils.getJCRNode(row);
            if (jcrNode == null)
            {
                return null;
            }

            return new AdvancedResultItemImpl(row, jcrNode);
        }
        catch (AccessDeniedException e)
        {
            throw new RuntimeException(e);
        }
        catch (RepositoryException e)
        {
            throw new RuntimeException(e);
        }
    }

}
