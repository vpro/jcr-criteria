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
import info.magnolia.cms.i18n.DefaultI18nContentSupport;
import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.context.MgnlContext;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.test.ComponentsTestUtil;
import it.openutils.mgnlutils.test.RepositoryTestConfiguration;
import it.openutils.mgnlutils.test.TestNgRepositoryTestcase;

import javax.jcr.Node;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRCriteriaFactory;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.ResultIterator;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Order;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Restrictions;
import net.sourceforge.openutils.mgnlcriteria.tests.CriteriaTestUtils;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * Tests pagination in criteria queries.
 * @author dschivo
 */
@RepositoryTestConfiguration(jackrabbitRepositoryConfig = "/crit-repository/jackrabbit-test-configuration.xml", repositoryConfig = "/crit-repository/test-repositories.xml", bootstrapFiles = {
    "/crit-bootstrap/website.letters.xml",
    "/crit-bootstrap/userroles.anonymous.xml",
    "/crit-bootstrap/users.system.anonymous.xml",
    "/crit-bootstrap/config.server.auditLogging.xml",
    "/crit-bootstrap/config.server.i18n.content.xml" }, security = true)
public class PaginationTest extends TestNgRepositoryTestcase
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
        // - Letters
        // --- A
        // --- B
        // --- C
        // --- ...
        // --- X
        // --- Y
        // --- Z
        MgnlContext.getJCRSession(RepositoryConstants.WEBSITE).save();

        ComponentsTestUtil.setInstance(I18nContentSupport.class, new DefaultI18nContentSupport());
    }

    /**
     * Retrieves all letters.
     * @throws Exception
     */
    @Test
    public void testNoPagination() throws Exception
    {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath("/letters");
        criteria.add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_PAGE));
        criteria.addOrder(Order.asc("@title"));
        AdvancedResult advResult = criteria.execute();

        Assert.assertEquals(advResult.getTotalSize(), 26);

        ResultIterator< ? extends Node> resultIterator = advResult.getItems();
        Assert.assertEquals(resultIterator.getSize(), 26);

        Assert.assertEquals(CriteriaTestUtils.title(resultIterator.next()), "A");

        Node content = null;
        while (resultIterator.hasNext())
        {
            content = resultIterator.next();
        }
        Assert.assertEquals(CriteriaTestUtils.title(content), "Z");
    }

    @Test
    public void testForEachSupport() throws Exception
    {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath("/letters");
        criteria.add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_PAGE));
        criteria.addOrder(Order.asc("@title"));
        AdvancedResult advResult = criteria.execute();

        int count = 0;

        for (Node content : advResult.getItems())
        {
            Assert.assertNotNull(content);
            count++;
        }

        Assert.assertEquals(count, 26);
    }

    /**
     * Retrieves letters from the tenth on.
     * @throws Exception
     */
    @Test
    public void testSetFirstResult() throws Exception
    {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath("/letters");
        criteria.add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_PAGE));
        criteria.addOrder(Order.asc("@title"));
        criteria.setFirstResult(9);
        AdvancedResult advResult = criteria.execute();

        Assert.assertEquals(advResult.getTotalSize(), 26);

        ResultIterator< ? extends Node> resultIterator = advResult.getItems();

        Assert.assertEquals(CriteriaTestUtils.title(resultIterator.next()), "J");
        Assert.assertEquals(resultIterator.getSize(), 17);
    }

    /**
     * Retrieves the first ten letters.
     * @throws Exception
     */
    @Test
    public void testSetMaxResults() throws Exception
    {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath("/letters");
        criteria.add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_PAGE));
        criteria.addOrder(Order.asc("@title"));
        criteria.setMaxResults(10);
        AdvancedResult advResult = criteria.execute();

        // test is broken with jackrabbit > 2.6.1 due to https://issues.apache.org/jira/browse/JCR-3402
        // starting from jackrabbit 2.6.2 size is only set if the number of fetched nodes is < total number of result,
        // considering offset (e.g if you have 97 results and paging by 10, the total number will only be returned when
        // asking for page 10, which is pretty useless)
        if (advResult.getTotalSize() == -1)
        {
            Assert.fail("total number of results not set for query " + criteria.toXpathExpression());
        }

        Assert.assertEquals(advResult.getTotalSize(), 26);

        ResultIterator< ? extends Node> resultIterator = advResult.getItems();
        Assert.assertEquals(resultIterator.getSize(), 10);

        Node content = null;
        while (resultIterator.hasNext())
        {
            content = resultIterator.next();
        }
        Assert.assertEquals(CriteriaTestUtils.title(content), "J");
    }

    /**
     * Retrieves the third page of 5 letters.
     * @throws Exception
     */
    @Test
    public void testSetPaging() throws Exception
    {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath("/letters");
        criteria.add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_PAGE));
        criteria.addOrder(Order.asc("@title"));
        criteria.setPaging(5, 3);
        AdvancedResult advResult = criteria.execute();

        if (advResult.getTotalSize() == -1)
        {
            Assert.fail("total number of results not set for query " + criteria.toXpathExpression());
        }

        Assert.assertEquals(advResult.getTotalSize(), 26);

        ResultIterator< ? extends Node> resultIterator = advResult.getItems();

        Assert.assertEquals(resultIterator.getSize(), 5);
        Assert.assertEquals(CriteriaTestUtils.title(resultIterator.next()), "K");
        Assert.assertEquals(CriteriaTestUtils.title(resultIterator.next()), "L");
        Assert.assertEquals(CriteriaTestUtils.title(resultIterator.next()), "M");
        Assert.assertEquals(CriteriaTestUtils.title(resultIterator.next()), "N");
        Assert.assertEquals(CriteriaTestUtils.title(resultIterator.next()), "O");
    }

    /**
     * Retrieves the fourth page of 8 letters.
     * @throws Exception
     */
    @Test
    public void testSetPagingLastPage() throws Exception
    {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath("/letters");
        criteria.add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_PAGE));
        criteria.addOrder(Order.asc("@title"));
        criteria.setPaging(8, 4);
        AdvancedResult advResult = criteria.execute();

        Assert.assertEquals(advResult.getTotalSize(), 26);

        ResultIterator< ? extends Node> resultIterator = advResult.getItems();

        Assert.assertEquals(resultIterator.getSize(), 2);
        Assert.assertEquals(CriteriaTestUtils.title(resultIterator.next()), "Y");
        Assert.assertEquals(CriteriaTestUtils.title(resultIterator.next()), "Z");
    }

    /**
     * Retrieves the last page of 3 letters.
     * @throws Exception
     */
    @Test
    public void testSetPagingEvenPage() throws Exception
    {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath("/letters");
        criteria.add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_PAGE));
        criteria.addOrder(Order.asc("@title"));
        criteria.setPaging(5, 1);

        AdvancedResult advResult = criteria.execute();

        if (advResult.getTotalSize() == -1)
        {
            Assert.fail("total number of results not set for query " + criteria.toXpathExpression());
        }

        Assert.assertEquals(advResult.getTotalSize(), 26);
        Assert.assertEquals(advResult.getNumberOfPages(), 6);

        ResultIterator< ? extends Node> resultIterator = advResult.getItems();
        Assert.assertEquals(resultIterator.getSize(), 5);
    }
}
