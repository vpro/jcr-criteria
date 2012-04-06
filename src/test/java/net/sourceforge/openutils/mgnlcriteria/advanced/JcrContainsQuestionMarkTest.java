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

package net.sourceforge.openutils.mgnlcriteria.advanced;

import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.i18n.DefaultI18nContentSupport;
import info.magnolia.cms.i18n.I18nContentSupport;
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

import javax.jcr.query.Query;

import net.sourceforge.openutils.mgnlcriteria.advanced.impl.AdvancedResultImpl;
import net.sourceforge.openutils.mgnlcriteria.advanced.impl.QueryExecutorHelper;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRQueryException;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * Unit test on escaping quotation mark in jcr:contains(). Unescaped quotation marks are not illegal: no Exception is
 * thrown on query execution. All quotation marks (not only the trailing one) should be escaped to obatain results.
 * @author dschivo
 * @version $Id$
 */
@RepositoryTestConfiguration(jackrabbitRepositoryConfig = "/crit-repository/jackrabbit-test-configuration.xml", repositoryConfig = "/crit-repository/test-repositories.xml", bootstrapFiles = {
    "/crit-bootstrap/website.contains.xml",
    "/crit-bootstrap/userroles.anonymous.xml",
    "/crit-bootstrap/users.system.anonymous.xml" })
public class JcrContainsQuestionMarkTest extends TestNgRepositoryTestcase
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
        // - hello test? world
        // - hello te?st world
        // - hello "Milano" world
        // - lorem
        // - lorem ipsum
        // - dolor sit
        // - dolor sit amet
        MgnlContext.getHierarchyManager(RepositoryConstants.WEBSITE).save();

        ComponentsTestUtil.setInstance(I18nContentSupport.class, new DefaultI18nContentSupport());

        // info.magnolia.cms.security.SecurityTest.setUp()
        final SecuritySupportImpl sec = new SecuritySupportImpl();
        sec.addUserManager(Realm.REALM_SYSTEM.getName(), new SystemUserManager());
        sec.setRoleManager(new MgnlRoleManager());
        ComponentsTestUtil.setInstance(SecuritySupport.class, sec);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testTrailingUnescaped() throws Exception
    {
        HierarchyManager hm = MgnlContext.getHierarchyManager(RepositoryConstants.WEBSITE);

        AdvancedResultImpl advResult = null;
        try
        {
            String stmt = "//*[((@jcr:primaryType='mgnl:content') and (jcr:contains(@title,'test?')))] order by @jcr:score";
            advResult = QueryExecutorHelper.execute(stmt, Query.XPATH, hm, 10, 0, null, false);
        }
        catch (JCRQueryException e)
        {
            // The following statement is NOT true:
            // A search string like 'test?' will run into a ParseException
            // documented in
            // http://issues.apache.org/jira/browse/JCR-1248
            Assert.fail("http://issues.apache.org/jira/browse/JCR-1248");
        }

        // remark: total size is only evaluated when the result is sorted
        Assert.assertEquals(advResult.getTotalSize(), 0);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testTrailingEscaped() throws Exception
    {
        HierarchyManager hm = MgnlContext.getHierarchyManager(RepositoryConstants.WEBSITE);

        String stmt = "//*[((@jcr:primaryType='mgnl:content') and (jcr:contains(@title,'test\\?')))] order by @jcr:score";
        AdvancedResultImpl advResult = QueryExecutorHelper.execute(stmt, Query.XPATH, hm, -1, 0, null, false);

        Assert.assertEquals(advResult.getTotalSize(), 1);
        Assert.assertEquals(advResult.getItems().next().getTitle(), "hello test? world");
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testMiddle() throws Exception
    {
        HierarchyManager hm = MgnlContext.getHierarchyManager(RepositoryConstants.WEBSITE);
        String stmt;
        AdvancedResultImpl advResult;

        stmt = "//*[((@jcr:primaryType='mgnl:content') and (jcr:contains(@title,'te?st')))] order by @jcr:score";
        advResult = QueryExecutorHelper.execute(stmt, Query.XPATH, hm, -1, 0, null, false);
        Assert.assertEquals(advResult.getTotalSize(), 0);

        stmt = "//*[((@jcr:primaryType='mgnl:content') and (jcr:contains(@title,'te\\?st')))] order by @jcr:score";
        advResult = QueryExecutorHelper.execute(stmt, Query.XPATH, hm, -1, 0, null, false);
        Assert.assertEquals(advResult.getTotalSize(), 1);
        Assert.assertEquals(advResult.getItems().next().getTitle(), "hello te?st world");
    }
}
