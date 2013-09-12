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

package net.sourceforge.openutils.mgnlcriteria.jcr.query.lucene;

import info.magnolia.cms.security.AccessManager;
import info.magnolia.cms.security.MgnlRoleManager;
import info.magnolia.cms.security.Permission;
import info.magnolia.cms.security.Realm;
import info.magnolia.cms.security.SecuritySupport;
import info.magnolia.cms.security.SecuritySupportImpl;
import info.magnolia.cms.security.SystemUserManager;
import info.magnolia.context.ContextDecorator;
import info.magnolia.context.MgnlContext;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.test.ComponentsTestUtil;
import it.openutils.mgnlutils.test.RepositoryTestConfiguration;
import it.openutils.mgnlutils.test.TestNgRepositoryTestcase;

import java.util.Calendar;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRCriteriaFactory;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.ResultIterator;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Order;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Restrictions;
import net.sourceforge.openutils.mgnlcriteria.tests.CriteriaTestUtils;

import org.apache.commons.lang.StringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * Tests that this custom search index modifies the lucene query according to ACL rules.
 * @author dschivo
 */
@RepositoryTestConfiguration(jackrabbitRepositoryConfig = "/crit-repository/jackrabbit-acl-search-index-test-configuration.xml", repositoryConfig = "/crit-repository/test-repositories.xml", bootstrapFiles = {
    "/crit-bootstrap/website.pets.xml",
    "/crit-bootstrap/userroles.anonymous.xml",
    "/crit-bootstrap/users.system.anonymous.xml" })
public class AclSearchIndexTest extends TestNgRepositoryTestcase
{

    /**
     * {@inheritDoc}
     */
    @Override
    @BeforeClass
    public void setUp() throws Exception
    {
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

        Session hm = MgnlContext.getJCRSession(RepositoryConstants.WEBSITE);
        hm.save();

        // info.magnolia.cms.security.SecurityTest.setUp()
        final SecuritySupportImpl sec = new SecuritySupportImpl();
        sec.addUserManager(Realm.REALM_SYSTEM.getName(), new SystemUserManager());
        sec.setRoleManager(new MgnlRoleManager());
        ComponentsTestUtil.setInstance(SecuritySupport.class, sec);
    }

    /**
     * Tests that the execution of a query on all pets returns dogs only, because of an ACL rule.
     * @throws Exception
     */
    @Test
    public void testDogsOnly() throws Exception
    {
        final AccessManager wrappedAM = MgnlContext.getAccessManager(RepositoryConstants.WEBSITE);
        final AccessManager wrapperAM = new AccessManager()
        {

            public boolean isGranted(String path, long permissions)
            {
                // ACL rule: deny permission on pets subtree
                if (StringUtils.startsWith(path, "/pets/"))
                {
                    // ACL rule: read permission on dogs subtree
                    return StringUtils.startsWith(path, "/pets/dogs/");
                }
                return wrappedAM.isGranted(path, permissions);
            }

            public void setPermissionList(List<Permission> permissions)
            {
                wrappedAM.setPermissionList(permissions);
            }

            public List<Permission> getPermissionList()
            {
                return wrappedAM.getPermissionList();
            }

            public long getPermissions(String path)
            {
                return wrappedAM.getPermissions(path);
            }
        };
        MgnlContext.setInstance(new ContextDecorator(MgnlContext.getInstance())
        {

            /**
             * {@inheritDoc}
             */
            @Override
            public AccessManager getAccessManager(String name)
            {
                if (RepositoryConstants.WEBSITE.equals(name))
                {
                    return wrapperAM;
                }
                return super.getAccessManager(name);
            }
        });
        try
        {
            Calendar begin = Calendar.getInstance();
            begin.set(1999, Calendar.JANUARY, 1);
            Calendar end = Calendar.getInstance();
            end.set(2001, Calendar.DECEMBER, 31);

            Criteria criteria = JCRCriteriaFactory
                .createCriteria()
                .setWorkspace(RepositoryConstants.WEBSITE)
                .setBasePath("/pets")
                .add(Restrictions.between("@birthDate", begin, end))
                .addOrder(Order.asc("@birthDate"));

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
            ResultIterator<? extends Node> iterator = result.getItems();

            Assert.assertTrue(iterator.hasNext());
            Assert.assertEquals(CriteriaTestUtils.name(iterator.next()), "4");
            Assert.assertTrue(iterator.hasNext());
            Assert.assertEquals(CriteriaTestUtils.name(iterator.next()), "12");
            Assert.assertTrue(iterator.hasNext());
            Assert.assertEquals(CriteriaTestUtils.name(iterator.next()), "3");
            Assert.assertFalse(iterator.hasNext());
        }
        finally
        {
            MgnlContext.setInstance(((ContextDecorator) MgnlContext.getInstance()).getWrappedContext());
        }
    }

    /**
     * Tests that the execution of a query on all pets does not return any dog, because of an ACL rule.
     * @throws Exception
     */
    @Test
    public void testDogsExcluded() throws Exception
    {
        final AccessManager wrappedAM = MgnlContext.getAccessManager(RepositoryConstants.WEBSITE);
        final AccessManager wrapperAM = new AccessManager()
        {

            public boolean isGranted(String path, long permissions)
            {
                // ACL rule: read permission on pets subtree
                if (StringUtils.startsWith(path, "/pets/"))
                {
                    // ACL rule: deny permission on dogs subtree
                    return !StringUtils.startsWith(path, "/pets/dogs/");
                }
                return wrappedAM.isGranted(path, permissions);
            }

            public void setPermissionList(List<Permission> permissions)
            {
                wrappedAM.setPermissionList(permissions);
            }

            public List<Permission> getPermissionList()
            {
                return wrappedAM.getPermissionList();
            }

            public long getPermissions(String path)
            {
                return wrappedAM.getPermissions(path);
            }
        };
        MgnlContext.setInstance(new ContextDecorator(MgnlContext.getInstance())
        {

            /**
             * {@inheritDoc}
             */
            @Override
            public AccessManager getAccessManager(String name)
            {
                if (RepositoryConstants.WEBSITE.equals(name))
                {
                    return wrapperAM;
                }
                return super.getAccessManager(name);
            }
        });
        try
        {
            Calendar begin = Calendar.getInstance();
            begin.set(1999, Calendar.JANUARY, 1);
            Calendar end = Calendar.getInstance();
            end.set(2001, Calendar.DECEMBER, 31);

            Criteria criteria = JCRCriteriaFactory
                .createCriteria()
                .setWorkspace(RepositoryConstants.WEBSITE)
                .setBasePath("/pets")
                .add(Restrictions.between("@birthDate", begin, end))
                .addOrder(Order.asc("@birthDate"));

            AdvancedResult result = criteria.execute();

            // Accessible results (dogs excluded):
            // --- 9 (title=Lucky, petType=bird, birthDate=1999-08-06)
            // --- 6 (title=George, petType=snake, birthDate=2000-01-20)
            // --- 11 (title=Freddy, petType=bird, birthDate=2000-03-09)
            // --- 1 (title=Leo, petType=cat, birthDate=2000-09-07)
            // --- 5 (title=Iggy, petType=lizard, birthDate=2000-11-30)
            ResultIterator<? extends Node> iterator = result.getItems();

            Assert.assertTrue(iterator.hasNext());
            Assert.assertEquals(CriteriaTestUtils.name(iterator.next()), "9");
            Assert.assertTrue(iterator.hasNext());
            Assert.assertEquals(CriteriaTestUtils.name(iterator.next()), "6");
            Assert.assertTrue(iterator.hasNext());
            Assert.assertEquals(CriteriaTestUtils.name(iterator.next()), "11");
            Assert.assertTrue(iterator.hasNext());
            Assert.assertEquals(CriteriaTestUtils.name(iterator.next()), "1");
            Assert.assertTrue(iterator.hasNext());
            Assert.assertEquals(CriteriaTestUtils.name(iterator.next()), "5");
            Assert.assertFalse(iterator.hasNext());
        }
        finally
        {
            MgnlContext.setInstance(((ContextDecorator) MgnlContext.getInstance()).getWrappedContext());
        }
    }

}
