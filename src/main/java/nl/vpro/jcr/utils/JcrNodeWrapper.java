package nl.vpro.jcr.utils;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;

import javax.jcr.*;
import javax.jcr.lock.Lock;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;

/**
 * @author Michiel Meeuwissen
 */
public abstract class JcrNodeWrapper implements Node {

    protected abstract Node getNode() throws RepositoryException;

    @Override
    public Node addNode(String relPath) throws RepositoryException {
        return getNode().addNode(relPath);
    }

    @Override
    public Node addNode(String relPath, String primaryNodeTypeName) throws RepositoryException {
        return getNode().addNode(relPath, primaryNodeTypeName);
    }

    @Override
    public void orderBefore(String srcChildRelPath, String destChildRelPath) throws RepositoryException {
        getNode().orderBefore(srcChildRelPath, destChildRelPath);
    }

    @Override
    public Property setProperty(String name, Value value) throws RepositoryException {
        return getNode().setProperty(name, value);
    }

    @Override
    public Property setProperty(String name, Value value, int type) throws RepositoryException {
        return getNode().setProperty(name, value, type);
    }

    @Override
    public Property setProperty(String name, Value[] values) throws RepositoryException {
        return getNode().setProperty(name, values);
    }

    @Override
    public Property setProperty(String name, Value[] values, int type) throws RepositoryException {
        return getNode().setProperty(name, values, type);
    }

    @Override
    public Property setProperty(String name, String[] values) throws RepositoryException {
        return getNode().setProperty(name, values);
    }

    @Override
    public Property setProperty(String name, String[] values, int type) throws RepositoryException {
        return getNode().setProperty(name, values, type);
    }

    @Override
    public Property setProperty(String name, String value) throws RepositoryException {
        return getNode().setProperty(name, value);
    }

    @Override
    public Property setProperty(String name, String value, int type) throws RepositoryException {
        return getNode().setProperty(name, value, type);
    }

    @Override
    @Deprecated
    public Property setProperty(String name, InputStream value) throws RepositoryException {
        return getNode().setProperty(name, value);
    }

    @Override
    public Property setProperty(String name, Binary value) throws RepositoryException {
        return getNode().setProperty(name, value);
    }

    @Override
    public Property setProperty(String name, boolean value) throws RepositoryException {
        return getNode().setProperty(name, value);
    }

    @Override
    public Property setProperty(String name, double value) throws RepositoryException {
        return getNode().setProperty(name, value);
    }

    @Override
    public Property setProperty(String name, BigDecimal value) throws RepositoryException {
        return getNode().setProperty(name, value);
    }

    @Override
    public Property setProperty(String name, long value) throws RepositoryException {
        return getNode().setProperty(name, value);
    }

    @Override
    public Property setProperty(String name, Calendar value) throws RepositoryException {
        return getNode().setProperty(name, value);
    }

    @Override
    public Property setProperty(String name, Node value) throws RepositoryException {
        return getNode().setProperty(name, value);
    }

    @Override
    public Node getNode(String relPath) throws RepositoryException {
        return getNode().getNode(relPath);
    }

    @Override
    public NodeIterator getNodes() throws RepositoryException {
        return getNode().getNodes();
    }

    @Override
    public NodeIterator getNodes(String namePattern) throws RepositoryException {
        return getNode().getNodes(namePattern);
    }

    @Override
    public NodeIterator getNodes(String[] nameGlobs) throws RepositoryException {
        return getNode().getNodes(nameGlobs);
    }

    @Override
    public Property getProperty(String relPath) throws RepositoryException {
        return getNode().getProperty(relPath);
    }

    @Override
    public PropertyIterator getProperties() throws RepositoryException {
        return getNode().getProperties();
    }

    @Override
    public PropertyIterator getProperties(String namePattern) throws RepositoryException {
        return getNode().getProperties(namePattern);
    }

    @Override
    public PropertyIterator getProperties(String[] nameGlobs) throws RepositoryException {
        return getNode().getProperties(nameGlobs);
    }

    @Override
    public Item getPrimaryItem() throws RepositoryException {
        return getNode().getPrimaryItem();
    }

    @Override
    @Deprecated
    public String getUUID() throws RepositoryException {
        return getNode().getUUID();
    }

    @Override
    public String getIdentifier() throws RepositoryException {
        return getNode().getIdentifier();
    }

    @Override
    public int getIndex() throws RepositoryException {
        return getNode().getIndex();
    }

    @Override
    public PropertyIterator getReferences() throws RepositoryException {
        return getNode().getReferences();
    }

    @Override
    public PropertyIterator getReferences(String name) throws RepositoryException {
        return getNode().getReferences(name);
    }

    @Override
    public PropertyIterator getWeakReferences() throws RepositoryException {
        return getNode().getWeakReferences();
    }

    @Override
    public PropertyIterator getWeakReferences(String name) throws RepositoryException {
        return getNode().getWeakReferences(name);
    }

    @Override
    public boolean hasNode(String relPath) throws RepositoryException {
        return getNode().hasNode(relPath);
    }

    @Override
    public boolean hasProperty(String relPath) throws RepositoryException {
        return getNode().hasProperty(relPath);
    }

    @Override
    public boolean hasNodes() throws RepositoryException {
        return getNode().hasNodes();
    }

    @Override
    public boolean hasProperties() throws RepositoryException {
        return getNode().hasProperties();
    }

    @Override
    public NodeType getPrimaryNodeType() throws RepositoryException {
        return getNode().getPrimaryNodeType();
    }

    @Override
    public NodeType[] getMixinNodeTypes() throws RepositoryException {
        return getNode().getMixinNodeTypes();
    }

    @Override
    public boolean isNodeType(String nodeTypeName) throws RepositoryException {
        return getNode().isNodeType(nodeTypeName);
    }

    @Override
    public void setPrimaryType(String nodeTypeName) throws RepositoryException {
        getNode().setPrimaryType(nodeTypeName);
    }

    @Override
    public void addMixin(String mixinName) throws RepositoryException {
        getNode().addMixin(mixinName);
    }

    @Override
    public void removeMixin(String mixinName) throws RepositoryException {
        getNode().removeMixin(mixinName);
    }

    @Override
    public boolean canAddMixin(String mixinName) throws RepositoryException {
        return getNode().canAddMixin(mixinName);
    }

    @Override
    public NodeDefinition getDefinition() throws RepositoryException {
        return getNode().getDefinition();
    }

    @Override
    @Deprecated
    public Version checkin() throws RepositoryException {
        return getNode().checkin();
    }

    @Override
    @Deprecated
    public void checkout() throws RepositoryException {
        getNode().checkout();
    }

    @Override
    @Deprecated
    public void doneMerge(Version version) throws RepositoryException {
        getNode().doneMerge(version);
    }

    @Override
    @Deprecated
    public void cancelMerge(Version version) throws RepositoryException {
        getNode().cancelMerge(version);
    }

    @Override
    public void update(String srcWorkspace) throws RepositoryException {
        getNode().update(srcWorkspace);
    }

    @Override
    @Deprecated
    public NodeIterator merge(String srcWorkspace, boolean bestEffort) throws RepositoryException {
        return getNode().merge(srcWorkspace, bestEffort);
    }

    @Override
    public String getCorrespondingNodePath(String workspaceName) throws RepositoryException {
        return getNode().getCorrespondingNodePath(workspaceName);
    }

    @Override
    public NodeIterator getSharedSet() throws RepositoryException {
        return getNode().getSharedSet();
    }

    @Override
    public void removeSharedSet() throws RepositoryException {
        getNode().removeSharedSet();
    }

    @Override
    public void removeShare() throws RepositoryException {
        getNode().removeShare();
    }

    @Override
    public boolean isCheckedOut() throws RepositoryException {
        return getNode().isCheckedOut();
    }

    @Override
    @Deprecated
    public void restore(String versionName, boolean removeExisting) throws RepositoryException {
        getNode().restore(versionName, removeExisting);
    }

    @Override
    @Deprecated
    public void restore(Version version, boolean removeExisting) throws RepositoryException {
        getNode().restore(version, removeExisting);
    }

    @Override
    @Deprecated
    public void restore(Version version, String relPath, boolean removeExisting) throws RepositoryException {
        getNode().restore(version, relPath, removeExisting);
    }

    @Override
    @Deprecated
    public void restoreByLabel(String versionLabel, boolean removeExisting) throws RepositoryException {
        getNode().restoreByLabel(versionLabel, removeExisting);
    }

    @Override
    @Deprecated
    public VersionHistory getVersionHistory() throws RepositoryException {
        return getNode().getVersionHistory();
    }

    @Override
    @Deprecated
    public Version getBaseVersion() throws RepositoryException {
        return getNode().getBaseVersion();
    }

    @Override
    @Deprecated
    public Lock lock(boolean isDeep, boolean isSessionScoped) throws RepositoryException {
        return getNode().lock(isDeep, isSessionScoped);
    }

    @Override
    @Deprecated
    public Lock getLock() throws RepositoryException {
        return getNode().getLock();
    }

    @Override
    @Deprecated
    public void unlock() throws RepositoryException {
        getNode().unlock();
    }

    @Override
    @Deprecated
    public boolean holdsLock() throws RepositoryException {
        return getNode().holdsLock();
    }

    @Override
    public boolean isLocked() throws RepositoryException {
        return getNode().isLocked();
    }

    @Override
    public void followLifecycleTransition(String transition) throws RepositoryException {
        getNode().followLifecycleTransition(transition);
    }

    @Override
    public String[] getAllowedLifecycleTransistions() throws RepositoryException {
        return getNode().getAllowedLifecycleTransistions();
    }

    @Override
    public String getPath() throws RepositoryException {
        return getNode().getPath();
    }

    @Override
    public String getName() throws RepositoryException {
        return getNode().getName();
    }

    @Override
    public Item getAncestor(int depth) throws RepositoryException {
        return getNode().getAncestor(depth);
    }

    @Override
    public Node getParent() throws RepositoryException {
        return getNode().getParent();
    }

    @Override
    public int getDepth() throws RepositoryException {
        return getNode().getDepth();
    }

    @Override
    public Session getSession() throws RepositoryException {
        return getNode().getSession();
    }

    @Override
    public boolean isNode() {
        try {
            return getNode().isNode();
        } catch (RepositoryException re) {
            return false;
        }
    }

    @Override
    public boolean isNew() {
        try {
            return getNode().isNew();
        } catch(RepositoryException re) {
            return false;
        }
    }

    @Override
    public boolean isModified() {
        try {
            return getNode().isModified();
        } catch(RepositoryException re) {
            return false;
        }
    }

    @Override
    public boolean isSame(Item otherItem) throws RepositoryException {
        return getNode().isSame(otherItem);
    }

    @Override
    public void accept(ItemVisitor visitor) throws RepositoryException {
        getNode().accept(visitor);
    }

    @Override
    @Deprecated
    public void save() throws RepositoryException {
        getNode().save();
    }

    @Override
    public void refresh(boolean keepChanges) throws RepositoryException {
        getNode().refresh(keepChanges);
    }

    @Override
    public void remove() throws RepositoryException {
        getNode().remove();
    }
}
