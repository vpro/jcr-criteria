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

import info.magnolia.content2bean.Content2BeanException;

import javax.jcr.Node;


/**
 * Transitionary class for shielding the usage of the deprecated Content2Bean while waiting for Node2Bean which is
 * available only in Magnolia 5 (guys, deprecating classes before giving a replacement is not nice).
 * @author fgiust
 * @version $Id$
 */
public class ToBeanUtils
{

    public static Object toBean(Node node, boolean recursive, final Class defaultClass)
    {
        try
        {
            return info.magnolia.content2bean.Content2BeanUtil.toBean(
                new info.magnolia.cms.core.DefaultContent(node),
                recursive,
                defaultClass);
        }
        catch (Content2BeanException e)
        {
            throw new RuntimeException(e);
        }
    }
}
