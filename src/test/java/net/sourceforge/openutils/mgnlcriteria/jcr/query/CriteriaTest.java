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

package net.sourceforge.openutils.mgnlcriteria.jcr.query;

import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.cms.i18n.DefaultI18nContentSupport;
import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.cms.security.MgnlRoleManager;
import info.magnolia.cms.security.Realm;
import info.magnolia.cms.security.SecuritySupport;
import info.magnolia.cms.security.SecuritySupportImpl;
import info.magnolia.cms.security.SystemUserManager;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.MetaDataUtil;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.test.ComponentsTestUtil;
import it.openutils.mgnlutils.test.RepositoryTestConfiguration;
import it.openutils.mgnlutils.test.TestNgRepositoryTestcase;

import java.util.Calendar;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Order;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.criterion.Restrictions;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.xpath.utils.XPathTextUtils;
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
    "/crit-bootstrap/website.pets.xml",
    "/crit-bootstrap/userroles.anonymous.xml",
    "/crit-bootstrap/users.system.anonymous.xml" })
public class CriteriaTest extends TestNgRepositoryTestcase
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
        MgnlContext.getJCRSession(RepositoryConstants.WEBSITE).save();

        ComponentsTestUtil.setInstance(I18nContentSupport.class, new DefaultI18nContentSupport());

        // info.magnolia.cms.security.SecurityTest.setUp()
        final SecuritySupportImpl sec = new SecuritySupportImpl();
        sec.addUserManager(Realm.REALM_SYSTEM.getName(), new SystemUserManager());
        sec.setRoleManager(new MgnlRoleManager());
        ComponentsTestUtil.setInstance(SecuritySupport.class, sec);
    }

    /**
     * Tests the xpath query statement produced by a Criteria instance.
     * @throws Exception
     */
    @Test
    public void testToXpathExpression() throws Exception
    {
        Criteria criteria = toXpathExpressionJavadocExampleCriteria();

        Calendar begin = Calendar.getInstance();
        begin.set(1999, Calendar.JANUARY, 1);
        begin.set(Calendar.HOUR_OF_DAY, 0);
        begin.set(Calendar.MINUTE, 0);
        begin.set(Calendar.SECOND, 0);
        begin.set(Calendar.MILLISECOND, 0);
        Calendar end = Calendar.getInstance();
        end.set(2001, Calendar.DECEMBER, 31);
        end.set(Calendar.HOUR_OF_DAY, 0);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 0);
        end.add(Calendar.MILLISECOND, -1);
        end.add(Calendar.DAY_OF_YEAR, 1);

        String expectedStmt = "//pets//*"
            + "[((jcr:contains(@title, 'Lucky')) and (@petType='dog')"
            + " and (@birthDate >=xs:dateTime('"
            + XPathTextUtils.toXsdDate(begin)
            + "')"
            + " and @birthDate <=xs:dateTime('"
            + XPathTextUtils.toXsdDate(end)
            + "')))]"
            + " order by @title descending";

        log.debug(expectedStmt);

        // @birthDate >=xs:dateTime('1999-01-01T00:00:00.000+01:00')
        // and
        // @birthDate <=xs:dateTime('2001-12-31T23:59:59.999+01:00')

        String actualStmt = criteria.toXpathExpression();

        Assert.assertEquals(StringUtils.remove(actualStmt, ' '), StringUtils.remove(expectedStmt, ' '));
    }

    /**
     * @throws Exception
     */
    @Test
    public void testExecuteTrivial() throws Exception
    {
        // Session hm = MgnlContext.getJCRSession(RepositoryConstants.WEBSITE);
        // Content node = hm.getContent("/pets");
        // Assert.assertEquals(CriteriaTestUtils.title(node), "Pets");

        Criteria criteria = JCRCriteriaFactory
            .createCriteria()
            .setWorkspace(RepositoryConstants.WEBSITE)
            .setBasePath("/jcr:root/*")
            .add(Restrictions.eq("@jcr:primaryType", MgnlNodeType.NT_CONTENT))
            .add(Restrictions.eq("@title", "Pets"));
        AdvancedResult result = criteria.execute();
        ResultIterator<AdvancedResultItem> iterator = result.getItems();
        Assert.assertTrue(iterator.hasNext());
        AdvancedResultItem resultNode = iterator.next();
        Assert.assertEquals(CriteriaTestUtils.title(resultNode), "Pets");
    }

    /**
     * Tests the query execution of a Criteria instance.
     * @throws Exception
     */
    @Test
    public void testExecute() throws Exception
    {
        Criteria criteria = toXpathExpressionJavadocExampleCriteria();

        AdvancedResult result = criteria.execute();
        Assert.assertEquals(result.getTotalSize(), 1);

        ResultIterator<AdvancedResultItem> iterator = result.getItems();
        Assert.assertEquals(iterator.getSize(), 1);
        Assert.assertEquals(CriteriaTestUtils.name(iterator.next()), "12");
    }

    /**
     * @return
     */
    private Criteria toXpathExpressionJavadocExampleCriteria()
    {
        Calendar begin = Calendar.getInstance();
        begin.set(1999, Calendar.JANUARY, 1);
        Calendar end = Calendar.getInstance();
        end.set(2001, Calendar.DECEMBER, 31);

        Criteria criteria = JCRCriteriaFactory
            .createCriteria()
            .setWorkspace(RepositoryConstants.WEBSITE)
            .setBasePath("/pets")
            .add(Restrictions.contains("@title", "Lucky"))
            .add(Restrictions.eq("@petType", "dog"))
            .add(Restrictions.betweenDates("@birthDate", begin, end))
            .addOrder(Order.desc("@title"));
        return criteria;
    }

    /**
     * Tests pagination of results.
     * @throws Exception
     */
    @Test
    public void testSetFirstResultAndMaxResults() throws Exception
    {
        Calendar begin = Calendar.getInstance();
        begin.set(1999, Calendar.JANUARY, 1);
        Calendar end = Calendar.getInstance();
        end.set(2001, Calendar.DECEMBER, 31);

        Criteria criteria = JCRCriteriaFactory
            .createCriteria()
            .setWorkspace(RepositoryConstants.WEBSITE)
            .setBasePath("/pets")
            .add(Restrictions.betweenDates("@birthDate", begin, end))
            .addOrder(Order.asc("@birthDate"))
            .setFirstResult(5)
            .setMaxResults(5);

        AdvancedResult result = criteria.execute();
        // first page:
        // --- 9 (title=Lucky, petType=bird, birthDate=1999-08-06)
        // --- 6 (title=George, petType=snake, birthDate=2000-01-20)
        // --- 4 (title=Jewel, petType=dog, birthDate=2000-03-07)
        // --- 11 (title=Freddy, petType=bird, birthDate=2000-03-09)
        // --- 12 (title=Lucky, petType=dog, birthDate=2000-06-24)
        // second page:
        // --- 1 (title=Leo, petType=cat, birthDate=2000-09-07)
        // --- 5 (title=Iggy, petType=lizard, birthDate=2000-11-30)
        // --- 3 (title=Rosy, petType=dog, birthDate=2001-04-17)
        Assert.assertEquals(result.getTotalSize(), 8);

        ResultIterator<AdvancedResultItem> iterator = result.getItems();
        Assert.assertEquals(iterator.getSize(), 3);
        Assert.assertEquals(CriteriaTestUtils.name(iterator.next()), "1");
        Assert.assertEquals(CriteriaTestUtils.name(iterator.next()), "5");
        Assert.assertEquals(CriteriaTestUtils.name(iterator.next()), "3");
    }

    /**
     * Tests pagination of results.
     * @throws Exception
     */
    @Test
    public void testSetPaging() throws Exception
    {
        Calendar begin = Calendar.getInstance();
        begin.set(1999, Calendar.JANUARY, 1);
        Calendar end = Calendar.getInstance();
        end.set(2001, Calendar.DECEMBER, 31);

        Criteria criteria = JCRCriteriaFactory
            .createCriteria()
            .setWorkspace(RepositoryConstants.WEBSITE)
            .setBasePath("/pets")
            .add(Restrictions.betweenDates("@birthDate", begin, end))
            .addOrder(Order.asc("@birthDate"))
            .setPaging(5, 2);

        AdvancedResult result = criteria.execute();
        // first page:
        // --- 9 (title=Lucky, petType=bird, birthDate=1999-08-06)
        // --- 6 (title=George, petType=snake, birthDate=2000-01-20)
        // --- 4 (title=Jewel, petType=dog, birthDate=2000-03-07)
        // --- 11 (title=Freddy, petType=bird, birthDate=2000-03-09)
        // --- 12 (title=Lucky, petType=dog, birthDate=2000-06-24)
        // second page:
        // --- 1 (title=Leo, petType=cat, birthDate=2000-09-07)
        // --- 5 (title=Iggy, petType=lizard, birthDate=2000-11-30)
        // --- 3 (title=Rosy, petType=dog, birthDate=2001-04-17)
        Assert.assertEquals(result.getTotalSize(), 8);

        ResultIterator<AdvancedResultItem> iterator = result.getItems();
        Assert.assertEquals(iterator.getSize(), 3);
        Assert.assertEquals(CriteriaTestUtils.name(iterator.next()), "1");
        Assert.assertEquals(CriteriaTestUtils.name(iterator.next()), "5");
        Assert.assertEquals(CriteriaTestUtils.name(iterator.next()), "3");
    }

    /**
     * Tests ordering, both ascending and descending.
     * @throws Exception
     */
    @Test
    public void testAddOrder() throws Exception
    {
        Criteria criteria;
        ResultIterator<AdvancedResultItem> iterator;
        Calendar birthDate;

        // gets the oldest pet (ascending order)
        criteria = JCRCriteriaFactory
            .createCriteria()
            .setWorkspace(RepositoryConstants.WEBSITE)
            .setBasePath("/pets")
            .add(Restrictions.isNotNull("@petType"))
            .addOrder(Order.asc("@birthDate"));
        iterator = criteria.execute().getItems();
        // ----- 7 (title=Samantha, petType=cat, birthDate=1995-09-04)
        // ----- 8 (title=Max, petType=cat, birthDate=1995-09-04)
        Assert.assertTrue(iterator.hasNext());
     
        birthDate = PropertyUtil.getDate(iterator.next(), "birthDate", null);
        Assert.assertEquals(birthDate.get(Calendar.YEAR), 1995);
        Assert.assertEquals(birthDate.get(Calendar.MONTH) + 1, 9);
        Assert.assertEquals(birthDate.get(Calendar.DAY_OF_MONTH), 4);

        // gets the youngest pet (descending order)
        criteria = JCRCriteriaFactory
            .createCriteria()
            .setWorkspace(RepositoryConstants.WEBSITE)
            .setBasePath("/pets")
            .add(Restrictions.isNotNull("@petType"))
            .addOrder(Order.desc("@birthDate"));
        iterator = criteria.execute().getItems();
        // ----- 2 (title=Basil, petType=hamster, birthDate=2002-08-06)
        Assert.assertTrue(iterator.hasNext());
        birthDate = PropertyUtil.getDate(iterator.next(), "birthDate", null);
        Assert.assertEquals(birthDate.get(Calendar.YEAR), 2002);
        Assert.assertEquals(birthDate.get(Calendar.MONTH) + 1, 8);
        Assert.assertEquals(birthDate.get(Calendar.DAY_OF_MONTH), 6);
    }

    /**
     * Tests multiple ordering, playing on the fact that the two oldests pets are born on the same date but have
     * different name.
     * @throws Exception
     */
    @Test
    public void testAddOrderMultiple() throws Exception
    {
        Criteria criteria;
        ResultIterator<AdvancedResultItem> iterator;

        // order by @birthDate ascending, @title ascending
        criteria = JCRCriteriaFactory
            .createCriteria()
            .setWorkspace(RepositoryConstants.WEBSITE)
            .setBasePath("/pets")
            .add(Restrictions.isNotNull("@petType"))
            .addOrder(Order.asc("@birthDate"))
            .addOrder(Order.asc("@title"));
        iterator = criteria.execute().getItems();
        // ----- 8 (title=Max, petType=cat, birthDate=1995-09-04)
        // ----- 7 (title=Samantha, petType=cat, birthDate=1995-09-04)
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(CriteriaTestUtils.title(iterator.next()), "Max");
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(CriteriaTestUtils.title(iterator.next()), "Samantha");

        // order by @birthDate ascending, @title descending
        criteria = JCRCriteriaFactory
            .createCriteria()
            .setWorkspace(RepositoryConstants.WEBSITE)
            .setBasePath("/pets")
            .add(Restrictions.isNotNull("@petType"))
            .addOrder(Order.asc("@birthDate"))
            .addOrder(Order.desc("@title"));
        iterator = criteria.execute().getItems();
        // ----- 7 (title=Samantha, petType=cat, birthDate=1995-09-04)
        // ----- 8 (title=Max, petType=cat, birthDate=1995-09-04)
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(CriteriaTestUtils.title(iterator.next()), "Samantha");
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(CriteriaTestUtils.title(iterator.next()), "Max");
    }

    @Test
    public void testDateComparison() throws Exception
    {
        Criteria criteria;
        ResultIterator<AdvancedResultItem> iterator;
        AdvancedResultItem node;
        Calendar date;

        criteria = JCRCriteriaFactory
            .createCriteria()
            .setWorkspace(RepositoryConstants.WEBSITE)
            .setBasePath("/pets")
            .add(Restrictions.isNotNull("@petType"))
            .add(Restrictions.eq("@title", "Leo"));
        iterator = criteria.execute().getItems();
        Assert.assertTrue(iterator.hasNext());
        node = iterator.next();
        Assert.assertEquals(CriteriaTestUtils.title(node), "Leo");

        date = (Calendar) MetaDataUtil.getMetaData(node).getCreationDate().clone();
        date.add(Calendar.DAY_OF_YEAR, 1);
        criteria = JCRCriteriaFactory
            .createCriteria()
            .setWorkspace(RepositoryConstants.WEBSITE)
            .setBasePath("/pets")
            .add(Restrictions.isNotNull("@petType"))
            .add(Restrictions.eq("@title", "Leo"));
        criteria.add(Restrictions.lt("MetaData/@mgnl:creationdate", date));
        iterator = criteria.execute().getItems();
        Assert.assertTrue(iterator.hasNext());
        node = iterator.next();
        Assert.assertEquals(CriteriaTestUtils.title(node), "Leo");

        date = (Calendar) MetaDataUtil.getMetaData(node).getCreationDate().clone();
        date.add(Calendar.HOUR, 1);
        criteria = JCRCriteriaFactory
            .createCriteria()
            .setWorkspace(RepositoryConstants.WEBSITE)
            .setBasePath("/pets")
            .add(Restrictions.isNotNull("@petType"))
            .add(Restrictions.eq("@title", "Leo"));
        criteria.add(Restrictions.lt("MetaData/@mgnl:creationdate", date));
        iterator = criteria.execute().getItems();
        Assert.assertTrue(iterator.hasNext());
        node = iterator.next();
        Assert.assertEquals(CriteriaTestUtils.title(node), "Leo");
    }

}
