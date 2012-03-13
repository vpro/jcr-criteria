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

import info.magnolia.cms.beans.runtime.FileProperties;
import info.magnolia.cms.core.DefaultContent;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.NodeData;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.link.LinkException;
import info.magnolia.link.LinkTransformerManager;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author fgiust
 * @version $Id$
 */
public class MappedDefaultContent extends DefaultContent implements Map<String, Object>
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(MappedDefaultContent.class);

    /**
     * @param elem
     * @param hierarchyManager
     * @throws RepositoryException
     * @throws AccessDeniedException
     */
    public MappedDefaultContent(Node elem, HierarchyManager hierarchyManager)
        throws RepositoryException,
        AccessDeniedException
    {
        super(elem);
    }

    /**
     * {@inheritDoc}
     */
    public int size()
    {
        return getNodeDataCollection().size();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty()
    {
        return getNodeDataCollection().isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsKey(Object key)
    {
        return getNodeData((String) key).isExist() || hasProperty(key);
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsValue(Object value)
    {
        // not implemented, only get() is needed
        return false;
    }

    /**
     * Shortcut for Content.getNodeData(name).getString() or Content.getNodeData(name).getName().
     * @see java.util.Map#get(Object)
     * @param key property name
     * @return property value
     */
    public Object get(Object key)
    {
        try
        {
            if (!hasNodeData((String) key))
            {
                // support the old lower case value
                if ("uuid".equalsIgnoreCase((String) key))
                {
                    key = "UUID";
                }
                if (hasProperty(key))
                {
                    try
                    {
                        return PropertyUtils.getSimpleProperty(this, (String) key);
                    }
                    catch (IllegalAccessException e)
                    {
                        log.error("can't read property " + key + " from the node " + this, e);
                    }
                    catch (InvocationTargetException e)
                    {
                        log.error("can't read property " + key + " from the node " + this, e);
                    }
                    catch (NoSuchMethodException e)
                    {
                        log.error("can't read property " + key + " from the node " + this, e);
                    }
                }
            }
        }
        catch (RepositoryException e)
        {
            // should really not happen
            log.error("can't check for node data {" + key + "}", e);
        }

        NodeData nodeData = getNodeData((String) key);
        Object value;
        int type = nodeData.getType();
        if (type == PropertyType.DATE)
        {
            value = nodeData.getDate();
        }
        else if (type == PropertyType.BINARY)
        {
            // only file path is supported
            FileProperties props = new FileProperties(this, (String) key);
            value = props.getProperty(FileProperties.PATH);
        }
        else if (nodeData.isMultiValue() == 1)
        // not using NodeData.MULTIVALUE_TRUE since the constant is not defined in magnolia 4.0
        {

            Value[] values = nodeData.getValues();

            String[] valueStrings = new String[values.length];

            for (int j = 0; j < values.length; j++)
            {
                try
                {
                    valueStrings[j] = values[j].getString();
                }
                catch (RepositoryException e)
                {
                    log.debug(e.getMessage());
                }
            }

            value = valueStrings;
        }
        else
        {
            try
            {
                value = info.magnolia.link.LinkUtil.convertLinksFromUUIDPattern(
                    nodeData.getString(),
                    LinkTransformerManager.getInstance().getBrowserLink(getHandle()));
            }
            catch (LinkException e)
            {
                log.warn("Failed to parse links with from " + nodeData.getName(), e);
                value = nodeData.getString();
            }
        }
        return value;
    }

    /**
     * Check if there is a bean property with the given key
     * @param key property name
     * @return true if this is a valid javabean property
     */
    protected boolean hasProperty(Object key)
    {
        try
        {
            return PropertyUtils.getPropertyDescriptor(this, (String) key) != null;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object put(String key, Object value)
    {
        // not implemented, only get() is needed
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Object remove(Object key)
    {
        // not implemented, only get() is needed
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void putAll(Map< ? extends String, ? extends Object> t)
    {
        // not implemented, only get() is needed
    }

    /**
     * {@inheritDoc}
     */
    public void clear()
    {
        // not implemented, only get() is needed
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> keySet()
    {
        Collection<NodeData> nodeDataCollection = getNodeDataCollection();
        Set<String> keys = new HashSet<String>();
        for (Iterator<NodeData> iter = nodeDataCollection.iterator(); iter.hasNext();)
        {
            keys.add(iter.next().getName());
        }

        return keys;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<Object> values()
    {
        Collection<NodeData> nodeDataCollection = getNodeDataCollection();
        Collection<Object> values = new ArrayList<Object>();
        for (Iterator<NodeData> iter = nodeDataCollection.iterator(); iter.hasNext();)
        {
            values.add(iter.next().getString());
        }

        return values;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Map.Entry<String, Object>> entrySet()
    {
        Collection<NodeData> nodeDataCollection = getNodeDataCollection();
        Set<Map.Entry<String, Object>> keys = new HashSet<Map.Entry<String, Object>>();
        for (Iterator<NodeData> iter = nodeDataCollection.iterator(); iter.hasNext();)
        {
            NodeData nd = iter.next();
            final String key = nd.getName();
            final String value = NodeDataUtil.getValueString(nd);
            keys.add(new Map.Entry<String, Object>()
            {

                public String getKey()
                {
                    return key;
                }

                public Object getValue()
                {
                    return value;
                }

                public Object setValue(Object value)
                {
                    return value;
                }
            });
        }

        return keys;
    }
}
