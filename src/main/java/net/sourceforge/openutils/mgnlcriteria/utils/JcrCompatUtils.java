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

package net.sourceforge.openutils.mgnlcriteria.utils;

import java.lang.reflect.InvocationTargetException;

import javax.jcr.Item;
import javax.jcr.RepositoryException;
import javax.jcr.query.Row;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility methods for accessing jcr 2.0 only properties using reflection, for compatibility with jackrabbit 1.6.
 * @author fgiust
 * @version $Id$
 */
public final class JcrCompatUtils
{

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(JcrCompatUtils.class);

    // don't instantiate
    private JcrCompatUtils()
    {

    }

    public static Item getJCRNode(Row row) throws RepositoryException
    {

        try
        {
            return (Item) PropertyUtils.getProperty(row, "node");
        }
        catch (IllegalAccessException e)
        {
        }
        catch (InvocationTargetException e)
        {
            log.warn("Error extracting node from row: {}", e.getTargetException() != null ? e
                .getTargetException()
                .getClass()
                .getName()
                + " "
                + e.getTargetException().getMessage() : e.getMessage());
        }
        catch (NoSuchMethodException e)
        {
            log
                .error("Unsupported version of jackrabbit detected, you need at least 1.6.x or a jcr 2.0 compliant version");
        }

        return null;
    }
}
