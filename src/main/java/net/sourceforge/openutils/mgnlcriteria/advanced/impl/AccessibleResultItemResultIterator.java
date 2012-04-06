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

package net.sourceforge.openutils.mgnlcriteria.advanced.impl;

import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.security.PermissionUtil;

import java.util.NoSuchElementException;

import javax.jcr.Session;
import javax.jcr.query.RowIterator;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResultItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author dschivo
 * @version $Id$
 */
public class AccessibleResultItemResultIterator extends AdvancedResultItemResultIterator
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(AccessibleResultItemResultIterator.class);

    /**
     * Local variable storing the next accessible result. Method hasNext() fetches it, method next() resets it.
     */
    private AdvancedResultItem next;

    /**
     *
     */
    public AccessibleResultItemResultIterator(RowIterator rowIterator, HierarchyManager hm)
    {
        super(rowIterator, hm);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext()
    {
        // already fetched, method next() not yet called
        if (next != null)
        {
            return true;
        }
        // no more results
        if (!super.hasNext())
        {
            return false;
        }
        // search for the next accessible result
        do
        {
            next = super.next();
            if (!PermissionUtil.isGranted(hm.getWorkspace().getName(), next.getHandle(), Session.ACTION_READ))
            {
                next = null;
            }
        }
        while (next == null && super.hasNext());
        // return true if a next result exists and it is accessible
        return next != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdvancedResultItem next()
    {
        // no more results
        if (next == null && !hasNext())
        {
            throw new NoSuchElementException();
        }
        // reset the local variable before returning its value
        AdvancedResultItem result = next;
        next = null;
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getPosition()
    {
        return next == null ? super.getPosition() : super.getPosition() - 1;
    }
}
