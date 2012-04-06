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
import info.magnolia.cms.i18n.DefaultI18nContentSupport;
import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.cms.security.AccessManager;
import info.magnolia.cms.security.AccessManagerImpl;
import info.magnolia.cms.security.Permission;
import info.magnolia.cms.security.PermissionImpl;
import info.magnolia.cms.util.SimpleUrlPattern;
import info.magnolia.context.MgnlContext;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.test.ComponentsTestUtil;
import it.openutils.mgnlutils.test.RepositoryTestConfiguration;
import it.openutils.mgnlutils.test.TestNgRepositoryTestcase;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Collections;

import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResultItem;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRCriteriaFactory;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.ResultIterator;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Order;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Restrictions;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * A test which retrieves a bunch of nodes, some of them actually forbidden by
 * the magnolia ACLs on the website repo. Iterating on the returned result
 * should probably generate an AccessDeniedException that we have to decide how
 * to handle (probably just by silently skipping nodes during the iteration as a
 * first solution)
 * 
 * @author dschivo
 */
@RepositoryTestConfiguration(jackrabbitRepositoryConfig = "/crit-repository/jackrabbit-test-configuration.xml", repositoryConfig = "/crit-repository/test-repositories.xml", bootstrapFiles = "/crit-bootstrap/website.pets.xml")
public class AccessibleResultItemResultIteratorTest extends
		TestNgRepositoryTestcase {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@BeforeClass
	public void setUp() throws Exception {
		super.setUp();

		// Nodes in this workspace:
		// - pets (title=Pets)
		// --- cats (title=Cats)
		// ----- 1 (title=Leo, petType=cat, birthDate=2000-09-07)
		// ----- 7 (title=Samantha, petType=cat, birthDate=1995-09-04)
		// ----- 8 (title=Max, petType=cat, birthDate=1995-09-04)
		// ----- 13 (title=Sly, petType=cat, birthDate=2002-06-08)
		// --- dogs (title=Dogs)
		// ----- 3 (title=Rosy, petType=dog, birthDate=2001-04-17)
		// ----- 4 (title=Jewel, petType=dog, birthDate=2000-03-07)
		// ----- 10 (title=Mulligan, petType=dog, birthDate=1997-02-24)
		// ----- 12 (title=Lucky, petType=dog, birthDate=2000-06-24)
		// --- lizards (title=Lizards)
		// ----- 5 (title=Iggy, petType=lizard, birthDate=2000-11-30)
		// --- snakes (title=Snakes)
		// ----- 6 (title=George, petType=snake, birthDate=2000-01-20)
		// --- birds (title=Birds)
		// ----- 9 (title=Lucky, petType=bird, birthDate=1999-08-06)
		// ----- 11 (title=Freddy, petType=bird, birthDate=2000-03-09)
		// --- hamsters (title=Hamsters)
		// ----- 2 (title=Basil, petType=hamster, birthDate=2002-08-06)

		HierarchyManager hm = MgnlContext
				.getHierarchyManager(RepositoryConstants.WEBSITE);
		hm.save();

		Permission readDogsPermission = new PermissionImpl();
		readDogsPermission.setPattern(new SimpleUrlPattern("/pets/dogs/*"));
		readDogsPermission.setPermissions(Permission.READ);

		AccessManager am = new AccessManagerImpl();
		am.setPermissionList(Collections.singletonList(readDogsPermission));

		// java.lang.NoSuchFieldException: accessManager
//		Field amField = hm.getClass().getDeclaredField("accessManager");
//		amField.setAccessible(true);
//		amField.set(hm, am);

		ComponentsTestUtil.setInstance(I18nContentSupport.class, new DefaultI18nContentSupport());
	}

	/**
	 * Tests that the website HierarchyManager allows access only to /pets/dogs.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetContent() throws Exception {
		HierarchyManager hm = MgnlContext
				.getHierarchyManager(RepositoryConstants.WEBSITE);

		try {
			// Allowed access
			hm.getContent("/pets/dogs/3");
		} catch (RepositoryException e) {
			Assert.fail("should be allowed to read path /pets/dogs");
		}

		try {
			// Not allowed access
			hm.getContent("/pets/cats/1");
			Assert.fail("should not be allowed to read path /pets/cats");
		} catch (RepositoryException e) {
		}
	}

	/**
	 * Tests the method of an advanced result for iterating over accessible
	 * nodes (i.e. those which do not throw an AccessDeniedException).
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetAccessibleItems() throws Exception {
		Calendar begin = Calendar.getInstance();
		begin.set(1999, Calendar.JANUARY, 1);
		Calendar end = Calendar.getInstance();
		end.set(2001, Calendar.DECEMBER, 31);

		Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(
				RepositoryConstants.WEBSITE).setBasePath("/pets").add(
				Restrictions.between("@birthDate", begin, end)).addOrder(
				Order.asc("@birthDate"));

		// Query results:
		// --- 9 (title=Lucky, petType=bird, birthDate=1999-08-06)
		// --- 6 (title=George, petType=snake, birthDate=2000-01-20)
		// --- 4 (title=Jewel, petType=dog, birthDate=2000-03-07)
		// --- 11 (title=Freddy, petType=bird, birthDate=2000-03-09)
		// --- 12 (title=Lucky, petType=dog, birthDate=2000-06-24)
		// --- 1 (title=Leo, petType=cat, birthDate=2000-09-07)
		// --- 5 (title=Iggy, petType=lizard, birthDate=2000-11-30)
		// --- 3 (title=Rosy, petType=dog, birthDate=2001-04-17)
		AdvancedResult result = criteria.execute();

		// Accessible results (dogs only):
		// --- 4 (title=Jewel, petType=dog, birthDate=2000-03-07)
		// --- 12 (title=Lucky, petType=dog, birthDate=2000-06-24)
		// --- 3 (title=Rosy, petType=dog, birthDate=2001-04-17)
		ResultIterator<AdvancedResultItem> iterator = result.getItems();

		Assert.assertTrue(iterator.hasNext());
		Assert.assertEquals(iterator.next().getName(), "4");
		Assert.assertTrue(iterator.hasNext());
		Assert.assertEquals(iterator.next().getName(), "12");
		Assert.assertTrue(iterator.hasNext());
		Assert.assertEquals(iterator.next().getName(), "3");
		Assert.assertFalse(iterator.hasNext());
	}

}
