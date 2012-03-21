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

import info.magnolia.cms.i18n.DefaultI18nContentSupport;
import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.context.MgnlContext;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.test.ComponentsTestUtil;
import it.openutils.mgnlutils.test.RepositoryTestConfiguration;
import it.openutils.mgnlutils.test.TestNgRepositoryTestcase;
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
 * Tests pagination in criteria queries.
 * @author dschivo
 */
@RepositoryTestConfiguration(jackrabbitRepositoryConfig = "/crit-repository/jackrabbit-test-configuration.xml", repositoryConfig = "/crit-repository/test-repositories.xml", bootstrapFiles = "/crit-bootstrap/website.letters.xml")
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
        MgnlContext.getHierarchyManager(RepositoryConstants.WEBSITE).save();

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
        criteria.add(Restrictions.eq("@jcr:primaryType", "mgnl:content"));
        criteria.addOrder(Order.asc("@title"));
        AdvancedResult advResult = criteria.execute();

        Assert.assertEquals(advResult.getTotalSize(), 26);

        ResultIterator<AdvancedResultItem> resultIterator = advResult.getItems();
        Assert.assertEquals(resultIterator.getSize(), 26);

        Assert.assertEquals(resultIterator.next().getTitle(), "A");

        AdvancedResultItem content = null;
        while (resultIterator.hasNext())
        {
            content = resultIterator.next();
        }
        Assert.assertEquals(content.getTitle(), "Z");
    }

    @Test
    public void testForEachSupport() throws Exception
    {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath("/letters");
        criteria.add(Restrictions.eq("@jcr:primaryType", "mgnl:content"));
        criteria.addOrder(Order.asc("@title"));
        AdvancedResult advResult = criteria.execute();

        int count = 0;

        for (AdvancedResultItem content : advResult.getItems())
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
        criteria.add(Restrictions.eq("@jcr:primaryType", "mgnl:content"));
        criteria.addOrder(Order.asc("@title"));
        criteria.setFirstResult(9);
        AdvancedResult advResult = criteria.execute();

        Assert.assertEquals(advResult.getTotalSize(), 26);

        ResultIterator<AdvancedResultItem> resultIterator = advResult.getItems();

        Assert.assertEquals(resultIterator.next().getTitle(), "J");
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
        criteria.add(Restrictions.eq("@jcr:primaryType", "mgnl:content"));
        criteria.addOrder(Order.asc("@title"));
        criteria.setMaxResults(10);
        AdvancedResult advResult = criteria.execute();

        Assert.assertEquals(advResult.getTotalSize(), 26);

        ResultIterator<AdvancedResultItem> resultIterator = advResult.getItems();
        Assert.assertEquals(resultIterator.getSize(), 10);

        AdvancedResultItem content = null;
        while (resultIterator.hasNext())
        {
            content = resultIterator.next();
        }
        Assert.assertEquals(content.getTitle(), "J");
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
        criteria.add(Restrictions.eq("@jcr:primaryType", "mgnl:content"));
        criteria.addOrder(Order.asc("@title"));
        criteria.setPaging(5, 3);
        AdvancedResult advResult = criteria.execute();

        Assert.assertEquals(advResult.getTotalSize(), 26);

        ResultIterator<AdvancedResultItem> resultIterator = advResult.getItems();

        Assert.assertEquals(resultIterator.getSize(), 5);
        Assert.assertEquals(resultIterator.next().getTitle(), "K");
        Assert.assertEquals(resultIterator.next().getTitle(), "L");
        Assert.assertEquals(resultIterator.next().getTitle(), "M");
        Assert.assertEquals(resultIterator.next().getTitle(), "N");
        Assert.assertEquals(resultIterator.next().getTitle(), "O");
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
        criteria.add(Restrictions.eq("@jcr:primaryType", "mgnl:content"));
        criteria.addOrder(Order.asc("@title"));
        criteria.setPaging(8, 4);
        AdvancedResult advResult = criteria.execute();

        Assert.assertEquals(advResult.getTotalSize(), 26);

        ResultIterator<AdvancedResultItem> resultIterator = advResult.getItems();

        Assert.assertEquals(resultIterator.getSize(), 2);
        Assert.assertEquals(resultIterator.next().getTitle(), "Y");
        Assert.assertEquals(resultIterator.next().getTitle(), "Z");
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
        criteria.add(Restrictions.eq("@jcr:primaryType", "mgnl:content"));
        criteria.addOrder(Order.asc("@title"));
        criteria.setPaging(5, 1);

        AdvancedResult advResult = criteria.execute();
        Assert.assertEquals(advResult.getTotalSize(), 26);
        Assert.assertEquals(advResult.getNumberOfPages(), 6);

        ResultIterator<AdvancedResultItem> resultIterator = advResult.getItems();
        Assert.assertEquals(resultIterator.getSize(), 5);
    }
}
