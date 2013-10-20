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
import info.magnolia.jcr.RuntimeRepositoryException;
import info.magnolia.jcr.wrapper.DelegateNodeWrapper;
import info.magnolia.jcr.wrapper.I18nNodeWrapper;

import java.lang.reflect.InvocationTargetException;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.Row;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResultItem;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author fgiust
 * @version $Id$
 */
public class AdvancedResultItemImpl extends DelegateNodeWrapper implements AdvancedResultItem
{

    private final Row row;

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(AdvancedResultItemImpl.class);

    /**
     * @param elem
     * @param hierarchyManager
     * @throws RepositoryException
     * @throws AccessDeniedException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     */
    public AdvancedResultItemImpl(Row row, Item item) throws RepositoryException, AccessDeniedException
    {
        super(new I18nNodeWrapper((Node) item));
        this.row = row;
    }

    /**
     * {@inheritDoc}
     */
    public String getExcerpt()
    {

        return getExcerpt(".");
    }

    /**
     * {@inheritDoc}
     */
    public String getExcerpt(String selector)
    {

        Value excerptValue;
        try
        {
            excerptValue = row.getValue("rep:excerpt(" + selector + ")");
        }
        catch (RepositoryException e)
        {
            log.warn("Error getting excerpt for " + this.getHandle(), e);
            return null;
        }

        if (excerptValue != null)
        {
            try
            {
                return excerptValue.getString();
            }
            catch (RepositoryException e)
            {
                log.warn("Error getting excerpt for " + this.getHandle(), e);
                return null;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public double getScore()
    {
        try
        {
            return (Double) PropertyUtils.getSimpleProperty(row, "score");
        }
        catch (IllegalAccessException e)
        {
            log.warn("Error getting score for {}", this.getHandle(), e);
        }
        catch (InvocationTargetException e)
        {
            log.warn("Error getting score for {}", this.getHandle(), e);
        }
        catch (NoSuchMethodException e)
        {
            log
                .error("Unsupported version of jackrabbit detected, you need at least 1.6.x or a jcr 2.0 compliant version");
        }

        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public double getScore(String selector)
    {
        if (selector == null)
        {
            try
            {
                return row.getScore();
            }
            catch (RepositoryException e)
            {
                log.warn("unable to extract score from {}", row);
            }
        }
        try
        {
            return row.getScore(selector);
        }
        catch (RepositoryException e)
        {
            log.warn("unable to extract score from {} using selector {}", row, selector);
        }

        return 0;
    }

    public String getTitle()
    {
        try
        {
            if (hasProperty("title"))
            {
                return getProperty("title").getString();
            }
        }
        catch (RepositoryException e)
        {
            throw new RuntimeRepositoryException(e);
        }

        return null;
    }

    public String getHandle()
    {
        try
        {
            return getPath();
        }
        catch (RepositoryException e)
        {
            throw new RuntimeRepositoryException(e);
        }
    }

    public Node getJCRNode()
    {
        return this;
    }

}
