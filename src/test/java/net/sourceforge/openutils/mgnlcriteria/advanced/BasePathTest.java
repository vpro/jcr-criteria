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

package net.sourceforge.openutils.mgnlcriteria.advanced;

import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.cms.security.MgnlRoleManager;
import info.magnolia.cms.security.Realm;
import info.magnolia.cms.security.SecuritySupport;
import info.magnolia.cms.security.SecuritySupportImpl;
import info.magnolia.cms.security.SystemUserManager;
import info.magnolia.context.MgnlContext;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.test.ComponentsTestUtil;
import it.openutils.mgnlutils.test.RepositoryTestConfiguration;
import it.openutils.mgnlutils.test.TestNgRepositoryTestcase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jcr.Node;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRCriteriaFactory;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.ResultIterator;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Criterion;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Restrictions;
import net.sourceforge.openutils.mgnlcriteria.tests.CriteriaTestUtils;

import org.apache.commons.lang.StringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * Tests criteria queries with different values of basePath parameter.
 * @author dschivo
 * @version $Id$
 */
@RepositoryTestConfiguration(jackrabbitRepositoryConfig = "/crit-repository/jackrabbit-test-configuration.xml", repositoryConfig = "/crit-repository/test-repositories.xml", bootstrapFiles = {
    "/crit-bootstrap/website.Criteria.xml",
    "/crit-bootstrap/userroles.anonymous.xml",
    "/crit-bootstrap/users.system.anonymous.xml" })
public class BasePathTest extends TestNgRepositoryTestcase
{

    /**
     * {@inheritDoc}
     */
    @Override
    @BeforeClass
    public void setUp() throws Exception
    {
        super.setUp();

        // Titles of the nodes in this workspace:
        // - Criteria
        // --- AbstractCriteriaImpl
        // ----- AbstractMagnoliaCriteriaImpl
        // ------- MagnoliaCriteriaImpl
        // --------- MagnoliaCriteriaWithLimitImpl
        // ----- AdvancedCriteriaImpl
        // --- AdvancedCriteriaImpl
        // --- TranslatableCriteria
        // ----- AbstractCriteriaImpl
        // ------- AbstractMagnoliaCriteriaImpl
        // --------- MagnoliaCriteriaImpl
        // ----------- MagnoliaCriteriaWithLimitImpl
        // ------- AdvancedCriteriaImpl
        MgnlContext.getJCRSession(RepositoryConstants.WEBSITE).save();

        // info.magnolia.cms.security.SecurityTest.setUp()
        final SecuritySupportImpl sec = new SecuritySupportImpl();
        sec.addUserManager(Realm.REALM_SYSTEM.getName(), new SystemUserManager());
        sec.setRoleManager(new MgnlRoleManager());
        ComponentsTestUtil.setInstance(SecuritySupport.class, sec);
    }

    /**
     * Passing a null basePath should search the entire repository.
     * @throws Exception
     */
    @Test
    public void testNullBasePath() throws Exception
    {
        Collection<String> paths = searchPaths(null, "AdvancedCriteriaImpl");
        Assert.assertNotNull(paths);
        Assert.assertEquals(paths.size(), 3);
        Assert.assertTrue(paths.contains("/Criteria/AbstractCriteriaImpl/AdvancedCriteriaImpl"));
        Assert.assertTrue(paths.contains("/Criteria/AdvancedCriteriaImpl"));
        Assert.assertTrue(paths.contains("/Criteria/TranslatableCriteria/AbstractCriteriaImpl/AdvancedCriteriaImpl"));
    }

    /**
     * Passing an empty basePath should search the entire repository.
     * @throws Exception
     */
    @Test
    public void testEmptyBasePath() throws Exception
    {
        Collection<String> paths = searchPaths(StringUtils.EMPTY, "AdvancedCriteriaImpl");
        Assert.assertNotNull(paths);
        Assert.assertEquals(paths.size(), 3);
        Assert.assertTrue(paths.contains("/Criteria/AbstractCriteriaImpl/AdvancedCriteriaImpl"));
        Assert.assertTrue(paths.contains("/Criteria/AdvancedCriteriaImpl"));
        Assert.assertTrue(paths.contains("/Criteria/TranslatableCriteria/AbstractCriteriaImpl/AdvancedCriteriaImpl"));
    }

    /**
     * Passing an xpath query ending with /* as the basePath should search the children.
     * @throws Exception
     */
    @Test
    public void testSearchXpathBasePathWithSingleSlash() throws Exception
    {
        Collection<String> paths = searchPaths("//Criteria/AbstractCriteriaImpl/*", StringUtils.EMPTY);
        Assert.assertNotNull(paths);
        Assert.assertEquals(paths.size(), 2);
        Assert.assertTrue(paths.contains("/Criteria/AbstractCriteriaImpl/AbstractMagnoliaCriteriaImpl"));
        Assert.assertTrue(paths.contains("/Criteria/AbstractCriteriaImpl/AdvancedCriteriaImpl"));
    }

    /**
     * Passing an xpath query ending with //* as the basePath should search the descendants.
     * @throws Exception
     */
    @Test
    public void testSearchXpathBasePathWithDoubleSlash() throws Exception
    {
        Collection<String> paths = searchPaths("//Criteria/TranslatableCriteria//*", "AdvancedCriteriaImpl");
        Assert.assertNotNull(paths);
        Assert.assertEquals(paths.size(), 1);
        Assert.assertTrue(paths.contains("/Criteria/TranslatableCriteria/AbstractCriteriaImpl/AdvancedCriteriaImpl"));
    }

    /**
     * Passing an handle as the basePath should search the descendants.
     * @throws Exception
     */
    @Test
    public void testSearchHandleBasePath() throws Exception
    {
        Collection<String> paths = searchPaths("/Criteria/TranslatableCriteria", "AdvancedCriteriaImpl");
        Assert.assertNotNull(paths);
        Assert.assertEquals(paths.size(), 1);
        Assert.assertTrue(paths.contains("/Criteria/TranslatableCriteria/AbstractCriteriaImpl/AdvancedCriteriaImpl"));
    }

    /**
     * Passing an handle ending with / as the basePath should search the descendants. This test makes sure that the
     * resulting xpath query does not end with ///*
     * @throws Exception
     */
    @Test
    public void testSearchHandleBasePathWithTrailingSlash() throws Exception
    {
        Collection<String> paths = searchPaths("/Criteria/TranslatableCriteria/", "AdvancedCriteriaImpl");
        Assert.assertNotNull(paths);
        Assert.assertEquals(paths.size(), 1);
        Assert.assertTrue(paths.contains("/Criteria/TranslatableCriteria/AbstractCriteriaImpl/AdvancedCriteriaImpl"));
    }

    private Collection<String> searchPaths(String basePath, String title)
    {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath(basePath);
        criteria.add(Restrictions.eq(Criterion.JCR_PRIMARYTYPE, MgnlNodeType.NT_PAGE));
        if (!StringUtils.isEmpty(title))
        {
            criteria.add(Restrictions.eq("@title", title));
        }
        AdvancedResult advResult = criteria.execute();
        ResultIterator<? extends Node> items = advResult.getItems();
        List<String> paths = new ArrayList<String>();
        while (items.hasNext())
        {
            paths.add(CriteriaTestUtils.path(items.next()));
        }
        return paths;
    }
}
