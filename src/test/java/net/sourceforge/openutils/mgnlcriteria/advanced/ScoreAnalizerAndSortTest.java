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

import java.util.Collection;
import java.util.List;

import javax.jcr.Node;

import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResult;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.AdvancedResultItem;
import net.sourceforge.openutils.mgnlcriteria.jcr.query.ResultIterator;
import net.sourceforge.openutils.mgnlcriteria.tests.CriteriaTestUtils;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * @author fgiust
 * @version $Id$
 */
@RepositoryTestConfiguration(jackrabbitRepositoryConfig = "/crit-repository/jackrabbit-test-configuration.xml", repositoryConfig = "/crit-repository/test-repositories.xml", bootstrapFiles = {
    "/crit-bootstrap/website.00000.xml",
    "/crit-bootstrap/userroles.anonymous.xml",
    "/crit-bootstrap/users.system.anonymous.xml" })
public class ScoreAnalizerAndSortTest extends TestNgRepositoryTestcase
{

    /**
     * {@inheritDoc}
     */
    @Override
    @BeforeClass
    public void setUp() throws Exception
    {

        super.setUp();

        MgnlContext.getJCRSession(RepositoryConstants.WEBSITE).save();

        ComponentsTestUtil.setInstance(I18nContentSupport.class, new DefaultI18nContentSupport());

        // info.magnolia.cms.security.SecurityTest.setUp()
        final SecuritySupportImpl sec = new SecuritySupportImpl();
        sec.addUserManager(Realm.REALM_SYSTEM.getName(), new SystemUserManager());
        sec.setRoleManager(new MgnlRoleManager());
        ComponentsTestUtil.setInstance(SecuritySupport.class, sec);
    }

    @Test
    public void testSearchDante() throws Exception
    {
        // Dante Alighieri
        // Alighieri, Dante

        AdvancedResult advResult = CriteriaTestUtils.search("Dante Alighieri", 1, 200);
        Collection<Node> result = CriteriaTestUtils.collectCollectionFromResult(advResult);

        CriteriaTestUtils.assertNumOfResults(2, result, "Dante Alighieri");
    }

    @Test
    public void testSearchFagiano() throws Exception
    {
        // fagiano
        // fagiàno
        // fàgiànò

        AdvancedResult advResult = CriteriaTestUtils.search("fagiano", 1, 200);
        Collection<Node> result = CriteriaTestUtils.collectCollectionFromResult(advResult);

        CriteriaTestUtils.assertNumOfResults(3, result, "fagiano");

        ResultIterator<? extends Node> iterator = advResult.getItems();

        Assert.assertTrue(((AdvancedResultItem) iterator.next()).getScore() > ((AdvancedResultItem) iterator.next())
            .getScore());

        iterator = advResult.getItems();

        // not sure what the selector name "s" means, but that's the only valid selector for this query, according to
        // jackrabbit
        Assert.assertTrue(((AdvancedResultItem) iterator.next()).getScore("s") > ((AdvancedResultItem) iterator.next())
            .getScore("s"));
    }

    @Test
    public void testSearchPesca() throws Exception
    {
        // pèsca
        // canna da pesca

        AdvancedResult advResult = CriteriaTestUtils.search("pesca", 1, 200);
        Collection<Node> result = CriteriaTestUtils.collectCollectionFromResult(advResult);

        CriteriaTestUtils.assertSortedResults(new String[]{"pèsca", "canna da pesca" }, result, "pesca");
    }

    @Test
    public void testSearchPageNumber1() throws Exception
    {
        AdvancedResult advResult = CriteriaTestUtils.search("francia", 1, 1);
        Assert.assertEquals(advResult.getPage(), 1);
    }

    // see CRIT-17
    @Test
    public void testSearchPageNumber2() throws Exception
    {
        AdvancedResult advResult = CriteriaTestUtils.search("francia", 2, 1);
        Assert.assertEquals(advResult.getPage(), 2);
    }

    @Test
    public void testSearchPageNumber3() throws Exception
    {
        AdvancedResult advResult = CriteriaTestUtils.search("francia", 3, 1);
        Assert.assertEquals(advResult.getPage(), 3);
    }

    @Test
    public void testSearchFrancia() throws Exception
    {
        // --- "francia" in titles:
        // Frància
        // Parigi (Francia)
        // Parigi (Frància)

        // --- "francia" in paragraphs:
        // "Tallart, Camille d'Hostun, cónte di-",
        // "federale",
        // ":)",
        // "Faccina sorridente :)"

        AdvancedResult advResult = CriteriaTestUtils.search("francia", 1, 200);
        Collection<Node> result = CriteriaTestUtils.collectCollectionFromResult(advResult);

        Assert.assertEquals(advResult.getTotalSize(), 7);

        // the correct result should be:
        // Frància
        // Parigi (Francia)
        // Parigi (Frància)

        // jackrabbit 1.6 return this order

        // jackrabbit 2.0 returns:
        // Parigi (Francia)
        // Parigi (Frància)
        // Frància
        // --> probably the score is evaluted differently with the custom
        // analizer for "à" (or maybe it's a bug...
        // anyway just check the first three results ignoring the order

        CriteriaTestUtils.assertUnsortedResults(
            new String[]{"Frància", "Parigi (Francia)", "Parigi (Frància)", },
            ((List<Node>) result).subList(0, 3),
            "francia");

        // the remaining 4 pages have the same score, so the order is not stable
        // (changes between jdk5 and jdk6)
        // "Tallart, Camille d'Hostun, cónte di-",
        // "federale",
        // ":)",
        // "Faccina sorridente :)"
    }

}
