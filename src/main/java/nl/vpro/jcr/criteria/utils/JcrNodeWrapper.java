package nl.vpro.jcr.criteria.utils;

import javax.jcr.*;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.ActivityViolationException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionHistory;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @author Michiel Meeuwissen
 */
public abstract class JcrNodeWrapper implements Node {

	protected abstract Node getNode() throws RepositoryException;

	@Override
	public Node addNode(String relPath) throws ItemExistsException, PathNotFoundException, VersionException, ConstraintViolationException, LockException, RepositoryException {
		return getNode().addNode(relPath);
	}

	@Override
	public Node addNode(String relPath, String primaryNodeTypeName) throws ItemExistsException, PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException, ConstraintViolationException, RepositoryException {
		return getNode().addNode(relPath, primaryNodeTypeName);
	}

	@Override
	public void orderBefore(String srcChildRelPath, String destChildRelPath) throws UnsupportedRepositoryOperationException, VersionException, ConstraintViolationException, ItemNotFoundException, LockException, RepositoryException {
		getNode().orderBefore(srcChildRelPath, destChildRelPath);
	}

	@Override
	public Property setProperty(String name, Value value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		return getNode().setProperty(name, value);
	}

	@Override
	public Property setProperty(String name, Value value, int type) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		return getNode().setProperty(name, value, type);
	}

	@Override
	public Property setProperty(String name, Value[] values) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		return getNode().setProperty(name, values);
	}

	@Override
	public Property setProperty(String name, Value[] values, int type) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		return getNode().setProperty(name, values, type);
	}

	@Override
	public Property setProperty(String name, String[] values) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		return getNode().setProperty(name, values);
	}

	@Override
	public Property setProperty(String name, String[] values, int type) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		return getNode().setProperty(name, values, type);
	}

	@Override
	public Property setProperty(String name, String value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		return getNode().setProperty(name, value);
	}

	@Override
	public Property setProperty(String name, String value, int type) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		return getNode().setProperty(name, value, type);
	}

	@Override
	public Property setProperty(String name, InputStream value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		return getNode().setProperty(name, value);
	}

	@Override
	public Property setProperty(String name, Binary value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		return getNode().setProperty(name, value);
	}

	@Override
	public Property setProperty(String name, boolean value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		return getNode().setProperty(name, value);
	}

	@Override
	public Property setProperty(String name, double value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		return getNode().setProperty(name, value);
	}

	@Override
	public Property setProperty(String name, BigDecimal value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		return getNode().setProperty(name, value);
	}

	@Override
	public Property setProperty(String name, long value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		return getNode().setProperty(name, value);
	}

	@Override
	public Property setProperty(String name, Calendar value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		return getNode().setProperty(name, value);
	}

	@Override
	public Property setProperty(String name, Node value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		return getNode().setProperty(name, value);
	}

	@Override
	public Node getNode(String relPath) throws PathNotFoundException, RepositoryException {
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
	public Property getProperty(String relPath) throws PathNotFoundException, RepositoryException {
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
	public Item getPrimaryItem() throws ItemNotFoundException, RepositoryException {
		return getNode().getPrimaryItem();
	}

	@Override
	public String getUUID() throws UnsupportedRepositoryOperationException, RepositoryException {
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
	public void setPrimaryType(String nodeTypeName) throws NoSuchNodeTypeException, VersionException, ConstraintViolationException, LockException, RepositoryException {
		getNode().setPrimaryType(nodeTypeName);
	}

	@Override
	public void addMixin(String mixinName) throws NoSuchNodeTypeException, VersionException, ConstraintViolationException, LockException, RepositoryException {
		getNode().addMixin(mixinName);
	}

	@Override
	public void removeMixin(String mixinName) throws NoSuchNodeTypeException, VersionException, ConstraintViolationException, LockException, RepositoryException {
		getNode().removeMixin(mixinName);
	}

	@Override
	public boolean canAddMixin(String mixinName) throws NoSuchNodeTypeException, RepositoryException {
		return getNode().canAddMixin(mixinName);
	}

	@Override
	public NodeDefinition getDefinition() throws RepositoryException {
		return getNode().getDefinition();
	}

	@Override
	public Version checkin() throws VersionException, UnsupportedRepositoryOperationException, InvalidItemStateException, LockException, RepositoryException {
		return getNode().checkin();
	}

	@Override
	public void checkout() throws UnsupportedRepositoryOperationException, LockException, ActivityViolationException, RepositoryException {
		getNode().checkout();
	}

	@Override
	public void doneMerge(Version version) throws VersionException, InvalidItemStateException, UnsupportedRepositoryOperationException, RepositoryException {
		getNode().doneMerge(version);
	}

	@Override
	public void cancelMerge(Version version) throws VersionException, InvalidItemStateException, UnsupportedRepositoryOperationException, RepositoryException {
		getNode().cancelMerge(version);
	}

	@Override
	public void update(String srcWorkspace) throws NoSuchWorkspaceException, AccessDeniedException, LockException, InvalidItemStateException, RepositoryException {
		getNode().update(srcWorkspace);
	}

	@Override
	public NodeIterator merge(String srcWorkspace, boolean bestEffort) throws NoSuchWorkspaceException, AccessDeniedException, MergeException, LockException, InvalidItemStateException, RepositoryException {
		return getNode().merge(srcWorkspace, bestEffort);
	}

	@Override
	public String getCorrespondingNodePath(String workspaceName) throws ItemNotFoundException, NoSuchWorkspaceException, AccessDeniedException, RepositoryException {
		return getNode().getCorrespondingNodePath(workspaceName);
	}

	@Override
	public NodeIterator getSharedSet() throws RepositoryException {
		return getNode().getSharedSet();
	}

	@Override
	public void removeSharedSet() throws VersionException, LockException, ConstraintViolationException, RepositoryException {
		getNode().removeSharedSet();
	}

	@Override
	public void removeShare() throws VersionException, LockException, ConstraintViolationException, RepositoryException {
		getNode().removeShare();
	}

	@Override
	public boolean isCheckedOut() throws RepositoryException {
		return getNode().isCheckedOut();
	}

	@Override
	public void restore(String versionName, boolean removeExisting) throws VersionException, ItemExistsException, UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException {
		getNode().restore(versionName, removeExisting);
	}

	@Override
	public void restore(Version version, boolean removeExisting) throws VersionException, ItemExistsException, InvalidItemStateException, UnsupportedRepositoryOperationException, LockException, RepositoryException {
		getNode().restore(version, removeExisting);
	}

	@Override
	public void restore(Version version, String relPath, boolean removeExisting) throws PathNotFoundException, ItemExistsException, VersionException, ConstraintViolationException, UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException {
		getNode().restore(version, relPath, removeExisting);
	}

	@Override
	public void restoreByLabel(String versionLabel, boolean removeExisting) throws VersionException, ItemExistsException, UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException {
		getNode().restoreByLabel(versionLabel, removeExisting);
	}

	@Override
	public VersionHistory getVersionHistory() throws UnsupportedRepositoryOperationException, RepositoryException {
		return getNode().getVersionHistory();
	}

	@Override
	public Version getBaseVersion() throws UnsupportedRepositoryOperationException, RepositoryException {
		return getNode().getBaseVersion();
	}

	@Override
	public Lock lock(boolean isDeep, boolean isSessionScoped) throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, InvalidItemStateException, RepositoryException {
		return getNode().lock(isDeep, isSessionScoped);
	}

	@Override
	public Lock getLock() throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, RepositoryException {
		return getNode().getLock();
	}

	@Override
	public void unlock() throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, InvalidItemStateException, RepositoryException {
		getNode().unlock();
	}

	@Override
	public boolean holdsLock() throws RepositoryException {
		return getNode().holdsLock();
	}

	@Override
	public boolean isLocked() throws RepositoryException {
		return getNode().isLocked();
	}

	@Override
	public void followLifecycleTransition(String transition) throws UnsupportedRepositoryOperationException, InvalidLifecycleTransitionException, RepositoryException {
		getNode().followLifecycleTransition(transition);
	}

	@Override
	public String[] getAllowedLifecycleTransistions() throws UnsupportedRepositoryOperationException, RepositoryException {
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
	public Item getAncestor(int depth) throws ItemNotFoundException, AccessDeniedException, RepositoryException {
		return getNode().getAncestor(depth);
	}

	@Override
	public Node getParent() throws ItemNotFoundException, AccessDeniedException, RepositoryException {
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
	public void save() throws AccessDeniedException, ItemExistsException, ConstraintViolationException, InvalidItemStateException, ReferentialIntegrityException, VersionException, LockException, NoSuchNodeTypeException, RepositoryException {
		getNode().save();
	}

	@Override
	public void refresh(boolean keepChanges) throws InvalidItemStateException, RepositoryException {
		getNode().refresh(keepChanges);
	}

	@Override
	public void remove() throws VersionException, LockException, ConstraintViolationException, AccessDeniedException, RepositoryException {
		getNode().remove();
	}
}
