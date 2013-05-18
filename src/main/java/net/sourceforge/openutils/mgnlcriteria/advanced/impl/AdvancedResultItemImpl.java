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
import info.magnolia.jcr.util.ContentMap;
import info.magnolia.jcr.wrapper.I18nNodeWrapper;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Calendar;

import javax.jcr.Binary;
import javax.jcr.InvalidItemStateException;
import javax.jcr.InvalidLifecycleTransitionException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.ItemVisitor;
import javax.jcr.MergeException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Row;
import javax.jcr.version.ActivityViolationException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionHistory;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResultItem;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author fgiust
 * @version $Id$
 */
public class AdvancedResultItemImpl extends ContentMap implements AdvancedResultItem
{

    private final Row row;

    private final Node node;

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
        this.node = new I18nNodeWrapper((Node) item);
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

    /**
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Item#getPath()
     */
    public String getPath() throws RepositoryException
    {
        return node.getPath();
    }

    /**
     * @ * @see javax.jcr.Item#getName()
     */
    public String getName()
    {
        try
        {
            return node.getName();
        }
        catch (RepositoryException e)
        {
            throw new RuntimeRepositoryException(e);
        }
    }

    /**
     * @param depth
     * @return
     * @throws ItemNotFoundException
     * @throws AccessDeniedException
     * @throws RepositoryException
     * @see javax.jcr.Item#getAncestor(int)
     */
    public Item getAncestor(int depth) throws ItemNotFoundException, javax.jcr.AccessDeniedException,
        RepositoryException
    {
        return node.getAncestor(depth);
    }

    /**
     * @param relPath
     * @return
     * @throws ItemExistsException
     * @throws PathNotFoundException
     * @throws VersionException
     * @throws ConstraintViolationException
     * @throws LockException
     * @throws RepositoryException
     * @see javax.jcr.Node#addNode(java.lang.String)
     */
    public Node addNode(String relPath) throws ItemExistsException, PathNotFoundException, VersionException,
        ConstraintViolationException, LockException, RepositoryException
    {
        return node.addNode(relPath);
    }

    /**
     * @return
     * @throws ItemNotFoundException
     * @throws AccessDeniedException
     * @throws RepositoryException
     * @see javax.jcr.Item#getParent()
     */
    public Node getParent() throws ItemNotFoundException, javax.jcr.AccessDeniedException, RepositoryException
    {
        return node.getParent();
    }

    /**
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Item#getDepth()
     */
    public int getDepth() throws RepositoryException
    {
        return node.getDepth();
    }

    /**
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Item#getSession()
     */
    public Session getSession() throws RepositoryException
    {
        return node.getSession();
    }

    /**
     * @return
     * @see javax.jcr.Item#isNode()
     */
    public boolean isNode()
    {
        return node.isNode();
    }

    /**
     * @return
     * @see javax.jcr.Item#isNew()
     */
    public boolean isNew()
    {
        return node.isNew();
    }

    /**
     * @return
     * @see javax.jcr.Item#isModified()
     */
    public boolean isModified()
    {
        return node.isModified();
    }

    /**
     * @param otherItem
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Item#isSame(javax.jcr.Item)
     */
    public boolean isSame(Item otherItem) throws RepositoryException
    {
        return node.isSame(otherItem);
    }

    /**
     * @param relPath
     * @param primaryNodeTypeName
     * @return
     * @throws ItemExistsException
     * @throws PathNotFoundException
     * @throws NoSuchNodeTypeException
     * @throws LockException
     * @throws VersionException
     * @throws ConstraintViolationException
     * @throws RepositoryException
     * @see javax.jcr.Node#addNode(java.lang.String, java.lang.String)
     */
    public Node addNode(String relPath, String primaryNodeTypeName) throws ItemExistsException, PathNotFoundException,
        NoSuchNodeTypeException, LockException, VersionException, ConstraintViolationException, RepositoryException
    {
        return node.addNode(relPath, primaryNodeTypeName);
    }

    /**
     * @param visitor
     * @throws RepositoryException
     * @see javax.jcr.Item#accept(javax.jcr.ItemVisitor)
     */
    public void accept(ItemVisitor visitor) throws RepositoryException
    {
        node.accept(visitor);
    }

    /**
     * @throws AccessDeniedException
     * @throws ItemExistsException
     * @throws ConstraintViolationException
     * @throws InvalidItemStateException
     * @throws ReferentialIntegrityException
     * @throws VersionException
     * @throws LockException
     * @throws NoSuchNodeTypeException
     * @throws RepositoryException
     * @deprecated
     * @see javax.jcr.Item#save()
     */
    @Deprecated
    public void save() throws javax.jcr.AccessDeniedException, ItemExistsException, ConstraintViolationException,
        InvalidItemStateException, ReferentialIntegrityException, VersionException, LockException,
        NoSuchNodeTypeException, RepositoryException
    {
        node.save();
    }

    /**
     * @param srcChildRelPath
     * @param destChildRelPath
     * @throws UnsupportedRepositoryOperationException
     * @throws VersionException
     * @throws ConstraintViolationException
     * @throws ItemNotFoundException
     * @throws LockException
     * @throws RepositoryException
     * @see javax.jcr.Node#orderBefore(java.lang.String, java.lang.String)
     */
    public void orderBefore(String srcChildRelPath, String destChildRelPath)
        throws UnsupportedRepositoryOperationException, VersionException, ConstraintViolationException,
        ItemNotFoundException, LockException, RepositoryException
    {
        node.orderBefore(srcChildRelPath, destChildRelPath);
    }

    /**
     * @param keepChanges
     * @throws InvalidItemStateException
     * @throws RepositoryException
     * @see javax.jcr.Item#refresh(boolean)
     */
    public void refresh(boolean keepChanges) throws InvalidItemStateException, RepositoryException
    {
        node.refresh(keepChanges);
    }

    /**
     * @param name
     * @param value
     * @return
     * @throws ValueFormatException
     * @throws VersionException
     * @throws LockException
     * @throws ConstraintViolationException
     * @throws RepositoryException
     * @see javax.jcr.Node#setProperty(java.lang.String, javax.jcr.Value)
     */
    public Property setProperty(String name, Value value) throws ValueFormatException, VersionException, LockException,
        ConstraintViolationException, RepositoryException
    {
        return node.setProperty(name, value);
    }

    /**
     * @throws VersionException
     * @throws LockException
     * @throws ConstraintViolationException
     * @throws AccessDeniedException
     * @throws RepositoryException
     * @see javax.jcr.Item#remove()
     */
    public void remove() throws VersionException, LockException, ConstraintViolationException,
        javax.jcr.AccessDeniedException, RepositoryException
    {
        node.remove();
    }

    /**
     * @param name
     * @param value
     * @param type
     * @return
     * @throws ValueFormatException
     * @throws VersionException
     * @throws LockException
     * @throws ConstraintViolationException
     * @throws RepositoryException
     * @see javax.jcr.Node#setProperty(java.lang.String, javax.jcr.Value, int)
     */
    public Property setProperty(String name, Value value, int type) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException
    {
        return node.setProperty(name, value, type);
    }

    /**
     * @param name
     * @param values
     * @return
     * @throws ValueFormatException
     * @throws VersionException
     * @throws LockException
     * @throws ConstraintViolationException
     * @throws RepositoryException
     * @see javax.jcr.Node#setProperty(java.lang.String, javax.jcr.Value[])
     */
    public Property setProperty(String name, Value[] values) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException
    {
        return node.setProperty(name, values);
    }

    /**
     * @param name
     * @param values
     * @param type
     * @return
     * @throws ValueFormatException
     * @throws VersionException
     * @throws LockException
     * @throws ConstraintViolationException
     * @throws RepositoryException
     * @see javax.jcr.Node#setProperty(java.lang.String, javax.jcr.Value[], int)
     */
    public Property setProperty(String name, Value[] values, int type) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException
    {
        return node.setProperty(name, values, type);
    }

    /**
     * @param name
     * @param values
     * @return
     * @throws ValueFormatException
     * @throws VersionException
     * @throws LockException
     * @throws ConstraintViolationException
     * @throws RepositoryException
     * @see javax.jcr.Node#setProperty(java.lang.String, java.lang.String[])
     */
    public Property setProperty(String name, String[] values) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException
    {
        return node.setProperty(name, values);
    }

    /**
     * @param name
     * @param values
     * @param type
     * @return
     * @throws ValueFormatException
     * @throws VersionException
     * @throws LockException
     * @throws ConstraintViolationException
     * @throws RepositoryException
     * @see javax.jcr.Node#setProperty(java.lang.String, java.lang.String[], int)
     */
    public Property setProperty(String name, String[] values, int type) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException
    {
        return node.setProperty(name, values, type);
    }

    /**
     * @param name
     * @param value
     * @return
     * @throws ValueFormatException
     * @throws VersionException
     * @throws LockException
     * @throws ConstraintViolationException
     * @throws RepositoryException
     * @see javax.jcr.Node#setProperty(java.lang.String, java.lang.String)
     */
    public Property setProperty(String name, String value) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException
    {
        return node.setProperty(name, value);
    }

    /**
     * @param name
     * @param value
     * @param type
     * @return
     * @throws ValueFormatException
     * @throws VersionException
     * @throws LockException
     * @throws ConstraintViolationException
     * @throws RepositoryException
     * @see javax.jcr.Node#setProperty(java.lang.String, java.lang.String, int)
     */
    public Property setProperty(String name, String value, int type) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException
    {
        return node.setProperty(name, value, type);
    }

    /**
     * @param name
     * @param value
     * @return
     * @throws ValueFormatException
     * @throws VersionException
     * @throws LockException
     * @throws ConstraintViolationException
     * @throws RepositoryException
     * @deprecated
     * @see javax.jcr.Node#setProperty(java.lang.String, java.io.InputStream)
     */
    @Deprecated
    public Property setProperty(String name, InputStream value) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException
    {
        return node.setProperty(name, value);
    }

    /**
     * @param name
     * @param value
     * @return
     * @throws ValueFormatException
     * @throws VersionException
     * @throws LockException
     * @throws ConstraintViolationException
     * @throws RepositoryException
     * @see javax.jcr.Node#setProperty(java.lang.String, javax.jcr.Binary)
     */
    public Property setProperty(String name, Binary value) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException
    {
        return node.setProperty(name, value);
    }

    /**
     * @param name
     * @param value
     * @return
     * @throws ValueFormatException
     * @throws VersionException
     * @throws LockException
     * @throws ConstraintViolationException
     * @throws RepositoryException
     * @see javax.jcr.Node#setProperty(java.lang.String, boolean)
     */
    public Property setProperty(String name, boolean value) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException
    {
        return node.setProperty(name, value);
    }

    /**
     * @param name
     * @param value
     * @return
     * @throws ValueFormatException
     * @throws VersionException
     * @throws LockException
     * @throws ConstraintViolationException
     * @throws RepositoryException
     * @see javax.jcr.Node#setProperty(java.lang.String, double)
     */
    public Property setProperty(String name, double value) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException
    {
        return node.setProperty(name, value);
    }

    /**
     * @param name
     * @param value
     * @return
     * @throws ValueFormatException
     * @throws VersionException
     * @throws LockException
     * @throws ConstraintViolationException
     * @throws RepositoryException
     * @see javax.jcr.Node#setProperty(java.lang.String, java.math.BigDecimal)
     */
    public Property setProperty(String name, BigDecimal value) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException
    {
        return node.setProperty(name, value);
    }

    /**
     * @param name
     * @param value
     * @return
     * @throws ValueFormatException
     * @throws VersionException
     * @throws LockException
     * @throws ConstraintViolationException
     * @throws RepositoryException
     * @see javax.jcr.Node#setProperty(java.lang.String, long)
     */
    public Property setProperty(String name, long value) throws ValueFormatException, VersionException, LockException,
        ConstraintViolationException, RepositoryException
    {
        return node.setProperty(name, value);
    }

    /**
     * @param name
     * @param value
     * @return
     * @throws ValueFormatException
     * @throws VersionException
     * @throws LockException
     * @throws ConstraintViolationException
     * @throws RepositoryException
     * @see javax.jcr.Node#setProperty(java.lang.String, java.util.Calendar)
     */
    public Property setProperty(String name, Calendar value) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException
    {
        return node.setProperty(name, value);
    }

    /**
     * @param name
     * @param value
     * @return
     * @throws ValueFormatException
     * @throws VersionException
     * @throws LockException
     * @throws ConstraintViolationException
     * @throws RepositoryException
     * @see javax.jcr.Node#setProperty(java.lang.String, javax.jcr.Node)
     */
    public Property setProperty(String name, Node value) throws ValueFormatException, VersionException, LockException,
        ConstraintViolationException, RepositoryException
    {
        return node.setProperty(name, value);
    }

    /**
     * @param relPath
     * @return
     * @throws PathNotFoundException
     * @throws RepositoryException
     * @see javax.jcr.Node#getNode(java.lang.String)
     */
    public Node getNode(String relPath) throws PathNotFoundException, RepositoryException
    {
        return node.getNode(relPath);
    }

    /**
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Node#getNodes()
     */
    public NodeIterator getNodes() throws RepositoryException
    {
        return node.getNodes();
    }

    /**
     * @param namePattern
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Node#getNodes(java.lang.String)
     */
    public NodeIterator getNodes(String namePattern) throws RepositoryException
    {
        return node.getNodes(namePattern);
    }

    /**
     * @param nameGlobs
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Node#getNodes(java.lang.String[])
     */
    public NodeIterator getNodes(String[] nameGlobs) throws RepositoryException
    {
        return node.getNodes(nameGlobs);
    }

    /**
     * @param relPath
     * @return
     * @throws PathNotFoundException
     * @throws RepositoryException
     * @see javax.jcr.Node#getProperty(java.lang.String)
     */
    public Property getProperty(String relPath) throws PathNotFoundException, RepositoryException
    {
        return node.getProperty(relPath);
    }

    /**
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Node#getProperties()
     */
    public PropertyIterator getProperties() throws RepositoryException
    {
        return node.getProperties();
    }

    /**
     * @param namePattern
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Node#getProperties(java.lang.String)
     */
    public PropertyIterator getProperties(String namePattern) throws RepositoryException
    {
        return node.getProperties(namePattern);
    }

    /**
     * @param nameGlobs
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Node#getProperties(java.lang.String[])
     */
    public PropertyIterator getProperties(String[] nameGlobs) throws RepositoryException
    {
        return node.getProperties(nameGlobs);
    }

    /**
     * @return
     * @throws ItemNotFoundException
     * @throws RepositoryException
     * @see javax.jcr.Node#getPrimaryItem()
     */
    public Item getPrimaryItem() throws ItemNotFoundException, RepositoryException
    {
        return node.getPrimaryItem();
    }

    /**
     * @return
     * @throws UnsupportedRepositoryOperationException
     * @throws RepositoryException
     * @deprecated
     * @see javax.jcr.Node#getUUID()
     */
    @Deprecated
    public String getUUID() throws UnsupportedRepositoryOperationException, RepositoryException
    {
        return node.getUUID();
    }

    /**
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Node#getIdentifier()
     */
    public String getIdentifier() throws RepositoryException
    {
        return node.getIdentifier();
    }

    /**
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Node#getIndex()
     */
    public int getIndex() throws RepositoryException
    {
        return node.getIndex();
    }

    /**
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Node#getReferences()
     */
    public PropertyIterator getReferences() throws RepositoryException
    {
        return node.getReferences();
    }

    /**
     * @param name
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Node#getReferences(java.lang.String)
     */
    public PropertyIterator getReferences(String name) throws RepositoryException
    {
        return node.getReferences(name);
    }

    /**
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Node#getWeakReferences()
     */
    public PropertyIterator getWeakReferences() throws RepositoryException
    {
        return node.getWeakReferences();
    }

    /**
     * @param name
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Node#getWeakReferences(java.lang.String)
     */
    public PropertyIterator getWeakReferences(String name) throws RepositoryException
    {
        return node.getWeakReferences(name);
    }

    /**
     * @param relPath
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Node#hasNode(java.lang.String)
     */
    public boolean hasNode(String relPath) throws RepositoryException
    {
        return node.hasNode(relPath);
    }

    /**
     * @param relPath
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Node#hasProperty(java.lang.String)
     */
    public boolean hasProperty(String relPath) throws RepositoryException
    {
        return node.hasProperty(relPath);
    }

    /**
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Node#hasNodes()
     */
    public boolean hasNodes() throws RepositoryException
    {
        return node.hasNodes();
    }

    /**
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Node#hasProperties()
     */
    public boolean hasProperties() throws RepositoryException
    {
        return node.hasProperties();
    }

    /**
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Node#getPrimaryNodeType()
     */
    public NodeType getPrimaryNodeType() throws RepositoryException
    {
        return node.getPrimaryNodeType();
    }

    /**
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Node#getMixinNodeTypes()
     */
    public NodeType[] getMixinNodeTypes() throws RepositoryException
    {
        return node.getMixinNodeTypes();
    }

    /**
     * @param nodeTypeName
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Node#isNodeType(java.lang.String)
     */
    public boolean isNodeType(String nodeTypeName) throws RepositoryException
    {
        return node.isNodeType(nodeTypeName);
    }

    /**
     * @param nodeTypeName
     * @throws NoSuchNodeTypeException
     * @throws VersionException
     * @throws ConstraintViolationException
     * @throws LockException
     * @throws RepositoryException
     * @see javax.jcr.Node#setPrimaryType(java.lang.String)
     */
    public void setPrimaryType(String nodeTypeName) throws NoSuchNodeTypeException, VersionException,
        ConstraintViolationException, LockException, RepositoryException
    {
        node.setPrimaryType(nodeTypeName);
    }

    /**
     * @param mixinName
     * @throws NoSuchNodeTypeException
     * @throws VersionException
     * @throws ConstraintViolationException
     * @throws LockException
     * @throws RepositoryException
     * @see javax.jcr.Node#addMixin(java.lang.String)
     */
    public void addMixin(String mixinName) throws NoSuchNodeTypeException, VersionException,
        ConstraintViolationException, LockException, RepositoryException
    {
        node.addMixin(mixinName);
    }

    /**
     * @param mixinName
     * @throws NoSuchNodeTypeException
     * @throws VersionException
     * @throws ConstraintViolationException
     * @throws LockException
     * @throws RepositoryException
     * @see javax.jcr.Node#removeMixin(java.lang.String)
     */
    public void removeMixin(String mixinName) throws NoSuchNodeTypeException, VersionException,
        ConstraintViolationException, LockException, RepositoryException
    {
        node.removeMixin(mixinName);
    }

    /**
     * @param mixinName
     * @return
     * @throws NoSuchNodeTypeException
     * @throws RepositoryException
     * @see javax.jcr.Node#canAddMixin(java.lang.String)
     */
    public boolean canAddMixin(String mixinName) throws NoSuchNodeTypeException, RepositoryException
    {
        return node.canAddMixin(mixinName);
    }

    /**
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Node#getDefinition()
     */
    public NodeDefinition getDefinition() throws RepositoryException
    {
        return node.getDefinition();
    }

    /**
     * @return
     * @throws VersionException
     * @throws UnsupportedRepositoryOperationException
     * @throws InvalidItemStateException
     * @throws LockException
     * @throws RepositoryException
     * @deprecated
     * @see javax.jcr.Node#checkin()
     */
    @Deprecated
    public Version checkin() throws VersionException, UnsupportedRepositoryOperationException,
        InvalidItemStateException, LockException, RepositoryException
    {
        return node.checkin();
    }

    /**
     * @throws UnsupportedRepositoryOperationException
     * @throws LockException
     * @throws ActivityViolationException
     * @throws RepositoryException
     * @deprecated
     * @see javax.jcr.Node#checkout()
     */
    @Deprecated
    public void checkout() throws UnsupportedRepositoryOperationException, LockException, ActivityViolationException,
        RepositoryException
    {
        node.checkout();
    }

    /**
     * @param version
     * @throws VersionException
     * @throws InvalidItemStateException
     * @throws UnsupportedRepositoryOperationException
     * @throws RepositoryException
     * @deprecated
     * @see javax.jcr.Node#doneMerge(javax.jcr.version.Version)
     */
    @Deprecated
    public void doneMerge(Version version) throws VersionException, InvalidItemStateException,
        UnsupportedRepositoryOperationException, RepositoryException
    {
        node.doneMerge(version);
    }

    /**
     * @param version
     * @throws VersionException
     * @throws InvalidItemStateException
     * @throws UnsupportedRepositoryOperationException
     * @throws RepositoryException
     * @deprecated
     * @see javax.jcr.Node#cancelMerge(javax.jcr.version.Version)
     */
    @Deprecated
    public void cancelMerge(Version version) throws VersionException, InvalidItemStateException,
        UnsupportedRepositoryOperationException, RepositoryException
    {
        node.cancelMerge(version);
    }

    /**
     * @param srcWorkspace
     * @throws NoSuchWorkspaceException
     * @throws AccessDeniedException
     * @throws LockException
     * @throws InvalidItemStateException
     * @throws RepositoryException
     * @see javax.jcr.Node#update(java.lang.String)
     */
    public void update(String srcWorkspace) throws NoSuchWorkspaceException, javax.jcr.AccessDeniedException,
        LockException, InvalidItemStateException, RepositoryException
    {
        node.update(srcWorkspace);
    }

    /**
     * @param srcWorkspace
     * @param bestEffort
     * @return
     * @throws NoSuchWorkspaceException
     * @throws AccessDeniedException
     * @throws MergeException
     * @throws LockException
     * @throws InvalidItemStateException
     * @throws RepositoryException
     * @deprecated
     * @see javax.jcr.Node#merge(java.lang.String, boolean)
     */
    @Deprecated
    public NodeIterator merge(String srcWorkspace, boolean bestEffort) throws NoSuchWorkspaceException,
        javax.jcr.AccessDeniedException, MergeException, LockException, InvalidItemStateException, RepositoryException
    {
        return node.merge(srcWorkspace, bestEffort);
    }

    /**
     * @param workspaceName
     * @return
     * @throws ItemNotFoundException
     * @throws NoSuchWorkspaceException
     * @throws AccessDeniedException
     * @throws RepositoryException
     * @see javax.jcr.Node#getCorrespondingNodePath(java.lang.String)
     */
    public String getCorrespondingNodePath(String workspaceName) throws ItemNotFoundException,
        NoSuchWorkspaceException, javax.jcr.AccessDeniedException, RepositoryException
    {
        return node.getCorrespondingNodePath(workspaceName);
    }

    /**
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Node#getSharedSet()
     */
    public NodeIterator getSharedSet() throws RepositoryException
    {
        return node.getSharedSet();
    }

    /**
     * @throws VersionException
     * @throws LockException
     * @throws ConstraintViolationException
     * @throws RepositoryException
     * @see javax.jcr.Node#removeSharedSet()
     */
    public void removeSharedSet() throws VersionException, LockException, ConstraintViolationException,
        RepositoryException
    {
        node.removeSharedSet();
    }

    /**
     * @throws VersionException
     * @throws LockException
     * @throws ConstraintViolationException
     * @throws RepositoryException
     * @see javax.jcr.Node#removeShare()
     */
    public void removeShare() throws VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        node.removeShare();
    }

    /**
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Node#isCheckedOut()
     */
    public boolean isCheckedOut() throws RepositoryException
    {
        return node.isCheckedOut();
    }

    /**
     * @param versionName
     * @param removeExisting
     * @throws VersionException
     * @throws ItemExistsException
     * @throws UnsupportedRepositoryOperationException
     * @throws LockException
     * @throws InvalidItemStateException
     * @throws RepositoryException
     * @deprecated
     * @see javax.jcr.Node#restore(java.lang.String, boolean)
     */
    @Deprecated
    public void restore(String versionName, boolean removeExisting) throws VersionException, ItemExistsException,
        UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException
    {
        node.restore(versionName, removeExisting);
    }

    /**
     * @param version
     * @param removeExisting
     * @throws VersionException
     * @throws ItemExistsException
     * @throws InvalidItemStateException
     * @throws UnsupportedRepositoryOperationException
     * @throws LockException
     * @throws RepositoryException
     * @deprecated
     * @see javax.jcr.Node#restore(javax.jcr.version.Version, boolean)
     */
    @Deprecated
    public void restore(Version version, boolean removeExisting) throws VersionException, ItemExistsException,
        InvalidItemStateException, UnsupportedRepositoryOperationException, LockException, RepositoryException
    {
        node.restore(version, removeExisting);
    }

    /**
     * @param version
     * @param relPath
     * @param removeExisting
     * @throws PathNotFoundException
     * @throws ItemExistsException
     * @throws VersionException
     * @throws ConstraintViolationException
     * @throws UnsupportedRepositoryOperationException
     * @throws LockException
     * @throws InvalidItemStateException
     * @throws RepositoryException
     * @deprecated
     * @see javax.jcr.Node#restore(javax.jcr.version.Version, java.lang.String, boolean)
     */
    @Deprecated
    public void restore(Version version, String relPath, boolean removeExisting) throws PathNotFoundException,
        ItemExistsException, VersionException, ConstraintViolationException, UnsupportedRepositoryOperationException,
        LockException, InvalidItemStateException, RepositoryException
    {
        node.restore(version, relPath, removeExisting);
    }

    /**
     * @param versionLabel
     * @param removeExisting
     * @throws VersionException
     * @throws ItemExistsException
     * @throws UnsupportedRepositoryOperationException
     * @throws LockException
     * @throws InvalidItemStateException
     * @throws RepositoryException
     * @deprecated
     * @see javax.jcr.Node#restoreByLabel(java.lang.String, boolean)
     */
    @Deprecated
    public void restoreByLabel(String versionLabel, boolean removeExisting) throws VersionException,
        ItemExistsException, UnsupportedRepositoryOperationException, LockException, InvalidItemStateException,
        RepositoryException
    {
        node.restoreByLabel(versionLabel, removeExisting);
    }

    /**
     * @return
     * @throws UnsupportedRepositoryOperationException
     * @throws RepositoryException
     * @deprecated
     * @see javax.jcr.Node#getVersionHistory()
     */
    @Deprecated
    public VersionHistory getVersionHistory() throws UnsupportedRepositoryOperationException, RepositoryException
    {
        return node.getVersionHistory();
    }

    /**
     * @return
     * @throws UnsupportedRepositoryOperationException
     * @throws RepositoryException
     * @deprecated
     * @see javax.jcr.Node#getBaseVersion()
     */
    @Deprecated
    public Version getBaseVersion() throws UnsupportedRepositoryOperationException, RepositoryException
    {
        return node.getBaseVersion();
    }

    /**
     * @param isDeep
     * @param isSessionScoped
     * @return
     * @throws UnsupportedRepositoryOperationException
     * @throws LockException
     * @throws AccessDeniedException
     * @throws InvalidItemStateException
     * @throws RepositoryException
     * @deprecated
     * @see javax.jcr.Node#lock(boolean, boolean)
     */
    @Deprecated
    public Lock lock(boolean isDeep, boolean isSessionScoped) throws UnsupportedRepositoryOperationException,
        LockException, javax.jcr.AccessDeniedException, InvalidItemStateException, RepositoryException
    {
        return node.lock(isDeep, isSessionScoped);
    }

    /**
     * @return
     * @throws UnsupportedRepositoryOperationException
     * @throws LockException
     * @throws AccessDeniedException
     * @throws RepositoryException
     * @deprecated
     * @see javax.jcr.Node#getLock()
     */
    @Deprecated
    public Lock getLock() throws UnsupportedRepositoryOperationException, LockException,
        javax.jcr.AccessDeniedException, RepositoryException
    {
        return node.getLock();
    }

    /**
     * @throws UnsupportedRepositoryOperationException
     * @throws LockException
     * @throws AccessDeniedException
     * @throws InvalidItemStateException
     * @throws RepositoryException
     * @deprecated
     * @see javax.jcr.Node#unlock()
     */
    @Deprecated
    public void unlock() throws UnsupportedRepositoryOperationException, LockException,
        javax.jcr.AccessDeniedException, InvalidItemStateException, RepositoryException
    {
        node.unlock();
    }

    /**
     * @return
     * @throws RepositoryException
     * @deprecated
     * @see javax.jcr.Node#holdsLock()
     */
    @Deprecated
    public boolean holdsLock() throws RepositoryException
    {
        return node.holdsLock();
    }

    /**
     * @return
     * @throws RepositoryException
     * @see javax.jcr.Node#isLocked()
     */
    public boolean isLocked() throws RepositoryException
    {
        return node.isLocked();
    }

    /**
     * @param transition
     * @throws UnsupportedRepositoryOperationException
     * @throws InvalidLifecycleTransitionException
     * @throws RepositoryException
     * @see javax.jcr.Node#followLifecycleTransition(java.lang.String)
     */
    public void followLifecycleTransition(String transition) throws UnsupportedRepositoryOperationException,
        InvalidLifecycleTransitionException, RepositoryException
    {
        node.followLifecycleTransition(transition);
    }

    /**
     * @return
     * @throws UnsupportedRepositoryOperationException
     * @throws RepositoryException
     * @see javax.jcr.Node#getAllowedLifecycleTransistions()
     */
    public String[] getAllowedLifecycleTransistions() throws UnsupportedRepositoryOperationException,
        RepositoryException
    {
        return node.getAllowedLifecycleTransistions();
    }

    public String getTitle()
    {
        try
        {
            if (node.hasProperty("title"))
            {
                return node.getProperty("title").getString();
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
            return node.getPath();
        }
        catch (RepositoryException e)
        {
            throw new RuntimeRepositoryException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(Object key)
    {
        if (key == null)
        {
            return null;
        }

        String keystr = ObjectUtils.toString(key);

        if (StringUtils.equals(keystr, "handle"))
        {
            keystr = "@path";
        }

        Object result = super.get(keystr);

        if (result == null)
        {
            try
            {
                return PropertyUtils.getProperty(this, keystr);
            }
            catch (Throwable e)
            {
                // ignore
            }
        }

        return result;
    }

}
