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
import java.util.Collections;
import java.util.List;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResultItem;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.Criteria;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRCriteriaFactory;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.JCRQueryException;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.ResultIterator;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Order;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Restrictions;
import net.sourceforge.openutils.mgnlcriteria.tests.CriteriaTestUtils;

import org.apache.commons.lang.StringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * @author dschivo
 * @version $Id$
 */
@RepositoryTestConfiguration(jackrabbitRepositoryConfig = "/crit-repository/jackrabbit-test-configuration.xml", repositoryConfig = "/crit-repository/test-repositories.xml", bootstrapFiles = {
    "/crit-bootstrap/website.contains.xml",
    "/crit-bootstrap/userroles.anonymous.xml",
    "/crit-bootstrap/users.system.anonymous.xml" })
public class JcrContainsCriteriaSearchTest extends TestNgRepositoryTestcase
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
        MgnlContext.getJCRSession(RepositoryConstants.WEBSITE).save();

        ComponentsTestUtil.setInstance(I18nContentSupport.class, new DefaultI18nContentSupport());

        // info.magnolia.cms.security.SecurityTest.setUp()
        final SecuritySupportImpl sec = new SecuritySupportImpl();
        sec.addUserManager(Realm.REALM_SYSTEM.getName(), new SystemUserManager());
        sec.setRoleManager(new MgnlRoleManager());
        ComponentsTestUtil.setInstance(SecuritySupport.class, sec);
    }

    @Test
    public void testLoremAndIpsum() throws Exception
    {
        Criteria criteria = criteria("lorem ipsum", false);
        AdvancedResult advResult = criteria.execute();
        Assert.assertNotNull(advResult);
        Assert.assertEquals(advResult.getTotalSize(), 1);
        ResultIterator<AdvancedResultItem> items = advResult.getItems();
        AdvancedResultItem item = items.next();
        Assert.assertEquals(CriteriaTestUtils.title(item), "lorem ipsum");
    }

    @Test
    public void testLoremAndNotIpsum() throws Exception
    {
        Criteria criteria = criteria("lorem -ipsum", false);
        AdvancedResult advResult = criteria.execute();
        Assert.assertNotNull(advResult);
        Assert.assertEquals(advResult.getTotalSize(), 1);
        ResultIterator<AdvancedResultItem> items = advResult.getItems();
        AdvancedResultItem item = items.next();
        Assert.assertEquals(CriteriaTestUtils.title(item), "lorem");
    }

    @Test
    public void testIpsumOrAmet() throws Exception
    {
        Criteria criteria = criteria("ipsum OR amet", false);
        AdvancedResult advResult = criteria.execute();
        Assert.assertNotNull(advResult);
        Assert.assertEquals(advResult.getTotalSize(), 2);
        ResultIterator<AdvancedResultItem> items = advResult.getItems();
        List<String> titles = new ArrayList<String>();
        while (items.hasNext())
        {
            titles.add(CriteriaTestUtils.title(items.next()));
        }
        Collections.sort(titles);
        Assert.assertEquals(titles.get(0), "dolor sit amet");
        Assert.assertEquals(titles.get(1), "lorem ipsum");
    }

    @Test
    public void testIpsumOrSitAndAmet() throws Exception
    {
        // Criteria criteria = criteria("ipsum OR \"sit AND amet\"", false);
        // AND is not a lucene keywords, but a stop word which is stripped when using the default lucene analyzer
        // search terms are ANDed by default
        Criteria criteria = criteria("ipsum OR \"sit amet\"", false);
        AdvancedResult advResult = criteria.execute();
        Assert.assertNotNull(advResult);
        Assert.assertEquals(advResult.getTotalSize(), 2);
        ResultIterator<AdvancedResultItem> items = advResult.getItems();
        List<String> titles = new ArrayList<String>();
        while (items.hasNext())
        {
            titles.add(CriteriaTestUtils.title(items.next()));
        }
        Collections.sort(titles);
        Assert.assertEquals(titles.get(0), "dolor sit amet");
        Assert.assertEquals(titles.get(1), "lorem ipsum");
    }

    @Test
    public void testTest1() throws Exception
    {
        String textEnteredByUser = "test?";
        Criteria criteria = criteria(textEnteredByUser, true);
        Assert.assertEquals(
            StringUtils.remove(criteria.toXpathExpression(), ' '),
            "//*[((@jcr:primaryType='mgnl:content')and(jcr:contains(@title,'test\\?')))]orderby@jcr:scoredescending");
        AdvancedResult advResult = null;
        try
        {
            advResult = criteria.execute();
        }
        catch (JCRQueryException e)
        {
            Assert.fail("Invalid query. " + e.getMessage());
        }
        Assert.assertNotNull(advResult);
        Assert.assertEquals(advResult.getTotalSize(), 1);
        ResultIterator<AdvancedResultItem> items = advResult.getItems();
        AdvancedResultItem item = items.next();
        Assert.assertEquals(CriteriaTestUtils.title(item), "hello test? world");
    }

    @Test
    public void testTest2() throws Exception
    {
        String textEnteredByUser = "te?st";
        Criteria criteria = criteria(textEnteredByUser, true);
        Assert.assertEquals(
            StringUtils.remove(criteria.toXpathExpression(), ' '),
            "//*[((@jcr:primaryType='mgnl:content')and(jcr:contains(@title,'te\\?st')))]orderby@jcr:scoredescending");
        AdvancedResult advResult = null;
        try
        {
            advResult = criteria.execute();
        }
        catch (JCRQueryException e)
        {
            Assert.fail("Invalid query. " + e.getMessage());
        }
        Assert.assertNotNull(advResult);
        Assert.assertEquals(advResult.getTotalSize(), 1);
        ResultIterator<AdvancedResultItem> items = advResult.getItems();
        AdvancedResultItem item = items.next();
        Assert.assertEquals(CriteriaTestUtils.title(item), "hello te?st world");
    }

    @Test
    public void testMilano() throws Exception
    {
        String textEnteredByUser = "\"Milano\"";
        Criteria criteria = criteria(textEnteredByUser, true);
        Assert
            .assertEquals(
                StringUtils.remove(criteria.toXpathExpression(), ' '),
                "//*[((@jcr:primaryType='mgnl:content')and(jcr:contains(@title,'\\\"Milano\\\"')))]orderby@jcr:scoredescending");
        AdvancedResult advResult = null;
        try
        {
            advResult = criteria.execute();
        }
        catch (JCRQueryException e)
        {
            Assert.fail("Invalid query. " + e.getMessage());
        }
        Assert.assertNotNull(advResult);
        Assert.assertEquals(advResult.getTotalSize(), 1);
        ResultIterator<AdvancedResultItem> items = advResult.getItems();
        AdvancedResultItem item = items.next();
        Assert.assertEquals(CriteriaTestUtils.title(item), "hello \"Milano\" world");
    }

    private Criteria criteria(String titleSearch, boolean escape)
    {
        Criteria criteria = JCRCriteriaFactory.createCriteria().setWorkspace(RepositoryConstants.WEBSITE);
        criteria.setBasePath(StringUtils.EMPTY);
        criteria.add(Restrictions.eq("@jcr:primaryType", "mgnl:content"));
        criteria.add(Restrictions.contains("@title", titleSearch, escape));
        criteria.addOrder(Order.desc("@jcr:score"));
        return criteria;
    }
}
